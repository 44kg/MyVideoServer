package app;

import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.*;
import server.db.DatabaseService;
import server.httpHandlers.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static final String FILE_LOG = "log";
    public static final String DIRECTORY_LOGS = "/logs";
    public static final String DIRECTORY_ARCHIVE = "/archive";

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        String path = args[0];
        createDirectories(path);

        try {
            HttpServer server = HttpServer.create();
            DatabaseService dbs = new DatabaseService(args[3], args[5], args[6]);
            ServerState serverState = new ServerState(dbs, path, Integer.parseInt(args[2]));
            server.bind(new InetSocketAddress(Integer.parseInt(args[1])), 0);

            server.createContext(MyHttpHandler.ADMIN, new HttpHandlerAdmin(serverState));
            server.createContext(MyHttpHandler.LOGS, new HttpHandlerLogs(args[0]));
            server.createContext(MyHttpHandler.RESTART, new HttpHandlerRestart());
            server.createContext(MyHttpHandler.STATISTICS, new HttpHandlerStatistics(dbs, serverState));
            server.createContext(MyHttpHandler.REFERENCE_STATE, new HttpHandlerReferenceState(dbs, serverState));

            server.start();
            LOGGER.log(Level.INFO, "Server started");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Server error", e);
        }
    }

    public static void createDirectories(String workingPath) {
        createPath(workingPath + DIRECTORY_ARCHIVE);
        createPath(workingPath + DIRECTORY_LOGS);
    }

    public static void createPath(String pathname) {
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
}
