package com.github.gavvydizzle.parkourrace.parkour;

public class CompletedRace {

    private final int mapID, ticks;

    public CompletedRace(int mapID, int ticks) {
        this.mapID = mapID;
        this.ticks = ticks;
    }

    public int getMapID() {
        return mapID;
    }

    public int getTicks() {
        return ticks;
    }
}
