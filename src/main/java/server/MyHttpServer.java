package server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class MyHttpServer {
    private String path;
    private int port;
    private HttpServer httpServer;

    public static final String FILE_LOG = "log";
    public static final String DIRECTORY_LOGS = "/logs";
    public static final String DIRECTORY_ARCHIVE = "/archive";

    public MyHttpServer(String path, int port) throws IOException {
        this.path = path;
        this.port = port;
        httpServer = HttpServer.create();
    }

    public String getPath() {
        return path;
    }

    public int getPort() {
        return port;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }
}
