package server;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class MyHtml {
    private MyHttpServer myHttpServer;
    private List<String> htmlNames;

    public static final String ADMIN = "admin.html";
    public static final String ERROR_500 = "error500.html";

    public static final String HTML_PACKAGE = "html/";

    public MyHtml(MyHttpServer myHttpServer) {
        this.myHttpServer = myHttpServer;
        URL url = ClassLoader.getSystemResource(HTML_PACKAGE);
        File file = new File(url.getPath());
        String[] files = file.list();
        if (files != null) {
            htmlNames = Arrays.asList(files);
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
