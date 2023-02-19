package com.github.gavvydizzle.parkourrace.parkour;

import org.bukkit.entity.Player;

public class TimedPlayer {

    private final Player player;
    private final int startTime;
    private final int mapID;


    public TimedPlayer(Player player, int startTime, int mapID) {
        this.player = player;
        this.startTime = startTime;
        this.mapID = mapID;
    }

    public Player getPlayer() {
        return player;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getMapID() {
        return mapID;
    }
}
