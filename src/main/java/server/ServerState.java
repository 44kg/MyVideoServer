package server;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.command.CommandExecutor;
import server.command.CommandParser;

public class ServerState {
    private String cpuLoad;
    private String freeSpace;
    private String archiveSize;
    private String clients;
    private String cameras;

    private boolean isRunning;

    private MyHttpServer myHttpServer;

    private static final Logger LOGGER = LogManager.getLogger(ServerState.class);

    public ServerState(MyHttpServer myHttpServer) {
        this.myHttpServer = myHttpServer;
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
        isRunning = true;
        Thread thread = new Thread(() -> {
            try {
                while (isRunning) {
                    update();
                    Thread.sleep(3000);
                }
            }
            catch (InterruptedException e) {
                LOGGER.log(Level.ERROR, "Server state error", e);
            }
        });
        thread.start();
    }

    public void stop() {
        isRunning = false;
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
