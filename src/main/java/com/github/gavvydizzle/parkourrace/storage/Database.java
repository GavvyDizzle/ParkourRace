package com.github.gavvydizzle.parkourrace.storage;

import com.github.gavvydizzle.parkourrace.ParkourRace;
import com.github.gavvydizzle.parkourrace.parkour.ParkourCourse;
import com.github.gavvydizzle.parkourrace.parkour.leaderboard.LeaderboardEntry;
import com.github.mittenmc.serverutils.UUIDConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

public abstract class Database {

    private final String CREATE_COURSE_LEADERBOARD = "CREATE TABLE IF NOT EXISTS {table_name}(" +
            "uuid BINARY(16) NOT NULL," +
            "ticks INT       NOT NULL," +
            "time BIGINT     NOT NULL," +
            "PRIMARY KEY (uuid)" +
            ");";

    private final String LOAD_DATA = "SELECT * FROM {table_name} ORDER BY ticks ASC;";

    private final String UPSERT_COMPLETION = "INSERT OR REPLACE INTO {table_name}(uuid, ticks, time) VALUES(?,?,?);";

    private final String DELETE_TABLE = "DROP TABLE {table_name};";

    ParkourRace plugin;
    Connection connection;

    public Database(ParkourRace instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    /**
     * If the table for this map does not exist, one will be created
     * @param map The ParkourMap instance
     */
    public void createCourseLeaderboard(ParkourCourse map) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(CREATE_COURSE_LEADERBOARD.replace("{table_name}", map.getTableName()));
            ps.execute();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    /**
     * Retrieves the leaderboard times for this course
     * @param parkourMap The course
     * @return A list of sorted times for this course
     */
    public Collection<LeaderboardEntry> getCourseLeaderboard(ParkourCourse parkourMap) {
        Connection conn = null;
        PreparedStatement ps = null;
        Collection<LeaderboardEntry> times = new ArrayList<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(LOAD_DATA.replace("{table_name}", parkourMap.getTableName()));
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                times.add(new LeaderboardEntry(
                        UUIDConverter.convert(resultSet.getBytes(1)),
                        resultSet.getInt(2),
                        resultSet.getLong(3)
                ));
            }

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return times;
    }

    /**
     * Updates or adds a new entry to this course's leaderboard
     * @param parkourMap The course
     * @param leaderboardEntry The new leaderboard entry
     */
    public void saveNewBestTime(ParkourCourse parkourMap, LeaderboardEntry leaderboardEntry) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(UPSERT_COMPLETION.replace("{table_name}", parkourMap.getTableName()));
            ps.setBytes(1, UUIDConverter.convert(leaderboardEntry.getUuid()));
            ps.setInt(2, leaderboardEntry.getCompletionTicks());
            ps.setLong(3, leaderboardEntry.getFinishMillis());
            ps.execute();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    /**
     * Deletes the table in the database for this course
     * @param parkourMap The course
     */
    public void deleteTable(ParkourCourse parkourMap) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(DELETE_TABLE.replace("{table_name}", parkourMap.getTableName()));
            ps.execute();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

}