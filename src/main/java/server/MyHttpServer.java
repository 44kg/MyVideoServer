package server;

import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MyHttpServer {
    private String path;
    private int port;
    private HttpServer httpServer;
    private MyHtml myHtml;

    public static final String FILE_LOG = "log";
    public static final String DIRECTORY_LOGS = "/logs";
    public static final String DIRECTORY_ARCHIVE = "/archive";

    private static final Logger LOGGER = LogManager.getLogger(MyHttpServer.class);

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

    public MyHtml getMyHtml() {
        return myHtml;
    }

    public void setMyHtml(MyHtml myHtml) {
        this.myHtml = myHtml;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }
}
