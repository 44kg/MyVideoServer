import server.MyHttpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new MyHttpServer(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
