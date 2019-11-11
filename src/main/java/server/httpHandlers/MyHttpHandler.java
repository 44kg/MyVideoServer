package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHtml;
import server.MyHttpServer;
import server.StreamHandler;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class MyHttpHandler implements HttpHandler {
    private MyHttpServer myHttpServer;

    private static final Logger LOGGER = LogManager.getLogger(MyHttpHandler.class);

    public MyHttpHandler(MyHttpServer myHttpServer) {
        this.myHttpServer = myHttpServer;
    }

    public void sendErrorResponse(HttpExchange httpExchange, String errorInfo) {
        try {
            byte[] bytes = myHttpServer.getMyHtml().getHtmlAsByteArray(MyHtml.ERROR_500, errorInfo);
            httpExchange.sendResponseHeaders(500, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.ERROR_500, e);
        }
    }

    public void createZipLog(File path, File zipFile) {
        try {
            if (!zipFile.exists()) {
                if (zipFile.createNewFile()) {
                    if (path.exists()) {
                        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));
                        File[] files = path.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                FileInputStream fis = new FileInputStream(file);
                                zout.putNextEntry(new ZipEntry(file.getName()));
                                zout.write(StreamHandler.toByteArray(fis));
                                fis.close();
                            }
                        }
                        zout.close();
                    }
                }
                else {
                    LOGGER.log(Level.WARN, "Zip file was not created");
                }
            }
            else {
                if (zipFile.delete()) {
                    createZipLog(path, zipFile);
                }
                else {
                    LOGGER.log(Level.WARN, "Zip file cannot be deleted");
                }
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "Zip file creation error", e);
        }
    }

    public MyHttpServer getMyHttpServer() {
        return myHttpServer;
    }
}