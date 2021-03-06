package server;

import app.Main;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.command.CommandExecutor;
import server.command.CommandParser;
import server.db.DatabaseService;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class ServerState {
    private String cpuLoad;
    private String freeSpace;
    private String archiveSize;
    private String clients;
    private String cameras;

    private String cpuLoadRef;
    private String freeSpaceRef;
    private String archiveSizeRef;
    private String clientsRef;
    private String camerasRef;

    private volatile int timeRange;
    private DatabaseService dbs;
    private String path;

    private static final Logger LOGGER = LogManager.getLogger(ServerState.class);

    public ServerState(DatabaseService dbs, String path, int sec) {
        this.dbs = dbs;
        this.timeRange = sec * 1000;
        this.path = path;
        start();
    }

    public void update() {
        String numberOfCPUs = CommandParser.parseNumberOfCPUs(CommandExecutor.executeCommand(CommandParser.COMMAND_NUM_OF_CPUS));
        String response = CommandExecutor.executeCommand(CommandParser.COMMAND_CPU_LOAD);
        cpuLoad = CommandParser.parseCpuLoad(response, Integer.parseInt(numberOfCPUs));

        response = CommandExecutor.executeCommand(CommandParser.COMMAND_FREE_SPACE);
        freeSpace = CommandParser.parseFreeSpace(response);

        response = CommandExecutor.executeCommand(CommandParser.COMMAND_ARCHIVE_SIZE
                + path + Main.DIRECTORY_ARCHIVE);
        archiveSize = CommandParser.parseArchiveSize(response);

        response = CommandExecutor.executeCommand(CommandParser.COMMAND_CONNECTIONS);
        cameras = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CAMERAS);
        clients = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CLIENTS);

        saveToDatabase();
    }

    public void start() {
        readReferences();
        Thread thread = new Thread(() -> {
            while (true) {
                update();
                try {
                    sleep(timeRange);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.ERROR, "InterruptedException", e);
                }
            }
        });
        thread.start();
    }

    public void readReferences() {
        List<String> ref = dbs.selectReferences();
        cpuLoadRef = ref.get(0);
        freeSpaceRef = ref.get(1);
        archiveSizeRef = ref.get(2);
        clientsRef = ref.get(3);
        camerasRef = ref.get(4);
    }

    private void saveToDatabase() {
        float cpuLoad = Float.parseFloat(this.cpuLoad.substring(0, this.cpuLoad.length() - 1));
        float freeSpace = Float.parseFloat(this.freeSpace.substring(0, this.freeSpace.length() - 2));
        float archiveSize = Float.parseFloat(this.archiveSize.substring(0, this.archiveSize.length() - 2));
        int clients = Integer.parseInt(this.clients);
        int cameras = Integer.parseInt(this.cameras);
        dbs.insertStates(cpuLoad, freeSpace, archiveSize, clients, cameras);
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

    public List<String> getRefStates() {
        List<String> list = new ArrayList<>();
        list.add(cpuLoadRef);
        list.add(freeSpaceRef);
        list.add(archiveSizeRef);
        list.add(clientsRef);
        list.add(camerasRef);
        return list;
    }
}
