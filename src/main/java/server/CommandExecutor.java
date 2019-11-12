package server;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.httpHandlers.HttpHandlerAdmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecutor {
    private static final Logger LOGGER = LogManager.getLogger(CommandExecutor.class);

    public static String runLinuxCommand(String command) {
        if (command == null || command.trim().equals("")) {
            return "";
        }
        else {
            StringBuilder builder = new StringBuilder();
            try {
                String line;
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null)
                    builder.append(line).append("\n");
                process.waitFor();
                if (process.exitValue() == 1) {
                    LOGGER.log(Level.WARN, "Linux command: " + command + ". Exit value = 1");
                }
                process.destroy();
            } catch (IOException | InterruptedException e) {
                LOGGER.log(Level.ERROR, "Linux command execution error", e);
                return "";
            }
            return builder.toString();
        }
    }
}
