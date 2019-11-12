package server;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.httpHandlers.MyHttpHandler;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class MyHtml {
    private MyHttpServer myHttpServer;
    private List<String> htmlNames;

    public static final String ADMIN = "admin.html";
    public static final String ERROR_500 = "error500.html";

    public static final String HTML_PACKAGE = "html/";

    private static final Logger LOGGER = LogManager.getLogger(MyHtml.class);

    public MyHtml(MyHttpServer myHttpServer) {
        this.myHttpServer = myHttpServer;
        URL url = ClassLoader.getSystemResource(HTML_PACKAGE);
        try {
            File file = new File(url.toURI());
            String[] files = file.list();
            if (files != null) {
                htmlNames = new ArrayList<>();
                Collections.addAll(htmlNames, files);
            }
            else {
                LOGGER.log(Level.WARN, "Html file not found");
            }
        }
        catch (URISyntaxException e) {
            LOGGER.log(Level.ERROR, "URL to URI error");
        }
    }

    public InputStream getHtmlAsStream(String fileName) {
        if (htmlNames.contains(fileName)) {
            return ClassLoader.getSystemResourceAsStream(HTML_PACKAGE + fileName);
        }
        else return null;
    }

    public String getHtmlAsString(String fileName) {
        if (htmlNames.contains(fileName)) {
            return StreamHandler.toString(ClassLoader.getSystemResourceAsStream(HTML_PACKAGE + fileName));
        }
        else return "";
    }

    public byte[] getHtmlAsByteArray(String fileName) {
        return getHtmlAsString(fileName).getBytes();
    }
}
