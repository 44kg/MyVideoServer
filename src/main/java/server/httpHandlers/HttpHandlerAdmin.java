package server.httpHandlers;

import server.*;
import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpHandlerAdmin extends MyHttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(HttpHandlerAdmin.class);

    public HttpHandlerAdmin(MyHttpServer myHttpServer) {
        super(myHttpServer);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String string = MyHtml.getHtmlAsString(MyHtml.ADMIN);
            string = HtmlParser.parseAdmin(string, executeCommands());
            byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_ENCODING_KEY, HttpConstants.ACCEPT_ENCODING_VALUE);
            httpExchange.getResponseHeaders().add(HttpConstants.ACCEPT_KEY, HttpConstants.ACCEPT_VALUE);
            httpExchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(bytes);
            os.close();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "html sending error: " + MyHtml.ADMIN, e);
            sendErrorResponse(httpExchange, "Не удалось загрузить страницу.");
        }
    }

    private List<String> executeCommands() {
        List<String> list = new ArrayList<>();

        String numberOfCPUs = CommandParser.parseNumberOfCPUs(CommandExecutor.runLinuxCommand(CommandParser.COMMAND_NUM_OF_CPUS));
        String response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_CPU_LOAD);
        String cpuLoad = CommandParser.parseCpuLoad(response, Integer.parseInt(numberOfCPUs));

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_FREE_SPACE);
        String freeSpace = CommandParser.parseFreeSpace(response);

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_ARCHIVE_SIZE
                + getMyHttpServer().getPath() + MyHttpServer.DIRECTORY_ARCHIVE);
        String archiveSize = CommandParser.parseArchiveSize(response);

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_CONNECTIONS);
        String cameras = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CAMERAS);
        String clients = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CLIENTS);

        list.add(cpuLoad);
        list.add(freeSpace);
        list.add(archiveSize);
        list.add(cameras);
        list.add(clients);
        return list;
    }
}
