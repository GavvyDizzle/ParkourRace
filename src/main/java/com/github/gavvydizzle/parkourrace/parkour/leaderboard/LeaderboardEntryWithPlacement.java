package com.github.gavvydizzle.parkourrace.parkour.leaderboard;

public class LeaderboardEntryWithPlacement {

    private final int placement;
    private final LeaderboardEntry leaderboardEntry;

    public LeaderboardEntryWithPlacement(int placement, LeaderboardEntry leaderboardEntry) {
        this.placement = placement;
        this.leaderboardEntry = leaderboardEntry;
    }

    public int getPlacement() {
        return placement;
    }

    public LeaderboardEntry getLeaderboardEntry() {
        return leaderboardEntry;
    }
}
