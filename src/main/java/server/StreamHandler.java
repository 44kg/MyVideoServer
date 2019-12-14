package server;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StreamHandler {
    private static final Logger LOGGER = LogManager.getLogger(StreamHandler.class);

    public static void transmitData(InputStream inputStream, OutputStream outputStream) {
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Stream cannot be read", e);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "Stream closing error", e);
            }
        }
    }

    public static byte[] toByteArray(InputStream inputStream) {
        if (inputStream == null) return null;

        byte[] buffer = new byte[1024];
        int length;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            while ((length = inputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Stream cannot be read", e);
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "Stream closing error", e);
            }
        }
    }

    public static String toString(InputStream inputStream) {
        if (inputStream == null) return null;
        return new String(toByteArray(inputStream), StandardCharsets.UTF_8);
    }
}
