package server;

import com.sun.net.httpserver.HttpHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class HttpHandlersTest {
    private MyHttpServer myHttpServer;
    private HttpHandlers httpHandlers;

    @Before
    public void startTest() {
        try {
            myHttpServer = new MyHttpServer("", 8080);
            httpHandlers = new HttpHandlers(myHttpServer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getHttpHandlersMapTest1() {
        Map<String, HttpHandler> map = httpHandlers.getHttpHandlersMap();
        Assert.assertTrue(map.size() > 0);
    }

    @After
    public void endTest() {
        myHttpServer.stop();
    }
}
