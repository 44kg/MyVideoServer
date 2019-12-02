package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHttpServer;
import server.StreamHandler;
import server.db.DatabaseService;
import server.html.HtmlParser;
import server.html.MyHtml;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class HttpHandlerStatistics extends MyHttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerStatistics.class);

    public HttpHandlerStatistics(MyHttpServer myHttpServer) {
        super(myHttpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String string = MyHtml.getHtmlAsString(MyHtml.STATISTICS);
            String body = StreamHandler.toString(httpExchange.getRequestBody());
            LOGGER.log(Level.TRACE, body);
            if (!body.equals("")) {
                String[] parts = body.split("&");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].substring(parts[i].indexOf("=") + 1);
                }
                LOGGER.log(Level.TRACE, parts[0]);
                LOGGER.log(Level.TRACE, parts[1]);
                List<List<String>> table = getMyHttpServer().getDatabaseService().selectState(Date.valueOf(parts[0]), Date.valueOf(parts[1]));
                List<String> avgStates = new ArrayList<>();
                LOGGER.log(Level.TRACE, avgStates.size());
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_CPU_LOAD));
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_FREE_SPACE));
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_ARCHIVE_SIZE));
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_CLIENTS));
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_CAMERAS));

                List<String> refStates = new ArrayList<>();
                refStates.add(getMyHttpServer().getServerState().getCpuLoadRef());
                refStates.add(getMyHttpServer().getServerState().getFreeSpaceRef());
                refStates.add(getMyHttpServer().getServerState().getArchiveSizeRef());
                refStates.add(getMyHttpServer().getServerState().getClientsRef());
                refStates.add(getMyHttpServer().getServerState().getCamerasRef());
                for (String s : refStates) {
                    LOGGER.log(Level.TRACE, s);
                }

                string = HtmlParser.parseStatistics(string, parts[0] + " - " + parts[1], table, avgStates, refStates);
                LOGGER.log(Level.TRACE, string);
            }
            else {
                string = HtmlParser.parseStatistics(string, "", null, null, null);
            }
            byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_ENCODING_KEY, HttpConstants.ACCEPT_ENCODING_VALUE);
            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_KEY, HttpConstants.ACCEPT_VALUE);
            httpExchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.STATISTICS, e);
            sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
        }
    }
}
