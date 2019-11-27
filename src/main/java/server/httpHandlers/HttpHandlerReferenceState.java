package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHttpServer;
import server.StreamHandler;
import server.html.HtmlParser;
import server.html.MyHtml;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpHandlerReferenceState extends MyHttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerReferenceState.class);

    public HttpHandlerReferenceState(MyHttpServer myHttpServer) {
        super(myHttpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String string = MyHtml.getHtmlAsString(MyHtml.REFERENCE_STATE);
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

            List<String> list = new ArrayList<>();
            list.add(getMyHttpServer().getServerState().getCpuLoadRef());
            list.add(getMyHttpServer().getServerState().getFreeSpaceRef());
            list.add(getMyHttpServer().getServerState().getArchiveSizeRef());
            list.add(getMyHttpServer().getServerState().getCamerasRef());
            list.add(getMyHttpServer().getServerState().getClientsRef());

            string = HtmlParser.parseReferenceState(string, list);
            LOGGER.log(Level.TRACE, string);
            byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_ENCODING_KEY, HttpConstants.ACCEPT_ENCODING_VALUE);
            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_KEY, HttpConstants.ACCEPT_VALUE);
            httpExchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.REFERENCE_STATE, e);
            sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
        }
    }
}
