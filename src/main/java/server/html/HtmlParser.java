package server.html;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Date;

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
        if (html != null && replaces.size() == 5) {
            String date = new Date(System.currentTimeMillis()).toString();
            return html.replace(HTML_REPLACE_CPU, replaces.get(0)).replace(HTML_REPLACE_FREE_SPACE, replaces.get(1))
                    .replace(HTML_REPLACE_ARCHIVE_SIZE, replaces.get(2)).replace(HTML_REPLACE_CAMERAS, replaces.get(3))
                    .replace(HTML_REPLACE_CLIENTS, replaces.get(4)).replace(HTML_REPLACE_DATES, date);
        }
        else {
            LOGGER.log(Level.WARN, "Wrong argument for parsing " + HTML.ADMIN);
            return html;
        }
    }

    public static String parseError500(String html, String errorInfo) {
        if (errorInfo == null) {
            errorInfo = "";
        }
        return html.replace(HTML_REPLACE_ERROR_INFO, errorInfo);
    }

    public static String parseStatistic(String html, String dates, List<List<String>> table, List<String> avgStates, List<String> refStates) {
        if (html == null) return null;
        if (dates == null || table == null || avgStates == null || refStates == null || table.size() == 0 ||
                table.get(0).size() != 6 || avgStates.size() != 5 || refStates.size() != 5) return html;
        createHtmlTable(table, refStates);
        StringBuilder tableHtml = new StringBuilder();
        for (List<String> line : table) {
            tableHtml.append("<tr>");
            for (String value : line) {
                tableHtml.append(value);
            }
            tableHtml.append("</tr>");
        }
        return pasteNumberedList(html, avgStates).replace(HTML_REPLACE_DATES, dates).replace(HTML_REPLACE_TABLE, tableHtml.toString());
    }

    private static void createHtmlTable(List<List<String>> table, List<String> refStates) {
        float value;
        float refValue;
        for (List<String> line : table) {
            line.set(0, "<td align=\"center\" width=150><small><font color=\"black\">" + line.get(0) + "</font></small></td>");
            for (int i = 1; i < line.size(); i++) {
                value = Float.parseFloat(line.get(i));
                refValue = Float.parseFloat(refStates.get(i - 1));
                line.set(i, "<td align=\"center\" width=150><small><font color=\"black\">" + line.get(i) + "</font></small></td>");
                if (value > refValue && i != 2) line.set(i, line.get(i).replace("black", "red"));
                if (value < refValue && i == 2) line.set(i, line.get(i).replace("black", "red"));
            }
        }
    }

    public static String parseReferenceState(String html, List<String> states) {
        if (html != null && states.size() == 5) {
            return pasteNumberedList(html, states);
        }
        else {
            LOGGER.log(Level.WARN, "Wrong argument for parsing " + HTML.REFERENCE_STATE);
            return html;
        }
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
