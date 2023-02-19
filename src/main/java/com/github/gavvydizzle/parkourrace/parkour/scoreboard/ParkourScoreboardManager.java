package com.github.gavvydizzle.parkourrace.parkour.scoreboard;

import com.github.gavvydizzle.parkourrace.parkour.ParkourCourse;
import com.github.gavvydizzle.parkourrace.parkour.leaderboard.LeaderboardEntryWithPlacement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ParkourScoreboardManager {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final HashMap<UUID, String> names = new HashMap<>();
    private final ScoreboardManager scoreboardManager;

    public ParkourScoreboardManager() {
        scoreboardManager = Bukkit.getScoreboardManager();
    }

    public void updatePlayerScoreboard(Player player, ParkourCourse course) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("lb", Criteria.DUMMY, "Leaderboard");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        ArrayList<LeaderboardEntryWithPlacement> times = course.getLeaderboard().getEntriesForPlacement(player);

        Score blankScore = objective.getScore("");

        if (times.isEmpty()) {
            Score noTimes = objective.getScore(ChatColor.WHITE + "No times yet");
            blankScore.setScore(2);
            noTimes.setScore(1);
            player.setScoreboard(scoreboard);
            return;
        }

        int count = times.size()+1;
        blankScore.setScore(count--);

        for (LeaderboardEntryWithPlacement time : times) {
            Score score;
            if (time != null) {
                String name;
                if (names.containsKey(time.getLeaderboardEntry().getUuid())) {
                    name = names.get(time.getLeaderboardEntry().getUuid());
                }
                else {
                    name = Bukkit.getOfflinePlayer(time.getLeaderboardEntry().getUuid()).getName();
                    if (name == null) name = "Player";

                    names.put(time.getLeaderboardEntry().getUuid(), name);
                }

                String timeString = df.format(time.getLeaderboardEntry().getCompletionTicks() / 20.0);
                score = objective.getScore(ChatColor.YELLOW + "" + time.getPlacement() + ": " + ChatColor.WHITE + name + " " + ChatColor.GREEN + timeString + "s");
            }
            else {
                score = objective.getScore("...");
            }
            score.setScore(count--);
        }

        player.setScoreboard(scoreboard);
    }

    public void hideScoreboard(Player player) {
        player.setScoreboard(scoreboardManager.getNewScoreboard());
    }



}
