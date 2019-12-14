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

    private static final Logger LOGGER = LogManager.getLogger(MyHttpHandler.class);

    public MyHttpHandler() {}

    public void sendErrorResponse(HttpExchange httpExchange, String errorInfo) {
        try {
            String string = HTML.getHtmlAsString(HTML.ERROR_500);
            string = HtmlParser.parseError500(string, errorInfo);
            httpExchange.sendResponseHeaders(500, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(string.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + HTML.ERROR_500, e);
        }
    }

    public void sendResponse(HttpExchange httpExchange, String html) throws IOException{
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_ENCODING_KEY, HttpConstants.ACCEPT_ENCODING_VALUE);
        httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_KEY, HttpConstants.ACCEPT_VALUE);
        httpExchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
