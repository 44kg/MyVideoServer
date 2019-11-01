package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HttpHandlers {
    private Map<String, HttpHandler> httpHandlersMap;
    private MyHtml myHtml;

    private static final String ADMIN = "/admin";
    private static final String LOGS = "/logs";
    private static final String RESTART = "/restart";

    private static final String ACCEPT_ENCODING_KEY = "Accept-encoding";
    private static final String ACCEPT_ENCODING_VALUE = "gzip, deflate";
    private static final String ACCEPT_KEY = "Accept";
    private static final String ACCEPT_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/zip";

    private static final Logger LOGGER = LogManager.getLogger(HttpHandlers.class);

    public HttpHandlers(MyHttpServer myHttpServer) {
        httpHandlersMap = new HashMap<>();
        myHtml = new MyHtml();

        httpHandlersMap.put(ADMIN, httpExchange -> {
            try {
                byte[] bytes = myHtml.getHtmlAsByteArray(MyHtml.ADMIN);
                httpExchange.getResponseHeaders().add(ACCEPT_ENCODING_KEY, ACCEPT_ENCODING_VALUE);
                httpExchange.getResponseHeaders().add(ACCEPT_KEY, ACCEPT_VALUE);
                httpExchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(bytes);
                os.close();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.ADMIN, e);
                sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
            }
        });

        httpHandlersMap.put(LOGS, httpExchange -> {
            String string = myHttpServer.getPath() + LOGS;
            if (new File(string).exists()) {
                try {
                    File logPath = new File(myHttpServer.getPath() + LOGS);
                    File zipFile = new File(myHttpServer.getPath() + MyHttpServer.FILE_LOG + ".zip");
                    createZipLog(logPath, zipFile);

                    byte[] bytes = StreamHandler.toByteArray(new FileInputStream(zipFile));

                    if (!zipFile.delete()) {
                        LOGGER.log(Level.WARN, "Zip file cannot be deleted");
                    }

                    httpExchange.getResponseHeaders().add(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
                    httpExchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(bytes);
                    os.close();
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
        });

        httpHandlersMap.put(RESTART, httpExchange -> {
            String message = "Restart server...";
            try {
                httpExchange.getResponseHeaders().add(ACCEPT_ENCODING_KEY, ACCEPT_ENCODING_VALUE);
                httpExchange.getResponseHeaders().add(ACCEPT_KEY, ACCEPT_VALUE);
                httpExchange.sendResponseHeaders(200, message.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(message.getBytes());
                os.close();
            }
            catch (IOException e) {
                LOGGER.log(Level.ERROR, "Restart server error", e);
                sendErrorResponse(httpExchange, "Сервер не был перезапущен.");
            }
            LinuxCommandParser.restartServer();
        });
    }

    private void sendErrorResponse(HttpExchange httpExchange, String errorInfo) {
        try {
            byte[] bytes = myHtml.getHtmlAsByteArray(MyHtml.ERROR_500, errorInfo);
            httpExchange.sendResponseHeaders(500, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.ERROR_500, e);
        }
    }

    private void createZipLog(File path, File zipFile) {
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

    public Map<String, HttpHandler> getHttpHandlersMap() {
        return httpHandlersMap;
    }
}
