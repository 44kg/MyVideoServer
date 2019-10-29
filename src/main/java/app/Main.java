package app;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHttpServer;

import java.io.IOException;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            MyHttpServer myHttpServer = new MyHttpServer(args[0]);
            myHttpServer.createDirectories();
            myHttpServer.loadResources();
            myHttpServer.initServer(Integer.parseInt(args[1]));
            myHttpServer.createContexts();
            myHttpServer.startServer();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Server error", e);
        }
    }
}
