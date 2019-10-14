package server;

import com.sun.net.httpserver.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.InetSocketAddress;
import java.util.logging.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MyHttpServer {
    private String path;

    private static final Logger LOGGER = Logger.getLogger(MyHttpServer.class.getName());

    public MyHttpServer(String path) throws IOException {
        this.path = path;

        new File(path + "/archive").mkdirs();
        new File(path + "/logs").mkdirs();

        Handler loggerHandler = new FileHandler(path + "/logs/log", true);
        loggerHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(loggerHandler);

        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);

        HttpContext contextAdmin = server.createContext("/admin", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) {
                httpExchange.getResponseHeaders().add("Accept-encoding", "gzip, deflate");
                httpExchange.getResponseHeaders()
                        .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

                try {
                    String cpuLoad = cpuLoad();
                    String freeSpace = freeSpace();
                    String archiveSize = archiveSize();
                    String cameras = numberOfCameras();
                    String clients = numberOfClients();

                    InputStream file = ClassLoader.getSystemResourceAsStream("admin.html");
                    String string = new String(file.readAllBytes(), "UTF-8");

                    string = string.replace("$cpu", cpuLoad).replace("$freespace", freeSpace)
                                .replace("$archivesize", archiveSize).replace("$cameras", cameras)
                                .replace("$clients", clients);

                    byte[] bytes = string.getBytes();
                    httpExchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
                catch (IOException e) {
                    LOGGER.log(Level.WARNING, null, e);
                }
            }
        });

        HttpContext contextLogs = server.createContext("/logs", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) {
                httpExchange.getResponseHeaders().add("Content-Type", "application/zip");
                try {
                    File file = new File(path + "/logs/log");
                    FileInputStream in = new FileInputStream(file);
                    File zipFile = new File(path + "/logs/log.zip");
                    zipFile.createNewFile();
                    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
                    out.putNextEntry(new ZipEntry("log"));

                    byte[] b = new byte[1024];
                    int count;
                    while ((count = in.read(b)) > 0) {
                        out.write(b, 0, count);
                    }
                    out.close();
                    in.close();

                    FileInputStream fileInputStream = new FileInputStream(path + "/logs/log.zip");
                    byte[] bytes = fileInputStream.readAllBytes();
                    zipFile.delete();

                    httpExchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
                catch (IOException e) {
                    LOGGER.log(Level.WARNING, null, e);
                }
            }
        });

        HttpContext contextRestart = server.createContext("/restart", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) {
                httpExchange.getResponseHeaders().add("Accept-encoding", "gzip, deflate");
                httpExchange.getResponseHeaders()
                        .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                String message = "Restart server...";
                try {
                    httpExchange.sendResponseHeaders(200, message.length());
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(message.getBytes());
                    os.close();
                }
                catch (IOException e) {
                    LOGGER.log(Level.WARNING, null, e);
                }
                restartServer();
            }
        });

        server.setExecutor(null);
        server.start();
        LOGGER.log(Level.INFO, "Server started.");
    }

    private String cpuLoad() {
        BigDecimal result = new BigDecimal("0.0");
        StringBuilder builder = runLinuxCommand("ps -aux --sort -pcpu");
        String[] lines = builder.toString().split("\n");
        int index = builder.indexOf("%CPU");
        for (int i = 1; i < lines.length; i++) {
            result = result.add(new BigDecimal(lines[i].substring(index, index + 4).trim()));
        }
        result = result.divide(new BigDecimal(numberOfCPUs()), new MathContext(3));
        return result.toString() + "%";
    }

    // Количество ядер
    private String numberOfCPUs() {
        StringBuilder builder = runLinuxCommand("lscpu");
        String[] lines = builder.toString().split("\n");
        String numberOfCPUs = "";
        for (String line : lines) {
            if (line.startsWith("CPU(s):")) {
                numberOfCPUs = line.substring(7).trim();
                break;
            }
        }
        return numberOfCPUs;
    }

    private String freeSpace() {
        String[] lines = runLinuxCommand("df -h /").toString().split("\n");
        String line = lines[1];
        for (int i = 0; i < 3; i++) {
            line = line.substring(line.indexOf(" ")).trim();
        }
        line = line.substring(0, line.indexOf(" "));
        return line + "B";
    }

    private String archiveSize() {
        StringBuilder builder = runLinuxCommand("du --block-size=M " + path + "/archive");
        builder.delete(builder.indexOf("M"), builder.length());
        builder.append("MB");
        return builder.toString();
    }

    private String numberOfCameras() {
        StringBuilder builder = runLinuxCommand("netstat");
        String[] lines = builder.toString().split("\n");
        int counter = 0;
        for (String line : lines) {
            if (line.startsWith("tcp") && (line.contains("rtsp") || line.contains("554"))) {
                counter++;
            }
        }
        return Integer.toString(counter);
    }

    private String numberOfClients() {
        StringBuilder builder = runLinuxCommand("netstat");
        String[] lines = builder.toString().split("\n");
        int counter = 0;
        for (String line : lines) {
            if (line.startsWith("tcp") && line.contains("9000")) {
                counter++;
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
            Process process;
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null)
                builder.append(line).append("\n");
            process.waitFor();
            if (process.exitValue() == 1) {
                LOGGER.log(Level.WARNING, "Linux command exit key value: 1");
            }
            process.destroy();
        }
        catch (IOException | InterruptedException e) {
            LOGGER.log(Level.WARNING, null, e);
        }
        return builder;
    }
}
