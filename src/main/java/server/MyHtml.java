package server;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.*;

public class MyHtml {
    public static final String ADMIN = "admin.html";
    public static final String ERROR_500 = "error500.html";

    public static final String HTML_PACKAGE = "html/";

    public static final List<String> HTML_NAMES_LIST;
    static {
        HTML_NAMES_LIST = new ArrayList<>();
        HTML_NAMES_LIST.add(ADMIN);
        HTML_NAMES_LIST.add(ERROR_500);
    }

    private static final Logger LOGGER = LogManager.getLogger(MyHtml.class);

    public static InputStream getHtmlAsStream(String fileName) {
        if (HTML_NAMES_LIST.contains(fileName)) {
            return ClassLoader.getSystemResourceAsStream(HTML_PACKAGE + fileName);
        }
        else {
            LOGGER.log(Level.WARN, "Html file not found");
            return null;
        }
    }

    public static String getHtmlAsString(String fileName) {
        if (HTML_NAMES_LIST.contains(fileName)) {
            return StreamHandler.toString(ClassLoader.getSystemResourceAsStream(HTML_PACKAGE + fileName));
        }
        else {
            LOGGER.log(Level.WARN, "Html file not found");
            return "";
        }
    }
}
