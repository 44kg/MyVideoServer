package server.httpHandlers;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.command.CommandExecutor;
import server.command.CommandParser;

import java.io.IOException;

public class HttpHandlerRestart extends MyHttpHandler{
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerRestart.class);

    public HttpHandlerRestart() {
        super();
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String message = "Restart server...";
        try {
            httpExchange.getResponseHeaders().add(MyHttpHandler.ACCEPT_ENCODING_KEY, MyHttpHandler.ACCEPT_ENCODING_VALUE);
            httpExchange.getResponseHeaders().add(MyHttpHandler.ACCEPT_KEY, MyHttpHandler.ACCEPT_VALUE);
            httpExchange.sendResponseHeaders(200, message.length());
            sendResponse(httpExchange, message);
        }
        catch (IOException e) {
            LOGGER.log(Level.ERROR, "Restart server error", e);
            sendErrorResponse(httpExchange, "Сервер не был перезапущен.");
        }
        CommandExecutor.executeCommand(CommandParser.COMMAND_RESTART_SERVER);
    }
}
