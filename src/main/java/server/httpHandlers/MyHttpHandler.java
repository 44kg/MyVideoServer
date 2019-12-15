package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.html.HtmlParser;
import server.html.HTML;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class MyHttpHandler implements HttpHandler {
    public static final String ADMIN = "/admin";
    public static final String LOGS = "/logs";
    public static final String RESTART = "/restart";
    public static final String STATISTICS = "/statistics";
    public static final String REFERENCE_STATE = "/reference_state";

    public static final String ACCEPT_ENCODING_KEY = "Accept-encoding";
    public static final String ACCEPT_ENCODING_VALUE = "gzip, deflate";
    public static final String ACCEPT_KEY = "Accept";
    public static final String ACCEPT_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    public static final String CONTENT_TYPE_KEY = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "application/zip";

    private static final Logger LOGGER = LogManager.getLogger(MyHttpHandler.class);

    public MyHttpHandler() {}

    public void sendErrorResponse(HttpExchange httpExchange, String errorInfo) {
        OutputStream os = null;
        try {
            String string = HTML.getHtmlAsString(HTML.ERROR_500);
            string = HtmlParser.parseError500(string, errorInfo);
            httpExchange.sendResponseHeaders(500, 0);
            os = httpExchange.getResponseBody();
            os.write(string.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + HTML.ERROR_500, e);
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "Stream closing error", e);
            }
        }
    }

    public void sendResponse(HttpExchange httpExchange, String html) {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add(ACCEPT_ENCODING_KEY, ACCEPT_ENCODING_VALUE);
        httpExchange.getResponseHeaders().add(ACCEPT_KEY, ACCEPT_VALUE);
        try {
            httpExchange.sendResponseHeaders(200, bytes.length);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Response Headers sending error", e);
        }
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(bytes);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error", e);
        }


    }
}
