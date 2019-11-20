package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.command.CommandExecutor;
import server.command.CommandParser;
import server.MyHttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpHandlerRestart extends MyHttpHandler{
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerRestart.class);

    public HttpHandlerRestart(MyHttpServer myHttpServer) {
        super(myHttpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String message = "Restart server...";
        try {
            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_ENCODING_KEY, HttpConstants.ACCEPT_ENCODING_VALUE);
            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_KEY, HttpConstants.ACCEPT_VALUE);
            httpExchange.sendResponseHeaders(200, message.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(message.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "Restart server error", e);
            sendErrorResponse(httpExchange, "Сервер не был перезапущен.");
        }
        CommandExecutor.runLinuxCommand(CommandParser.COMMAND_RESTART_SERVER);
    }
}
