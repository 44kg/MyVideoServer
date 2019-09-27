package server;

import com.sun.net.httpserver.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.logging.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MyHttpServer {
    private static final String PATH = "/home/mainkaif/stn/stn-videoserver";
    private static final Logger LOGGER = Logger.getLogger(MyHttpServer.class.getName());

    public MyHttpServer() throws IOException {
        Handler loggerHandler = new FileHandler(PATH + "/logs/log", true);
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

                StringBuilder builder = new StringBuilder();
                builder.append("<!DOCTYPE html>").append("<html>").append("<head>")
                        .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")
                        .append("<title>stn-videoserver</title>").append("</head>").append("<body>")
                        .append("<H1>stn-videoserver</H1>").append("<H2>Состояние</H2>")
                        .append("<P>Загрузка CPU: " + getCPUload() + "</P>")
                        .append("<P>Свободное место на диске: " + getFreeSpace() + "</P>")
                        .append("<P>Размер архива: " + getArchiveSize() + "</P>")
                        .append("<P>Подключенный камеры: </P>").append("<P>Подключенный клиенты: </P>")
                        .append("</body>").append("</html>");

                byte[] bytes = builder.toString().getBytes();
                try {
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
                    File file = new File(PATH + "/logs/log");
                    FileInputStream in = new FileInputStream(file);
                    File zipFile = new File(PATH + "/logs/log.zip");
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

                    FileInputStream fileInputStream = new FileInputStream(PATH + "/logs/log.zip");
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
//        context.setAuthenticator(new Authenticator() {
//            @Override
//            public Result authenticate(HttpExchange httpExchange) {
//                if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
//                    return new Failure(403);
//                else
//                    return new Success(new HttpPrincipal("c0nst", "realm"));
//            }
//        });

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

        HttpContext contextTest = server.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.getResponseHeaders().add("Accept-encoding", "gzip, deflate, identity");
                httpExchange.getResponseHeaders().add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//                httpExchange.getResponseHeaders().add("Accept-Charset", "utf-8");

//                StringBuilder builder = new StringBuilder();
//                builder.append("<h1>URI: ").append(httpExchange.getRequestURI()).append("</h1>");
//                Headers headers = httpExchange.getRequestHeaders();
//                for (String header : headers.keySet()) {
//                    builder.append("<p>").append(header).append("=")
//                            .append(headers.getFirst(header)).append("</p>");
//                }
//                byte[] bytes = builder.toString().getBytes();

                FileInputStream fileInputStream = new FileInputStream(PATH + "/html/page2.html");
                byte[] bytes = fileInputStream.readAllBytes();

                httpExchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(bytes);
                os.close();

                System.out.println();
                System.out.println("REQUEST:");
                Set<String> headersRequest = httpExchange.getRequestHeaders().keySet();
                for (String header : headersRequest) {
                    System.out.println(header + " = " + httpExchange.getRequestHeaders().getFirst(header));
                }
                System.out.println();
                Set<String> headersResponse = httpExchange.getResponseHeaders().keySet();
                System.out.println("RESPONSE:");
                for (String header : headersResponse) {
                    System.out.println(header + " = " + httpExchange.getResponseHeaders().getFirst(header));
                }

            }
        });

        server.setExecutor(null);
        server.start();
        LOGGER.log(Level.INFO, "Server started.");
    }

    private String getCPUload() {
        StringBuilder builder = runLinuxCommand("ps -aux --sort -pcpu");
        String[] lines = builder.toString().split("\n");
        int index = builder.indexOf("%CPU");
        BigDecimal result = new BigDecimal("0.0");
        for (int i = 1; i < lines.length; i++) {
            result = result.add(new BigDecimal(lines[i].substring(index, index + 4).trim()));
        }
        return result.toString() + "%";
    }

    private String getFreeSpace() {
        String[] lines = runLinuxCommand("df -h /").toString().split("\n");
        String[] parts = lines[1].split("\\s");
        StringBuilder builder = new StringBuilder();
        for (String string : parts) {
            if (!string.equals("")) {
                builder.append(string).append("\n");
            }
        }
        return builder.toString().split("\n")[3];
    }

    private void restartServer() {
        runLinuxCommand("service <stn-videoserver> restart");
    }

    private String getArchiveSize() {
        StringBuilder builder = runLinuxCommand("du --block-size=M " + PATH + "/archive");
        builder.delete(builder.indexOf("M"), builder.length());
        builder.append("MB");
        return builder.toString();
    }

    private StringBuilder runLinuxCommand(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            String s;
            Process p;
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                sb.append(s).append("\n");
            p.waitFor();
            if (p.exitValue() == 1) {
                LOGGER.log(Level.WARNING, "Linux command exit key value: 1");
            }
            p.destroy();
        }
        catch (IOException | InterruptedException e) {
            LOGGER.log(Level.WARNING, null, e);
        }
        return sb;
    }
}
