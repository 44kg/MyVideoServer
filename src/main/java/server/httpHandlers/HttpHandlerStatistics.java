package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.MyHttpServer;
import server.StreamHandler;
import server.db.DatabaseService;
import server.html.HtmlParser;
import server.html.HTML;

import java.io.IOException;
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
            String string = HTML.getHtmlAsString(HTML.STATISTICS);
            String body = StreamHandler.toString(httpExchange.getRequestBody());
            if (!body.equals("")) {
                String[] parts = body.split("&");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].substring(parts[i].indexOf("=") + 1);
                }
                Date minDate = Date.valueOf(parts[0]);
                Date maxDate = Date.valueOf(parts[1]);
                List<List<String>> table = getMyHttpServer().getDatabaseService().selectState(minDate, maxDate);
                List<String> avgStates = new ArrayList<>();
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_CPU_LOAD, minDate, maxDate));
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_FREE_SPACE, minDate, maxDate));
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_ARCHIVE_SIZE, minDate, maxDate));
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_CLIENTS, minDate, maxDate));
                avgStates.add(getMyHttpServer().getDatabaseService().getAvgState(DatabaseService.COL_CAMERAS, minDate, maxDate));

                List<String> list = getMyHttpServer().getServerState().getRefStates();

                string = HtmlParser.parseStatistics(string, parts[0] + " - " + parts[1], table, avgStates, list);
            }
            else {
                string = HtmlParser.parseStatistics(string, "", null, null, null);
            }
            sendResponse(httpExchange, string);
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + HTML.STATISTICS, e);
            sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
        }
    }
}
