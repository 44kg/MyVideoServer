package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.HtmlParser;
import server.MyHtml;
import server.MyHttpServer;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class MyHttpHandler implements HttpHandler {
    private MyHttpServer myHttpServer;

    private static final Logger LOGGER = LogManager.getLogger(MyHttpHandler.class);

    public MyHttpHandler(MyHttpServer myHttpServer) {
        this.myHttpServer = myHttpServer;
    }

    public void sendErrorResponse(HttpExchange httpExchange, String errorInfo) {
        try {
            String string = MyHtml.getHtmlAsString(MyHtml.ERROR_500);
            string = HtmlParser.parseError500(string, errorInfo);
            httpExchange.sendResponseHeaders(500, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(string.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.ERROR_500, e);
        }
    }

    public MyHttpServer getMyHttpServer() {
        return myHttpServer;
    }
}
