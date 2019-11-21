package server;

import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.db.DatabaseService;

import java.io.IOException;

public class MyHttpServer {
    private String path;
    private int port;
    private HttpServer httpServer;
    private ServerState serverState;
    private DatabaseService databaseService;

    public static final String FILE_LOG = "log";
    public static final String DIRECTORY_LOGS = "/logs";
    public static final String DIRECTORY_ARCHIVE = "/archive";

    private static final Logger LOGGER = LogManager.getLogger(MyHttpServer.class);

    public MyHttpServer(String path, int port, int timeRangeSec) {
        this.path = path;
        this.port = port;
        try {
            httpServer = HttpServer.create();
            serverState = new ServerState(this, timeRangeSec);
            databaseService = new DatabaseService();
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "Server creating error", e);
            stop();
        }
    }

    public void stop() {
        serverState.stop();
        httpServer.stop(0);
        LOGGER.log(Level.INFO, "Server stopped");
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

    public ServerState getServerState() {
        return serverState;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }
}
