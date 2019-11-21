package server.db;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

public class DatabaseService {
    private Connection con;

    private static final Logger LOGGER = LogManager.getLogger(DatabaseService.class);

    public DatabaseService() {
        con = null;
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection
                    ("jdbc:postgresql://localhost:5432/test_db", "mainkaif", "11111");
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.ERROR, "Database connection error", e);
        }
    }

    public void insertState(float cpuLoad, float freeSpace, float archiveSize, int clients, int cameras) {
        try (PreparedStatement statement = con.prepareStatement
                ("INSERT INTO server_state_db (date, time, cpu_load, free_space, archive_size," +
                        " clients, cameras) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            statement.setDate(1, new Date(System.currentTimeMillis()));
            statement.setTime(2, new Time(System.currentTimeMillis()));
            statement.setFloat(3, cpuLoad);
            statement.setFloat(4, freeSpace);
            statement.setFloat(5, archiveSize);
            statement.setInt(6, clients);
            statement.setInt(7, cameras);
            System.out.println(freeSpace);
            System.out.println(statement.toString());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Database INSERT error", e);
        }
    }
}
