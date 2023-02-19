package com.github.gavvydizzle.parkourrace.parkour.leaderboard;

import java.util.Comparator;

public class LeaderboardEntryComparator implements Comparator<LeaderboardEntry> {

    // Compares based on the following
    // 1. Lowest completion ticks
    // 2. Lowest completion millis (which time was obtained first)
    // 3. In the very rare event of a tie, UUIDs will be compared
    @Override
    public int compare(LeaderboardEntry o1, LeaderboardEntry o2) {
        int completeTime = Integer.compare(o1.getCompletionTicks(), o2.getCompletionTicks());
        if (completeTime != 0) return completeTime;

        long completeMillis = Long.compare(o1.getFinishMillis(), o2.getFinishMillis());
        if (completeMillis != 0) return completeMillis < 0 ? -1 : 1;

        return o1.getUuid().compareTo(o2.getUuid());
    }
}
