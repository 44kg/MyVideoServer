package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.ServerState;
import server.StreamHandler;
import server.db.DatabaseService;
import server.html.HtmlParser;
import server.html.HTML;

import java.util.List;

public class HttpHandlerReferenceState extends MyHttpHandler {
    private DatabaseService dbs;
    private ServerState serverState;

    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerReferenceState.class);

    public HttpHandlerReferenceState(DatabaseService dbs, ServerState serverState) {
        super();
        this.dbs = dbs;
        this.serverState = serverState;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String string = HTML.getHtmlAsString(HTML.REFERENCE_STATE);
        String body = StreamHandler.toString(httpExchange.getRequestBody());
        if (!body.equals("")) {
            String[] parts = body.split("&");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].substring(parts[i].indexOf("=") + 1);
            }
            float cpuLoadRef = Float.parseFloat(parts[0]);
            float freeSpaceRef = Float.parseFloat(parts[1]);
            float archiveSizeRef = Float.parseFloat(parts[2]);
            int clientsRef = Integer.parseInt(parts[3]);
            int camerasRef = Integer.parseInt(parts[4]);
            dbs.updateState(0, cpuLoadRef, freeSpaceRef, archiveSizeRef, clientsRef, camerasRef);
            serverState.readReferences();
        }

        List<String> list = serverState.getRefStates();

        string = HtmlParser.parseReferenceState(string, list);
        sendResponse(httpExchange, string);
    }
}
