package server.html;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.StreamHandler;

import java.io.IOException;
import java.io.InputStream;

public class HTML {
    public static final String ADMIN = "admin.html";
    public static final String ERROR_500 = "error500.html";
    public static final String STATISTICS = "statistics.html";
    public static final String REFERENCE_STATE = "reference_state.html";

    public static final String HTML_PACKAGE = "html/";

    private static final Logger LOGGER = LogManager.getLogger(HTML.class);

    public static String getHtmlAsString(String fileName) {
        if (fileName == null) return "";
        try (InputStream stream = ClassLoader.getSystemResourceAsStream(HTML_PACKAGE + fileName)) {
            return StreamHandler.toString(stream);
        } catch (IOException e) {
            LOGGER.log(Level.WARN, "Html file not found");
            return "";
        }
    }
}
