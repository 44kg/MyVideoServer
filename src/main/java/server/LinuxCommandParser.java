package server;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;

public class LinuxCommandParser {
    public static final String COMMAND_CPU_LOAD = "top -b d1 n1";
    public static final String COMMAND_NUM_OF_CPUS = "lscpu";
    public static final String COMMAND_FREE_SPACE = "df -h /";
    public static final String COMMAND_ARCHIVE_SIZE = "du --block-size=M ";
    public static final String COMMAND_CONNECTIONS = "netstat -nap";

    private static final Logger LOGGER = LogManager.getLogger(LinuxCommandParser.class);

    public LinuxCommandParser() {}

    public static String getCpuLoad() {
        StringBuilder builder = runLinuxCommand(COMMAND_CPU_LOAD);
        BigDecimal result = new BigDecimal("0.0");
        String[] lines = builder.toString().split("\n");
        int index = lines[6].indexOf("%CPU");
        String addendum;
        for (int i = 7; i < lines.length; i++) {
            addendum = lines[i].substring(index, index + 4).trim().replace(",", ".");
            if (addendum.equals("0.0"))
                break;
            result = result.add(new BigDecimal(addendum));
        }
        result = result.divide(new BigDecimal(getNumberOfCPUs()), new MathContext(3));
        return result.toString() + "%";
    }

    public static String getNumberOfCPUs() {
        StringBuilder builder = runLinuxCommand(COMMAND_NUM_OF_CPUS);
        String[] lines = builder.toString().split("\n");
        String numberOfCPUs = "";
        for (String line : lines) {
            if (line.startsWith("CPU(s):")) {
                numberOfCPUs = line.substring(7).trim();
                break;
            }
        }
        return numberOfCPUs;
    }

    public static String getFreeSpace() {
        StringBuilder builder = runLinuxCommand(COMMAND_FREE_SPACE);
        String[] lines = builder.toString().split("\n");
        String line = lines[1];
        for (int i = 0; i < 3; i++) {
            line = line.substring(line.indexOf(" ")).trim();
        }
        line = line.substring(0, line.indexOf(" "));
        return line + "B";
    }

    public static String getArchiveSize() {
        StringBuilder builder = runLinuxCommand(COMMAND_ARCHIVE_SIZE);
        builder.delete(builder.indexOf("M"), builder.length());
        builder.append("MB");
        return builder.toString();
    }

    public static String getNumberOfConnections(StringBuilder response, String port) {
        if (response != null && port != null) {
            String[] lines = response.toString().split("\n");
            int counter = 0;
            for (String line : lines) {
                if (line.startsWith("tcp")) {
                    for (int i = 0; i < 4; i++) {
                        line = line.substring(line.indexOf(" ")).trim();
                    }
                    if (line.substring(line.indexOf(":") + 1, line.indexOf(" ")).equals(port)) {
                        counter++;
                    }
                }
                if (line.startsWith("unix")) {
                    break;
                }
            }
            return Integer.toString(counter);
        }
        else return null;
    }

    public static void restartServer() {
        runLinuxCommand("service restart");
    }

    public static StringBuilder runLinuxCommand(String command) {
        if (command == null || command.trim().equals("")) {
            return null;
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
                return null;
            }
            return builder;
        }
    }
}
