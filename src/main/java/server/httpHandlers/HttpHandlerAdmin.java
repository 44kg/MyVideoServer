package server.httpHandlers;

import server.*;
import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.html.HtmlParser;
import server.html.HTML;

import java.util.ArrayList;
import java.util.List;

public class HttpHandlerAdmin extends MyHttpHandler {
    private ServerState serverState;

    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerAdmin.class);

    public HttpHandlerAdmin(ServerState serverState) {
        super();
        this.serverState = serverState;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String string = HTML.getHtmlAsString(HTML.ADMIN);

        List<String> list = new ArrayList<>();
        list.add(serverState.getCpuLoad());
        list.add(serverState.getFreeSpace());
        list.add(serverState.getArchiveSize());
        list.add(serverState.getCameras());
        list.add(serverState.getClients());

        string = HtmlParser.parseAdmin(string, list);
        sendResponse(httpExchange, string);
    }
}
