package server;

import java.util.HashMap;
import java.util.Map;

public class MyHtml {
    private Map<String, String> mapHtml;

    public static final String ADMIN = "admin.html";
    public static final String ERROR_500 = "error500.html";

    public static final String HTML_REPLACE_CPU = "$cpu";
    public static final String HTML_REPLACE_FREE_SPACE = "$freespace";
    public static final String HTML_REPLACE_ARCHIVE_SIZE = "$archivesize";
    public static final String HTML_REPLACE_CAMERAS = "$cameras";
    public static final String HTML_REPLACE_CLIENTS = "$clients";
    public static final String HTML_REPLACE_ERROR_INFO = "$info";

    public MyHtml() {
        loadHtmls();
    }

    private void loadHtmls() {
        mapHtml = new HashMap<>();
        mapHtml.put(ADMIN, StreamHandler.toString(ClassLoader.getSystemResourceAsStream(ADMIN)));
        mapHtml.put(ERROR_500, StreamHandler.toString(ClassLoader.getSystemResourceAsStream(ERROR_500)));
    }


    public String getHtmlAsString(String htmlName) {
        if (htmlName.equals(ADMIN)) {
            String cpuLoad = LinuxCommandParser.getCpuLoad();
            String freeSpace = LinuxCommandParser.getFreeSpace();
            String archiveSize = LinuxCommandParser.getArchiveSize();
            StringBuilder response = LinuxCommandParser.runLinuxCommand(LinuxCommandParser.COMMAND_CONNECTIONS);
            String cameras = LinuxCommandParser.getNumberOfConnections(response, "554");
            String clients = LinuxCommandParser.getNumberOfConnections(response, "9000");

            String string = mapHtml.get(ADMIN);
            return string.replace(HTML_REPLACE_CPU, cpuLoad).replace(HTML_REPLACE_FREE_SPACE, freeSpace)
                    .replace(HTML_REPLACE_ARCHIVE_SIZE, archiveSize).replace(HTML_REPLACE_CAMERAS, cameras)
                    .replace(HTML_REPLACE_CLIENTS, clients);
        }
        else return null;
    }

    public String getHtmlAsString(String htmlName, String errorInfo) {
        if (htmlName.equals(ERROR_500)) {
            String string = mapHtml.get(ERROR_500);
            return string.replace(HTML_REPLACE_ERROR_INFO, errorInfo);
        }
        else return null;
    }

    public byte[] getHtmlAsByteArray(String htmlName) {
        return getHtmlAsString(htmlName).getBytes();
    }

    public byte[] getHtmlAsByteArray(String htmlName, String errorInfo) {
        return getHtmlAsString(htmlName, errorInfo).getBytes();
    }
}
