package server;

import java.math.BigDecimal;
import java.math.MathContext;

public class CommandParser {
    public static final String COMMAND_CPU_LOAD = "top -b d1 n1";
    public static final String COMMAND_NUM_OF_CPUS = "lscpu";
    public static final String COMMAND_FREE_SPACE = "df -h /";
    public static final String COMMAND_ARCHIVE_SIZE = "du --block-size=M ";
    public static final String COMMAND_CONNECTIONS = "netstat -nap";
    public static final String COMMAND_RESTART_SERVER = "service restart";

    public static final String PORT_FOR_CAMERAS = "rtsp";
    public static final String PORT_FOR_CLIENTS = "9000";

    public static String parseCpuLoad(String response, int numberOfCPUs) {
        BigDecimal result = new BigDecimal("0.0");
        String[] lines = response.split("\n");
        if (lines.length > 6) {
            int index = lines[6].indexOf("%CPU");
            String addendum;
            for (int i = 7; i < lines.length; i++) {
                addendum = lines[i].substring(index, index + 4).trim().replace(",", ".");
                if (addendum.equals("0.0"))
                    break;
                result = result.add(new BigDecimal(addendum));
            }
            result = result.divide(new BigDecimal(String.valueOf(numberOfCPUs)), new MathContext(3));
            return result.toString() + "%";
        }
        return "";
    }

    public static String parseNumberOfCPUs(String response) {
        String[] lines = response.split("\n");
        String numberOfCPUs = "0";
        for (String line : lines) {
            if (line.startsWith("CPU(s):")) {
                numberOfCPUs = line.substring(7).trim();
                break;
            }
        }
        return numberOfCPUs;
    }

    public static String parseFreeSpace(String response) {
        String[] lines = response.split("\n");
        if (lines.length >= 2) {
            String line = lines[1];
            for (int i = 0; i < 3; i++) {
                line = line.substring(line.indexOf(" ")).trim();
            }
            line = line.substring(0, line.indexOf(" "));
            return line + "B";
        }
        return "";
    }

    public static String parseArchiveSize(String response) {
        String[] lines = response.split("\n");
        StringBuilder builder = new StringBuilder(lines[lines.length - 1]);
        if (builder.length() > 0) {
            builder.delete(builder.indexOf("M"), builder.length());
            builder.append("MB");
            return builder.toString();
        }
        return "";
    }

    public static String parseNumberOfConnections(String response, String port) {
        if (response != null && port != null) {
            String[] lines = response.split("\n");
            if (lines.length > 3) {
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
        }
        return "";
    }
}
