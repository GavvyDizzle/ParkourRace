package com.github.gavvydizzle.parkourrace.parkour.leaderboard;

import java.util.UUID;

public class LeaderboardEntry {

    private final UUID uuid;
    private final int completionTicks;
    private final long finishMillis;

    public LeaderboardEntry(UUID uuid, int completionTicks) {
        this.uuid = uuid;
        this.completionTicks = completionTicks;
        this.finishMillis = System.currentTimeMillis();
    }

    public LeaderboardEntry(UUID uuid, int completionTicks, long finishMillis) {
        this.uuid = uuid;
        this.completionTicks = completionTicks;
        this.finishMillis = finishMillis;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getCompletionTicks() {
        return completionTicks;
    }

    public long getFinishMillis() {
        return finishMillis;
    }
}
