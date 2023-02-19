package com.github.gavvydizzle.parkourrace.parkour.leaderboard;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

// TODO - Optimise using a BST if there is time
public class Leaderboard {

    // This list will always be sorted according to the LeaderboardEntryComparator
    private final ArrayList<LeaderboardEntry> entries;
    private final LeaderboardEntryComparator leaderboardEntryComparator;

    public Leaderboard() {
        this.entries = new ArrayList<>();
        leaderboardEntryComparator = new LeaderboardEntryComparator();
    }

    /**
     * @param placement The placement (from 1-n)
     * @return True if the leaderboard contains and entry for this position.
     */
    public boolean hasEntry(int placement) {
        return entries.size() >= placement;
    }

    /**
     * @param player The player
     * @return The player's placement or -1 if they do not have a time yet
     */
    public int getPlayerPlacement(Player player) {
        UUID uuid = player.getUniqueId();
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getUuid().equals(uuid)) return i+1;
        }
        return -1;
    }

    /**
     * @param placement The placement (from 1-n)
     * @return The Leaderboard entry for this placement if one exists or null
     */
    @Nullable
    public LeaderboardEntry getLeaderboardEntry(int placement) {
        return entries.get(placement-1);
    }

    /**
     * Collects a list of LeaderboardEntryWithPlacement based on the given placement.
     * A null element in the list represents a break in the scores.
     * @param placement The placement
     * @return A list of LeaderboardEntryWithPlacement
     */
    public ArrayList<LeaderboardEntryWithPlacement> getEntriesForPlacement(int placement) {
        // The following is how leaderboard entries will be provided
        // Placement 1-4: Top 5 times
        // Placement 5: Top 6 times
        // Placement 6: Top 7 times
        // Placement 7: Top 8 times
        // Placement last: Top 5 times ... two above, time
        // Placements 8-(last-1): Top 5 times ... one above, time, one below

        ArrayList<LeaderboardEntryWithPlacement> arr = new ArrayList<>();
        int leaderboardSize = entries.size();

        if (placement <= 4) {
            for (int i = 0; i < Math.min(5, leaderboardSize); i++) {
                arr.add(new LeaderboardEntryWithPlacement(i+1, entries.get(i)));
            }
        }
        else if (placement == 5) {
            for (int i = 0; i < Math.min(6, leaderboardSize); i++) {
                arr.add(new LeaderboardEntryWithPlacement(i+1, entries.get(i)));
            }
        }
        else if (placement == 6) {
            for (int i = 0; i < Math.min(7, leaderboardSize); i++) {
                arr.add(new LeaderboardEntryWithPlacement(i+1, entries.get(i)));
            }
        }
        else if (placement == 7) {
            for (int i = 0; i < Math.min(8, leaderboardSize); i++) {
                arr.add(new LeaderboardEntryWithPlacement(i+1, entries.get(i)));
            }
        }
        else if (placement == leaderboardSize) {
            for (int i = 0; i < 5; i++) {
                arr.add(new LeaderboardEntryWithPlacement(i+1, entries.get(i)));
            }
            arr.add(null);
            arr.add(new LeaderboardEntryWithPlacement(placement-2, entries.get(placement-3)));
            arr.add(new LeaderboardEntryWithPlacement(placement-1, entries.get(placement-2)));
            arr.add(new LeaderboardEntryWithPlacement(placement, entries.get(placement-1)));
        }
        else {
            for (int i = 0; i < 5; i++) {
                arr.add(new LeaderboardEntryWithPlacement(i+1, entries.get(i)));
            }
            arr.add(null);
            arr.add(new LeaderboardEntryWithPlacement(placement-1, entries.get(placement-2)));
            arr.add(new LeaderboardEntryWithPlacement(placement, entries.get(placement-1)));
            arr.add(new LeaderboardEntryWithPlacement(placement+1, entries.get(placement)));
        }

        return arr;
    }

    /**
     * Collects a list of LeaderboardEntryWithPlacement based on the given placement.
     * A null element in the list represents a break in the scores.
     * @param player The player
     * @return A list of LeaderboardEntryWithPlacement
     */
    public ArrayList<LeaderboardEntryWithPlacement> getEntriesForPlacement(Player player) {
        int placement = getPlayerPlacement(player);
        if (placement == -1) placement = 1;
        return getEntriesForPlacement(placement);
    }

    /**
     * Adds an entry to the leaderboard.
     * This entry will only be added if the time is faster than the player's current fastest time
     * @param uuid The player's UUID
     * @param completionTicks The completion time in ticks
     * @return A LeaderboardEntry object if the time is a new best, null otherwise
     */
    public LeaderboardEntry addEntry(UUID uuid, int completionTicks) {
        Iterator<LeaderboardEntry> iterator = entries.iterator();

        while (iterator.hasNext()) {
            LeaderboardEntry entry = iterator.next();

            if (entry.getUuid().equals(uuid)) {
                if (entry.getCompletionTicks() <= completionTicks) {
                    return null;
                }
                else {
                    iterator.remove();
                    LeaderboardEntry leaderboardEntry = new LeaderboardEntry(uuid, completionTicks);
                    entries.add(leaderboardEntry);
                    entries.sort(leaderboardEntryComparator);
                    return leaderboardEntry;
                }
            }
        }

        LeaderboardEntry leaderboardEntry = new LeaderboardEntry(uuid, completionTicks);
        entries.add(leaderboardEntry);
        entries.sort(leaderboardEntryComparator);
        return leaderboardEntry;
    }

    /**
     * This method is intended to be called only by the database when loading times.
     * @param leaderboardEntryCollection A collection of leaderboard entries
     */
    public void addEntries(Collection<LeaderboardEntry> leaderboardEntryCollection) {
        entries.addAll(leaderboardEntryCollection);
        entries.sort(leaderboardEntryComparator);
    }

}
