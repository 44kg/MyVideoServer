package server;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StreamHandler {
    private static final Logger LOGGER = LogManager.getLogger(StreamHandler.class);

    public static void transmitData(InputStream inputStream, OutputStream outputStream) {
        if (inputStream != null && outputStream != null) {
            byte[] buffer = new byte[1024];
            int length;
            try {
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
            }
            catch (IOException e) {
                LOGGER.log(Level.ERROR, "Stream cannot be read", e);
            }
        }
    }

    public static byte[] toByteArray(InputStream inputStream) {
        if (inputStream != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            try {
                while ((length = inputStream.read(buffer)) > 0) {
                    baos.write(buffer, 0, length);
                }
                inputStream.close();
                baos.close();
            }
            catch (IOException e) {
                LOGGER.log(Level.ERROR, "Stream cannot be read", e);
                return null;
            }
            return baos.toByteArray();
        }
        else return null;
    }

    public static String toString(InputStream inputStream) {
        if (inputStream != null) {
            return new String(toByteArray(inputStream), StandardCharsets.UTF_8);
        }
        else return null;
    }
}
