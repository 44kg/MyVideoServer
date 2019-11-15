package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHttpServer;
import server.StreamHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HttpHandlerLogs extends MyHttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerLogs.class);

    public HttpHandlerLogs(MyHttpServer myHttpServer) {
        super(myHttpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String string = getMyHttpServer().getPath() + HttpConstants.LOGS;
        if (new File(string).exists()) {
            try {
                File logPath = new File(getMyHttpServer().getPath() + HttpConstants.LOGS);
                File zipFile = new File(getMyHttpServer().getPath() + MyHttpServer.FILE_LOG + ".zip");
                createZipLog(logPath, zipFile);

                httpExchange.getResponseHeaders().add(HttpConstants.CONTENT_TYPE_KEY, HttpConstants.CONTENT_TYPE_VALUE);
                httpExchange.sendResponseHeaders(200, zipFile.length());
                StreamHandler.transmitData(new FileInputStream(zipFile), httpExchange.getResponseBody());
            }
            catch (IOException e) {
                LOGGER.log(Level.ERROR, "Log file sending error", e);
                sendErrorResponse(httpExchange, "Ошибка при скачивании файла.");
            }
        }
        else {
            LOGGER.log(Level.ERROR, "No directory: " + string);
            sendErrorResponse(httpExchange, "Директории " + string + " не существует");
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
}
