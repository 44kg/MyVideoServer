package server;

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

    private static final Logger LOGGER = LogManager.getLogger(HtmlParser.class);

    public static String parseAdmin(String string, List<String> replaces) {
        if (replaces.size() == 5 && string != null) {
            return string.replace(HTML_REPLACE_CPU, replaces.get(0)).replace(HTML_REPLACE_FREE_SPACE, replaces.get(1))
                    .replace(HTML_REPLACE_ARCHIVE_SIZE, replaces.get(2)).replace(HTML_REPLACE_CAMERAS, replaces.get(3))
                    .replace(HTML_REPLACE_CLIENTS, replaces.get(4));
        }
        else {
            LOGGER.log(Level.WARN, "Wrong argument for parsing admin.html");
            return string;
        }
    }

    public static String parseError500(String string, String errorInfo) {
        if (errorInfo == null) {
            errorInfo = "";
        }
        return string.replace(HTML_REPLACE_ERROR_INFO, errorInfo);
    }
}
