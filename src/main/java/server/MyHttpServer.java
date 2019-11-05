package server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map.Entry;

public class MyHttpServer {
    private HttpServer httpServer;
    private HttpHandlers httpHandlers;
    private String path;

    public static final String FILE_LOG = "log";
    public static final String DIRECTORY_LOGS = "/logs";
    public static final String DIRECTORY_ARCHIVE = "/archive";

    private static final Logger LOGGER = LogManager.getLogger(MyHttpServer.class);

    public MyHttpServer(String path) throws IOException {
        this.path = path;
        httpServer = HttpServer.create();
        httpHandlers = new HttpHandlers(this);
    }

    public void createHttpContexts() {
        for(Entry<String, HttpHandler> entry : httpHandlers.getHttpHandlersMap().entrySet()) {
            httpServer.createContext(entry.getKey(), entry.getValue());
        }
    }

    public void start() {
        httpServer.start();
        LOGGER.log(Level.INFO, "Server started");
    }

    public void stop() {
        httpServer.stop(0);
        LOGGER.log(Level.INFO, "Server stopped");
    }

    public String getPath() {
        return path;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }
}
