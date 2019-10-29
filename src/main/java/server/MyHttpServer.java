package server;

import com.sun.net.httpserver.*;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MyHttpServer {
    private static final Logger LOGGER = LogManager.getLogger(MyHttpServer.class);

    private HttpServer server;
    private String path;
    private String numberOfCPUs;

    private String resourceAdmin;
    private String resourceError500;

    private static final String ADMIN = "admin";
    private static final String LOGS = "logs";
    private static final String RESTART = "restart";
    private static final String ARCHIVE = "archive";

    private static final String FILE_LOG = "log";
    private static final String FILE_HTML_ADMIN = "admin.html";
    private static final String FILE_HTML_500 = "error500.html";

    private static final String HTML_REPLACE_CPU = "$cpu";
    private static final String HTML_REPLACE_FREE_SPACE = "$freespace";
    private static final String HTML_REPLACE_ARCHIVE_SIZE = "$archivesize";
    private static final String HTML_REPLACE_CAMERAS = "$cameras";
    private static final String HTML_REPLACE_CLIENTS = "$clients";
    private static final String HTML_REPLACE_ERROR_INFO = "$info";

    private static final String ACCEPT_ENCODING_KEY = "Accept-encoding";
    private static final String ACCEPT_ENCODING_VALUE = "gzip, deflate";
    private static final String ACCEPT_KEY = "Accept";
    private static final String ACCEPT_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/zip";

    private static final String COMMAND_CPU_LOAD = "top -b d1 n1";
    private static final String COMMAND_NUM_OF_CPUS = "lscpu";
    private static final String COMMAND_FREE_SPACE = "df -h /";
    private static final String COMMAND_ARCHIVE_SIZE = "du --block-size=M ";

    private static final String COMMAND_CONNECTIONS = "netstat -nap";

    public MyHttpServer(String path) {
        this.path = path;
        numberOfCPUs = parseNumberOfCPUs(runLinuxCommand(COMMAND_NUM_OF_CPUS));
    }

    public void createContexts() {
        server.createContext("/" + ADMIN, httpExchange -> {
            try {
                String string = insertVariablesIntoHtmlAdmin();
                byte[] bytes = string.getBytes();
                httpExchange.getResponseHeaders().add(ACCEPT_ENCODING_KEY, ACCEPT_ENCODING_VALUE);
                httpExchange.getResponseHeaders().add(ACCEPT_KEY, ACCEPT_VALUE);
                httpExchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(bytes);
                os.close();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "html sending error: " + FILE_HTML_ADMIN, e);
                sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
            }
        });

        server.createContext("/" + LOGS, httpExchange -> {
            String string = path + "/" + LOGS;
            if (new File(string).exists()) {
                try {
                    File logPath = new File(path + "/" + LOGS);
                    File zipFile = new File(path + "/" + FILE_LOG + ".zip");
                    createZipLog(logPath, zipFile);

                    byte[] bytes = dataToByteArray(new FileInputStream(zipFile));

                    if (!zipFile.delete()) {
                        LOGGER.log(Level.WARN, "Zip file cannot be deleted");
                    }

                    httpExchange.getResponseHeaders().add(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
                    httpExchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
                catch (IOException e) {
                    LOGGER.log(Level.ERROR, "Log file sending error", e);
                    sendErrorResponse(httpExchange, "Ошибка при скачивании файла.");
                }
            }
            else {
                LOGGER.log(Level.ERROR, "No directory: " + string);
                sendErrorResponse(httpExchange, "Директории " + string + " не существует");
            }
        });

        server.createContext("/" + RESTART, httpExchange -> {
            String message = "Restart server...";
            try {
                httpExchange.getResponseHeaders().add(ACCEPT_ENCODING_KEY, ACCEPT_ENCODING_VALUE);
                httpExchange.getResponseHeaders().add(ACCEPT_KEY, ACCEPT_VALUE);
                httpExchange.sendResponseHeaders(200, message.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(message.getBytes());
                os.close();
            }
            catch (IOException e) {
                LOGGER.log(Level.ERROR, "Restart server error", e);
                sendErrorResponse(httpExchange, "Сервер не был перезапущен.");
            }
            restartServer();
        });
    }

    public void createDirectories() {
        createPath(path + "/" + ARCHIVE);
        createPath(path + "/" + LOGS);
    }

    public void loadResources() {
        resourceAdmin = loadResource(FILE_HTML_ADMIN);
        resourceError500 = loadResource(FILE_HTML_500);
    }

    public void initServer(int port) throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 0);
    }

    public void startServer() {
        server.start();
        LOGGER.log(Level.INFO, "Server started");
    }

    private void createPath(String pathname) {
        File file = new File(pathname);
        if (!file.exists()) {
            if (file.mkdir()) {
                LOGGER.log(Level.INFO, "Directory or file " + pathname + " created");
            }
            else {
                LOGGER.log(Level.WARN, "Missing and not created " + pathname);
            }
        }
    }

    private String loadResource(String resourceFile) {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceFile);
            if (inputStream != null) {
                return new String(dataToByteArray(inputStream), StandardCharsets.UTF_8);
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "Loading resources error", e);
        }
        return null;
    }

    private void sendErrorResponse(HttpExchange httpExchange, String errorInfo) {
        try {
            String string = insertVariablesIntoHtmlError500(errorInfo);
            httpExchange.sendResponseHeaders(500, string.getBytes().length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(string.getBytes());
            os.close();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + FILE_HTML_500, e);
        }
    }

    private String insertVariablesIntoHtmlAdmin() {
        String cpuLoad = parseCpuLoad(runLinuxCommand(COMMAND_CPU_LOAD));
        String freeSpace = parseFreeSpace(runLinuxCommand(COMMAND_FREE_SPACE));
        String archiveSize = parseArchiveSize(runLinuxCommand(COMMAND_ARCHIVE_SIZE + path + "/" + ARCHIVE));
        StringBuilder response = runLinuxCommand(COMMAND_CONNECTIONS);
        String cameras = parseNumberOfConnections(response, "554");
        String clients = parseNumberOfConnections(response, "9000");

        String string = resourceAdmin;
        return string.replace(HTML_REPLACE_CPU, cpuLoad).replace(HTML_REPLACE_FREE_SPACE, freeSpace)
                .replace(HTML_REPLACE_ARCHIVE_SIZE, archiveSize).replace(HTML_REPLACE_CAMERAS, cameras)
                .replace(HTML_REPLACE_CLIENTS, clients);
    }

    private String insertVariablesIntoHtmlError500(String errorInfo) {
        String string = resourceError500;
        return string.replace(HTML_REPLACE_ERROR_INFO, errorInfo);
    }

    private void createZipLog(File path, File zipFile) {
        try {
            if (!zipFile.exists()) {
                if (zipFile.createNewFile()) {
                    if (path.exists()) {
                        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));
                        File[] files = path.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                FileInputStream fis = new FileInputStream(file);
                                zout.putNextEntry(new ZipEntry(file.getName()));
                                zout.write(dataToByteArray(fis));
                                fis.close();
                            }
                        }
                        zout.close();
                    }
                }
                else {
                    LOGGER.log(Level.WARN, "Zip file was not created");
                }
            }
            else {
                if (zipFile.delete()) {
                    createZipLog(path, zipFile);
                }
                else {
                    LOGGER.log(Level.WARN, "Zip file cannot be deleted");
                }
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "Zip file creation error", e);
        }
    }

    public String parseCpuLoad(StringBuilder response) {
        BigDecimal result = new BigDecimal("0.0");
        String[] lines = response.toString().split("\n");
        int index = lines[6].indexOf("%CPU");
        String addendum;
        for (int i = 7; i < lines.length; i++) {
            addendum = lines[i].substring(index, index + 4).trim().replace(",", ".");
            if (addendum.equals("0.0"))
                break;
            result = result.add(new BigDecimal(addendum));
        }
        result = result.divide(new BigDecimal(numberOfCPUs), new MathContext(3));
        return result.toString() + "%";
    }

    private String parseNumberOfCPUs(StringBuilder response) {
        String[] lines = response.toString().split("\n");
        String numberOfCPUs = "";
        for (String line : lines) {
            if (line.startsWith("CPU(s):")) {
                numberOfCPUs = line.substring(7).trim();
                break;
            }
        }
        return numberOfCPUs;
    }

    private String parseFreeSpace(StringBuilder response) {
        String[] lines = response.toString().split("\n");
        String line = lines[1];
        for (int i = 0; i < 3; i++) {
            line = line.substring(line.indexOf(" ")).trim();
        }
        line = line.substring(0, line.indexOf(" "));
        return line + "B";
    }

    private String parseArchiveSize(StringBuilder response) {
        response.delete(response.indexOf("M"), response.length());
        response.append("MB");
        return response.toString();
    }

    private String parseNumberOfConnections(StringBuilder response, String port) {
        String[] lines = response.toString().split("\n");
        int counter = 0;
        for (String line : lines) {
            if (line.startsWith("tcp")) {
                for (int i = 0; i < 4; i++) {
                    line = line.substring(line.indexOf(" ")).trim();
                }
                if (line.substring(line.indexOf(":") + 1, line.indexOf(" ")).equals(port)) {
                    counter++;
                }
            }
            if (line.startsWith("unix")) {
                break;
            }
        }
        return Integer.toString(counter);
    }

    private void restartServer() {
        runLinuxCommand("service restart");
    }

    private StringBuilder runLinuxCommand(String command) {
        StringBuilder builder = new StringBuilder();
        try {
            String line;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null)
                builder.append(line).append("\n");
            process.waitFor();
            if (process.exitValue() == 1) {
                LOGGER.log(Level.WARN, "Linux command: " + command + ". Exit value = 1");
            }
            process.destroy();
        }
        catch (IOException | InterruptedException e) {
            LOGGER.log(Level.ERROR, "Linux command execution error", e);
        }
        return builder;
    }

    private byte[] dataToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) > 0) {
            baos.write(buffer, 0, length);
        }
        inputStream.close();
        baos.close();
        return baos.toByteArray();
    }
}
