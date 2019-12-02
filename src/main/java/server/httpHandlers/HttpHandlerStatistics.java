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

                List<String> refStates = new ArrayList<>();
                refStates.add(getMyHttpServer().getServerState().getCpuLoadRef());
                refStates.add(getMyHttpServer().getServerState().getFreeSpaceRef());
                refStates.add(getMyHttpServer().getServerState().getArchiveSizeRef());
                refStates.add(getMyHttpServer().getServerState().getClientsRef());
                refStates.add(getMyHttpServer().getServerState().getCamerasRef());

                string = HtmlParser.parseStatistics(string, parts[0] + " - " + parts[1], table, avgStates, refStates);
                LOGGER.log(Level.TRACE, string);
            }
            else {
                string = HtmlParser.parseStatistics(string, "", null, null, null);
            }
            sendResponse(httpExchange, string);
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.STATISTICS, e);
            sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
        }
    }
}
