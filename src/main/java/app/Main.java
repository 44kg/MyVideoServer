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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Main {
    public static final String FILE_LOG = "log";
    public static final String DIRECTORY_LOGS = "/logs";
    public static final String DIRECTORY_ARCHIVE = "/archive";

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
//        String[] db = {args[3], args[4], args[5], args[6]};
//
//        try {
//            createDirectories(args[0]);
//
//            MyHttpServer myHttpServer = new MyHttpServer(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), db);
//            myHttpServer.getHttpServer().setExecutor(Executors.newCachedThreadPool());
//
//            myHttpServer.getHttpServer().bind(new InetSocketAddress(myHttpServer.getPort()), 0);
//
//            myHttpServer.getHttpServer().createContext(HttpConstants.ADMIN, new HttpHandlerAdmin(myHttpServer));
//            myHttpServer.getHttpServer().createContext(HttpConstants.LOGS, new HttpHandlerLogs(myHttpServer));
//            myHttpServer.getHttpServer().createContext(HttpConstants.RESTART, new HttpHandlerRestart(myHttpServer));
//            myHttpServer.getHttpServer().createContext(HttpConstants.STATISTICS, new HttpHandlerStatistics(myHttpServer));
//            myHttpServer.getHttpServer().createContext(HttpConstants.REFERENCE_STATE, new HttpHandlerReferenceState(myHttpServer));
//
//            myHttpServer.getHttpServer().start();
//            LOGGER.log(Level.INFO, "Server started");
//        }
//        catch (IOException e) {
//            LOGGER.log(Level.ERROR, "Server error", e);
//        }
        String path = args[0];
        createDirectories(path);

        try {
            HttpServer server = HttpServer.create();
            DatabaseService dbs = new DatabaseService(args[3], args[4], args[5], args[6]);
            ServerState serverState = new ServerState(dbs, path, Integer.parseInt(args[2]));
            server.bind(new InetSocketAddress(Integer.parseInt(args[1])), 0);

            server.createContext(HttpConstants.ADMIN, new HttpHandlerAdmin(serverState));
            server.createContext(HttpConstants.LOGS, new HttpHandlerLogs(args[0]));
            server.createContext(HttpConstants.RESTART, new HttpHandlerRestart());
            server.createContext(HttpConstants.STATISTICS, new HttpHandlerStatistics(dbs, serverState));
            server.createContext(HttpConstants.REFERENCE_STATE, new HttpHandlerReferenceState(dbs, serverState));

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
