package server.httpHandlers;

import com.sun.net.httpserver.Headers;
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
import java.sql.Date;
import java.sql.Time;
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
                body = body.replace("%3A", ":");
                LOGGER.log(Level.TRACE, body);
                String[] parts = body.split("&");
                for (int i = 0; i < parts.length; i++) {
                    LOGGER.log(Level.TRACE, parts[i]);
                    parts[i] = parts[i].substring(parts[i].indexOf("=") + 1);
                    LOGGER.log(Level.TRACE, parts[i]);
                }
                List<List<String>> table = getMyHttpServer().getDatabaseService().selectState(Date.valueOf(parts[0]),
                        Time.valueOf(parts[1]), Date.valueOf(parts[2]), Time.valueOf(parts[3]));
                string = HtmlParser.parseStatistics(string, parts[0] + " " + parts[1] + " - " + parts[2] + " " + parts[3], table);
            }
            else {
                string = HtmlParser.parseStatistics(string, "", null);
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
