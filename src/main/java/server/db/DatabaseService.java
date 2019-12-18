package server.db;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private String connection;
    private String user;
    private String pass;

    private Connection con;

    public static final String DB_DRIVER = "org.postgresql.Driver";

    private static final Logger LOGGER = LogManager.getLogger(DatabaseService.class);

    public DatabaseService(String connection, String user, String pass) {
        this.connection = connection;
        this.user = user;
        this.pass = pass;
        con = null;
        openConnection();
    }

    public void openConnection() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName(DB_DRIVER);
                con = DriverManager.getConnection(connection, user, pass);
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

    public void insertStates(float cpuLoad, float freeSpace, float archiveSize, int clients, int cameras) {
        openConnection();
        Date date = new Date(System.currentTimeMillis());
        Time time = new Time(System.currentTimeMillis());
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate("INSERT INTO server_states_table (datetime, cpu_load, free_space, archive_size, " +
                    "clients, cameras) VALUES ('" + date + " " + time + "', " + cpuLoad + ", " + freeSpace + ", " +
                    archiveSize + ", " + clients + ", " + cameras + ")");
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "SQL insert states error", e);
        }

    }

    public List<List<String>> selectStates(Date minDate, Time minTime, Date maxDate, Time maxTime) {
        openConnection();
        List<List<String>> result = new ArrayList<>();
        try (Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM server_states_table WHERE datetime >= '" +
                    minDate + " " + minTime + "' AND datetime <= '" + maxDate + " " + maxTime + "'" );
            result = createList(rs);
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "SQL select states error", e);
        }
        return result;
    }

    public List<String> getAvgStates(Date minDate, Time minTime, Date maxDate, Time maxTime) {
        List<String> result = new ArrayList<>();
        result.add(getAvgState("cpu_load", minDate, minTime, maxDate, maxTime));
        result.add(getAvgState("free_space", minDate, minTime, maxDate, maxTime));
        result.add(getAvgState("archive_size", minDate, minTime, maxDate, maxTime));
        result.add(getAvgState("clients", minDate, minTime, maxDate, maxTime));
        result.add(getAvgState("cameras", minDate, minTime, maxDate, maxTime));
        return result;
    }

    private String getAvgState(String col, Date minDate, Time minTime, Date maxDate, Time maxTime) {
        openConnection();
        String result = "";
        try (Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT AVG(" + col +
                    ") AS avg_state FROM server_states_table WHERE datetime >= '" + minDate + " " + minTime +
                    "' AND datetime <= '" + maxDate + " " + maxTime + "'");
            rs.next();
            float f = BigDecimal.valueOf(rs.getFloat("avg_state")).setScale(1, BigDecimal.ROUND_HALF_DOWN).floatValue();
            result = String.valueOf(f);
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "SQL select average state error. Column: " + col, e);
        }
        return result;
    }

    public List<String> selectReferences() {
        openConnection();
        List<String> result = new ArrayList<>();
        try (Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM references_states");
            rs.next();
            for (int i = 2; i < 7; i++) {
                result.add(rs.getString(i));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "SQL select reference states error", e);
        }
        return result;
    }

    public void updateReferences(int cpuLoad, int freeSpace, int archiveSize, int clients, int cameras) {
        openConnection();
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate("UPDATE references_states SET cpu_load = " + cpuLoad + ", free_space = " +
                    freeSpace + ", archive_size = " + archiveSize + ", clients = " + clients +
                    ", cameras = " + cameras + " WHERE id = 0");
        } catch (SQLException e) {
            LOGGER.log(Level.ERROR, "SQL update reference states error", e);
        }
    }

    private List<List<String>> createList(ResultSet resultSet) throws SQLException {
        List<List<String>> lines = new ArrayList<>();
        while (resultSet.next()) {
            List<String> parts = new ArrayList<>();
            lines.add(parts);
            parts.add(String.valueOf(resultSet.getString("datetime")));
            parts.add(String.valueOf(resultSet.getFloat("cpu_load")));
            parts.add(String.valueOf(resultSet.getFloat("free_space")));
            parts.add(String.valueOf(resultSet.getFloat("archive_size")));
            parts.add(String.valueOf(resultSet.getInt("clients")));
            parts.add(String.valueOf(resultSet.getInt("cameras")));
        }
        return lines;
    }
}
