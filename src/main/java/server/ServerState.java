package server;

import server.command.CommandExecutor;
import server.command.CommandParser;

import java.util.ArrayList;
import java.util.List;

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

    private int timeRange;
    private boolean updatable;
    private MyHttpServer myHttpServer;

    public ServerState(MyHttpServer myHttpServer, int sec) {
        this.myHttpServer = myHttpServer;
        this.timeRange = sec * 1000;
        start();
    }

    public void update() {
        String numberOfCPUs = CommandParser.parseNumberOfCPUs(CommandExecutor.executeCommand(CommandParser.COMMAND_NUM_OF_CPUS));
        String response = CommandExecutor.executeCommand(CommandParser.COMMAND_CPU_LOAD);
        cpuLoad = CommandParser.parseCpuLoad(response, Integer.parseInt(numberOfCPUs));

        response = CommandExecutor.executeCommand(CommandParser.COMMAND_FREE_SPACE);
        freeSpace = CommandParser.parseFreeSpace(response);

        response = CommandExecutor.executeCommand(CommandParser.COMMAND_ARCHIVE_SIZE
                + myHttpServer.getPath() + MyHttpServer.DIRECTORY_ARCHIVE);
        archiveSize = CommandParser.parseArchiveSize(response);

        response = CommandExecutor.executeCommand(CommandParser.COMMAND_CONNECTIONS);
        cameras = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CAMERAS);
        clients = CommandParser.parseNumberOfConnections(response, CommandParser.PORT_FOR_CLIENTS);

        saveToDatabase();
    }

    public void start() {
        updatable = true;
        update();
        readReferences();
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

    public void readReferences() {
        List<String> ref = readReferenceStates();
        cpuLoadRef = ref.get(2);
        freeSpaceRef = ref.get(3);
        archiveSizeRef = ref.get(4);
        clientsRef = ref.get(5);
        camerasRef = ref.get(6);
    }

    private void saveToDatabase() {
        float cpuLoad = Float.parseFloat(this.cpuLoad.substring(0, this.cpuLoad.length() - 1));
        float freeSpace = Float.parseFloat(this.freeSpace.substring(0, this.freeSpace.length() - 2));
        float archiveSize = Float.parseFloat(this.archiveSize.substring(0, this.archiveSize.length() - 2));
        int clients = Integer.parseInt(this.clients);
        int cameras = Integer.parseInt(this.cameras);
        myHttpServer.getDatabaseService().insertState(cpuLoad, freeSpace, archiveSize, clients, cameras);
    }

    private List<String> readReferenceStates() {
        return myHttpServer.getDatabaseService().selectState(0, 0).get(0);
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
