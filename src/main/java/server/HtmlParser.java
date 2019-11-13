package server;

public class HtmlParser {
    public static final String HTML_REPLACE_CPU = "$cpu";
    public static final String HTML_REPLACE_FREE_SPACE = "$freespace";
    public static final String HTML_REPLACE_ARCHIVE_SIZE = "$archivesize";
    public static final String HTML_REPLACE_CAMERAS = "$cameras";
    public static final String HTML_REPLACE_CLIENTS = "$clients";
    public static final String HTML_REPLACE_ERROR_INFO = "$info";

    public static String parseAdmin(String string, String serverPath) {
        String numberOfCPUs = CommandParser.parseNumberOfCPUs(CommandExecutor.runLinuxCommand(CommandParser.COMMAND_NUM_OF_CPUS));
        String response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_CPU_LOAD);
        String cpuLoad = CommandParser.parseCpuLoad(response, Integer.parseInt(numberOfCPUs));

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_FREE_SPACE);
        String freeSpace = CommandParser.parseFreeSpace(response);

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_ARCHIVE_SIZE + serverPath + MyHttpServer.DIRECTORY_ARCHIVE);
        String archiveSize = CommandParser.parseArchiveSize(response);

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_CONNECTIONS);
        String cameras = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CAMERAS);
        String clients = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CLIENTS);

        return string.replace(HTML_REPLACE_CPU, cpuLoad).replace(HTML_REPLACE_FREE_SPACE, freeSpace)
                .replace(HTML_REPLACE_ARCHIVE_SIZE, archiveSize).replace(HTML_REPLACE_CAMERAS, cameras)
                .replace(HTML_REPLACE_CLIENTS, clients);
    }

    public static String parseError500(String string, String errorInfo) {
        return string.replace(HTML_REPLACE_ERROR_INFO, errorInfo);
    }
}
