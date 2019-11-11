package server.httpHandlers;

import server.HtmlParser;
import server.MyHttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHtml;

import java.io.IOException;
import java.io.OutputStream;

public class HttpHandlerAdmin extends MyHttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerAdmin.class);

    public HttpHandlerAdmin(MyHttpServer myHttpServer) {
        super(myHttpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String string = getMyHttpServer().getMyHtml().getHtmlAsString(MyHtml.ADMIN);
            string = HtmlParser.parseAdmin(string, getMyHttpServer().getPath());

            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_ENCODING_KEY, HttpConstants.ACCEPT_ENCODING_VALUE);
            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_KEY, HttpConstants.ACCEPT_VALUE);
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
            os.write(string.getBytes());
            os.close();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.ADMIN, e);
            sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
        }
    }
}
