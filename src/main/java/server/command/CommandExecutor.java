package server.command;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecutor {
    private static final Logger LOGGER = LogManager.getLogger(CommandExecutor.class);

    public static String executeCommand(String command) {
        if (command == null || command.trim().equals("")) return "";
        StringBuilder result = new StringBuilder();
        Process process = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Linux command execution error", e);
        } finally {
            try {
                if (process != null) process.destroy();
                if (reader != null) reader.close();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "Linux command execution error", e);
            }
        }
        return result.toString();
    }


}
