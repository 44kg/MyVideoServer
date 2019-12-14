package server.httpHandlers;

import server.*;
import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.html.HtmlParser;
import server.html.HTML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpHandlerAdmin extends MyHttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerAdmin.class);

    public HttpHandlerAdmin(MyHttpServer myHttpServer) {
        super(myHttpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String string = HTML.getHtmlAsString(HTML.ADMIN);

            List<String> list = new ArrayList<>();
            list.add(getMyHttpServer().getServerState().getCpuLoad());
            list.add(getMyHttpServer().getServerState().getFreeSpace());
            list.add(getMyHttpServer().getServerState().getArchiveSize());
            list.add(getMyHttpServer().getServerState().getCameras());
            list.add(getMyHttpServer().getServerState().getClients());

            string = HtmlParser.parseAdmin(string, list);
            sendResponse(httpExchange, string);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + HTML.ADMIN, e);
            sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
        }
    }
}
