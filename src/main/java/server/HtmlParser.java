package server;

public class HtmlParser {
    public static final String HTML_REPLACE_CPU = "$cpu";
    public static final String HTML_REPLACE_FREE_SPACE = "$freespace";
    public static final String HTML_REPLACE_ARCHIVE_SIZE = "$archivesize";
    public static final String HTML_REPLACE_CAMERAS = "$cameras";
    public static final String HTML_REPLACE_CLIENTS = "$clients";
    public static final String HTML_REPLACE_ERROR_INFO = "$info";

    public static String parseAdmin(String string, String serverPath) {
        String cpuLoad = LinuxCommandParser.getCpuLoad();
        String freeSpace = LinuxCommandParser.getFreeSpace();
        String archiveSize = LinuxCommandParser.getArchiveSize(serverPath + MyHttpServer.DIRECTORY_ARCHIVE);
        StringBuilder response = LinuxCommandParser.runLinuxCommand(LinuxCommandParser.COMMAND_CONNECTIONS);
        String cameras = LinuxCommandParser.getNumberOfConnections(response, "rtsp");
        String clients = LinuxCommandParser.getNumberOfConnections(response, "9000");

        return string.replace(HTML_REPLACE_CPU, cpuLoad).replace(HTML_REPLACE_FREE_SPACE, freeSpace)
                .replace(HTML_REPLACE_ARCHIVE_SIZE, archiveSize).replace(HTML_REPLACE_CAMERAS, cameras)
                .replace(HTML_REPLACE_CLIENTS, clients);
    }

    public static String parseError500(String string, String errorInfo) {
        return string.replace(HTML_REPLACE_ERROR_INFO, errorInfo);
    }
}
