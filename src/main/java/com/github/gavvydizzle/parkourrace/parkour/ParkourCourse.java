package com.github.gavvydizzle.parkourrace.parkour;

import com.github.gavvydizzle.parkourrace.parkour.leaderboard.Leaderboard;
import org.bukkit.Location;

import javax.annotation.Nullable;

public class ParkourCourse {

    private final int id;
    private Location teleportLoc, startLoc, finishLoc;
    private final Leaderboard leaderboard;

    public ParkourCourse(int id) {
        this.id = id;
        this.teleportLoc = null;
        this.startLoc = null;
        this.finishLoc = null;
        this.leaderboard = new Leaderboard();
    }

    public ParkourCourse(int id, Location teleportLoc, Location startLoc, Location finishLoc) {
        this.id = id;
        this.teleportLoc = teleportLoc;
        this.startLoc = startLoc;
        this.finishLoc = finishLoc;
        this.leaderboard = new Leaderboard();
    }


    /**
     * Gets the name of this map's table in the database
     * @return The name as a string
     */
    public String getTableName() {
        return "course_" + id;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public Location getTeleportLoc() {
        return teleportLoc;
    }

    public void setTeleportLoc(Location teleportLoc) {
        this.teleportLoc = teleportLoc;
    }

    @Nullable
    public Location getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(Location startLoc) {
        this.startLoc = startLoc;
    }

    @Nullable
    public Location getFinishLoc() {
        return finishLoc;
    }

    public void setFinishLoc(Location finishLoc) {
        this.finishLoc = finishLoc;
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }
}
