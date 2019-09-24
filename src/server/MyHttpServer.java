package server;

import com.sun.net.httpserver.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;

public class MyHttpServer {
    static final private String PATH = "/usr/local/stn/stn-videoserver";

    public MyHttpServer() throws IOException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);

        HttpContext context = server.createContext("/admin", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                StringBuilder builder = new StringBuilder();
                builder.append("<h1>State:</h1>").append("<p>CPU: " + getCPUload() + "</p>")
                        .append("<p>Free space: " + getFreeSpace() + "B</p>")
                        .append("<p>Archive size: " + getArchiveSize() + "</p>");
                byte[] bytes = builder.toString().getBytes();
                httpExchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(bytes);
                os.close();
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

        HttpContext contextTest = server.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                StringBuilder builder = new StringBuilder();
                builder.append("<h1>URI: ").append(httpExchange.getRequestURI()).append("</h1>");
                Headers headers = httpExchange.getRequestHeaders();
                for (String header : headers.keySet()) {
                    builder.append("<p>").append(header).append("=")
                            .append(headers.getFirst(header)).append("</p>");
                }
                byte[] bytes = builder.toString().getBytes();
                httpExchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        server.setExecutor(null);
        server.start();
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
        String[] lines = runLinuxCommand("df -h").toString().split("\n");
        String line = null;
        for (String string : lines) {
            if (string.startsWith("/dev/sda2")) {
                line = string;
            }
        }
        String[] parts = line.split("\\s");
        StringBuilder builder = new StringBuilder();
        for (String string : parts) {
            if (!string.equals("")) {
                builder.append(string).append("\n");
            }
        }
        return builder.toString().split("\n")[3];
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
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return sb;
    }
}
