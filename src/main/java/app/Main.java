package app;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHttpServer2;

import java.io.File;
import java.io.IOException;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        createDirectories(args[0]);
        try {
            MyHttpServer2 myHttpServer = new MyHttpServer2(args[0], Integer.parseInt(args[1]));
            myHttpServer.createHttpContexts();
            myHttpServer.start();
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "Server error", e);
        }
    }

    public static void createDirectories(String workingPath) {
        createPath(workingPath + MyHttpServer2.DIRECTORY_ARCHIVE);
        createPath(workingPath + MyHttpServer2.DIRECTORY_LOGS);
    }

    private static void createPath(String pathname) {
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
