package server.html;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class HtmlParser {
    public static final String HTML_REPLACE_CPU = "$cpu";
    public static final String HTML_REPLACE_FREE_SPACE = "$freespace";
    public static final String HTML_REPLACE_ARCHIVE_SIZE = "$archivesize";
    public static final String HTML_REPLACE_CAMERAS = "$cameras";
    public static final String HTML_REPLACE_CLIENTS = "$clients";
    public static final String HTML_REPLACE_ERROR_INFO = "$info";
    public static final String HTML_REPLACE_DATES = "$dates";
    public static final String HTML_REPLACE_TABLE = "$table";

    private static final Logger LOGGER = LogManager.getLogger(HtmlParser.class);

    public static String parseAdmin(String html, List<String> replaces) {
        if (replaces.size() == 5 && html != null) {
            return html.replace(HTML_REPLACE_CPU, replaces.get(0)).replace(HTML_REPLACE_FREE_SPACE, replaces.get(1))
                    .replace(HTML_REPLACE_ARCHIVE_SIZE, replaces.get(2)).replace(HTML_REPLACE_CAMERAS, replaces.get(3))
                    .replace(HTML_REPLACE_CLIENTS, replaces.get(4));
        }
        else {
            LOGGER.log(Level.WARN, "Wrong argument for parsing admin.html");
            return html;
        }
    }

    public static String parseError500(String html, String errorInfo) {
        if (errorInfo == null) {
            errorInfo = "";
        }
        return html.replace(HTML_REPLACE_ERROR_INFO, errorInfo);
    }

    public static String parseStatistics(String html, String dates, List<List<String>> table, List<String> avgStates) {
        StringBuilder tableHtml = new StringBuilder();
        if (table != null) {
            for (List<String> parts : table) {
                tableHtml.append("<tr>");
                for (String part : parts) {
                    tableHtml.append("<td align=\"center\" width=150><small>").append(part).append("</small></td>");
                }
                tableHtml.append("</tr>");
            }
        }
        return pasteNumberedList(html, avgStates).replace(HTML_REPLACE_DATES, dates).replace(HTML_REPLACE_TABLE, tableHtml.toString());
    }

    public static String parseReferenceState(String html, List<String> states) {
        return pasteNumberedList(html, states);
    }

    private static String pasteNumberedList(String html, List<String> list) {
        String replaceable;
        for (int i = 0; i < list.size(); i++) {
            replaceable = "$0" + (i + 1);
            html = html.replace(replaceable, list.get(i));
        }
        return html;
    }
}
