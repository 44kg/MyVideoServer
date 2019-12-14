package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHttpServer;
import server.StreamHandler;
import server.html.HtmlParser;
import server.html.HTML;

import java.io.IOException;
import java.util.List;

public class HttpHandlerReferenceState extends MyHttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerReferenceState.class);

    public HttpHandlerReferenceState(MyHttpServer myHttpServer) {
        super(myHttpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
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
                getMyHttpServer().getDatabaseService().updateState(0, cpuLoadRef, freeSpaceRef, archiveSizeRef, clientsRef, camerasRef);
                getMyHttpServer().getServerState().readReferences();
            }

            List<String> list = getMyHttpServer().getServerState().getRefStates();

            string = HtmlParser.parseReferenceState(string, list);
            sendResponse(httpExchange, string);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + HTML.REFERENCE_STATE, e);
            sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
        }
    }
}
