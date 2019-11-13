package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHttpServer;
import server.StreamHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
}
