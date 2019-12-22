package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import server.ServerState;
import server.StreamHandler;
import server.db.DatabaseService;
import server.html.HtmlParser;
import server.html.HTML;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class HttpHandlerStatistics extends MyHttpHandler {
    private DatabaseService dbs;
    private ServerState serverState;

    public HttpHandlerStatistics(DatabaseService dbs, ServerState serverState) {
        super();
        this.dbs = dbs;
        this.serverState = serverState;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String string = HTML.getHtmlAsString(HTML.STATISTICS);
        String body = StreamHandler.toString(httpExchange.getRequestBody());
        body = body.replace("%3A", ":");
        String[] parts = body.split("&");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].substring(parts[i].indexOf("=") + 1);
        }

        Date minDate = Date.valueOf(parts[0]);
        Time minTime = Time.valueOf(parts[1] + ":00");
        Date maxDate = Date.valueOf(parts[2]);
        Time maxTime = Time.valueOf(parts[3] + ":00");

        List<List<String>> table = dbs.selectStates(minDate, minTime, maxDate, maxTime);
        List<String> avgStates = dbs.getAvgStates(minDate, minTime, maxDate, maxTime);

        List<String> list = serverState.getRefStates();

        string = HtmlParser.parseStatistic(string, parts[0] + " " + parts[1] + " - " + parts[2] + " " + parts[3], table, avgStates, list);
        sendResponse(httpExchange, string);
    }
}
