package server;

import server.command.CommandExecutor;
import server.command.CommandParser;

public class ServerState {
    private String cpuLoad;
    private String freeSpace;
    private String archiveSize;
    private String clients;
    private String cameras;

    private int timeRange;
    private boolean updatable;
    private MyHttpServer myHttpServer;

    public ServerState(MyHttpServer myHttpServer, int sec) {
        this.myHttpServer = myHttpServer;
        this.timeRange = sec * 1000;
        start();
    }

    public void update() {
        String numberOfCPUs = CommandParser.parseNumberOfCPUs(CommandExecutor.runLinuxCommand(CommandParser.COMMAND_NUM_OF_CPUS));
        String response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_CPU_LOAD);
        cpuLoad = CommandParser.parseCpuLoad(response, Integer.parseInt(numberOfCPUs));

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_FREE_SPACE);
        freeSpace = CommandParser.parseFreeSpace(response);

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_ARCHIVE_SIZE
                + myHttpServer.getPath() + MyHttpServer.DIRECTORY_ARCHIVE);
        archiveSize = CommandParser.parseArchiveSize(response);

        response = CommandExecutor.runLinuxCommand(CommandParser.COMMAND_CONNECTIONS);
        cameras = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CAMERAS);
        clients = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CLIENTS);
    }

    public void start() {
        updatable = true;
        Thread thread = new Thread(() -> {
            long ms = System.currentTimeMillis();
            while (updatable) {
                if (System.currentTimeMillis() - ms > timeRange) {
                    update();
                    ms = System.currentTimeMillis();
                }
            }
        });
        thread.start();
    }

    public void stop() {
        updatable = false;
    }

    public String getCpuLoad() {
        return cpuLoad;
    }

    public String getFreeSpace() {
        return freeSpace;
    }

    public String getArchiveSize() {
        return archiveSize;
    }

    public String getClients() {
        return clients;
    }

    public String getCameras() {
        return cameras;
    }
}
