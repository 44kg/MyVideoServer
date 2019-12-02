package server.db;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private Connection con;

    public static final String DB_DRIVER = "org.postgresql.Driver";
    public static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/test_db";
    public static final String DB_TABLE = "states_db";
    public static final String DB_USER = "mainkaif";
    public static final String DB_PASS = "11111";

    public static final String COL_ID = "id";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";
    public static final String COL_CPU_LOAD = "cpu_load";
    public static final String COL_FREE_SPACE = "free_space";
    public static final String COL_ARCHIVE_SIZE = "archive_size";
    public static final String COL_CLIENTS = "clients";
    public static final String COL_CAMERAS = "cameras";

    private static final Logger LOGGER = LogManager.getLogger(DatabaseService.class);

    public DatabaseService() {
        con = null;
        openConnection();
    }

    public void openConnection() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName(DB_DRIVER);
                con = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASS);
                LOGGER.log(Level.INFO, "Connected to database");
            }
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.ERROR, "Database connection error", e);
            closeConnection();
        }
    }

    public void closeConnection() {
        if (con != null) {
            try {
                con.close();
            }
            catch (SQLException e) {
                LOGGER.log(Level.ERROR, "Database connection error", e);
            }
        }
    }

    public void insertState(float cpuLoad, float freeSpace, float archiveSize, int clients, int cameras) {
        openConnection();
        try (PreparedStatement statement = con.prepareStatement
                ("INSERT INTO " + DB_TABLE + " (" + COL_DATE + ", " + COL_TIME + ", " + COL_CPU_LOAD + ", "
                        + COL_FREE_SPACE + ", " + COL_ARCHIVE_SIZE + ", " + COL_CLIENTS + ", " + COL_CAMERAS
                        + ") VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            statement.setDate(1, new Date(System.currentTimeMillis()));
            statement.setTime(2, new Time(System.currentTimeMillis()));
            statement.setFloat(3, cpuLoad);
            statement.setFloat(4, freeSpace);
            statement.setFloat(5, archiveSize);
            statement.setInt(6, clients);
            statement.setInt(7, cameras);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Database INSERT error", e);
        }
    }

    public void updateState(int id, float cpuLoad, float freeSpace, float archiveSize, int clients, int cameras) {
        openConnection();
        try (PreparedStatement statement = con.prepareStatement
                ("UPDATE " + DB_TABLE + " SET " + COL_CPU_LOAD + " = ?, " + COL_FREE_SPACE + " = ?, "
                        + COL_ARCHIVE_SIZE + " = ?, " + COL_CLIENTS + " = ?, " + COL_CAMERAS + " = ? WHERE "
                        + COL_ID + " = ?")) {

            statement.setFloat(1, cpuLoad);
            statement.setFloat(2, freeSpace);
            statement.setFloat(3, archiveSize);
            statement.setInt(4, clients);
            statement.setInt(5, cameras);
            statement.setInt(6, id);

            statement.executeUpdate();
        }
        catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Database UPDATE error", e);
        }
    }

    public List<List<String>> selectState(Date minDate, Date maxDate) {
        openConnection();
        try (PreparedStatement statement = con.prepareStatement
                ("SELECT * FROM " + DB_TABLE + " WHERE " + COL_DATE + " >= ? AND " + COL_DATE + " <= ?")) {
            statement.setDate(1, minDate);
            statement.setDate(2, maxDate);
            ResultSet resultSet = statement.executeQuery();
            return createList(resultSet);
        }
        catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Database SELECT error", e);
            return null;
        }
    }

    public List<List<String>> selectState(int idStart, int idEnd) {
        openConnection();
        try (PreparedStatement statement = con.prepareStatement
                ("SELECT * FROM " + DB_TABLE + " WHERE " + COL_ID + " >= ? AND " + COL_ID + " <= ?")) {
            statement.setInt(1, idStart);
            statement.setInt(2, idEnd);
            ResultSet resultSet = statement.executeQuery();
            return createList(resultSet);
        }
        catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Database SELECT error", e);
            return null;
        }
    }

    public String getAvgState(String col, Date minDate, Date maxDate) {
        openConnection();
        try (PreparedStatement statement = con.prepareStatement
                ("SELECT AVG(" + col + ") AS avg_state FROM " + DB_TABLE + " WHERE " + COL_ID + " > 0 AND "
                        + COL_DATE + " >= ? AND " + COL_DATE + " <= ?")) {
            statement.setDate(1, minDate);
            statement.setDate(2, maxDate);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            float f = BigDecimal.valueOf(resultSet.getFloat("avg_state")).setScale(1, BigDecimal.ROUND_HALF_DOWN).floatValue();
            return String.valueOf(f);
        }
        catch (SQLException e) {
            LOGGER.log(Level.ERROR, "Database SELECT AVG error", e);
            return null;
        }
    }

    private List<List<String>> createList(ResultSet resultSet) throws SQLException {
        List<List<String>> lines = new ArrayList<>();
        while (resultSet.next()) {
            List<String> parts = new ArrayList<>();
            lines.add(parts);
            parts.add(String.valueOf(resultSet.getDate("date")));
            parts.add(String.valueOf(resultSet.getTime("time")));
            parts.add(String.valueOf(resultSet.getFloat("cpu_load")));
            parts.add(String.valueOf(resultSet.getFloat("free_space")));
            parts.add(String.valueOf(resultSet.getFloat("archive_size")));
            parts.add(String.valueOf(resultSet.getInt("clients")));
            parts.add(String.valueOf(resultSet.getInt("cameras")));
        }
        return lines;
    }
}
