package com.github.gavvydizzle.parkourrace.parkour;

import com.github.gavvydizzle.parkourrace.ParkourRace;
import com.github.mittenmc.serverutils.RepeatingTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TimingManager {

    private static final int MAX_RACE_SECONDS = 300;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final ParkourRace instance;
    private int tick;
    private final HashMap<UUID, TimedPlayer> runningPlayers;

    public TimingManager(ParkourRace instance) {
        this.instance = instance;
        this.runningPlayers = new HashMap<>();
        tick = 0;

        startTickClock();
    }

    private void startTickClock() {
        new RepeatingTask(instance, 0, 1) {
            @Override
            public void run() {
                tick++;

                // TODO - Configurable message
                for (Iterator<Map.Entry<UUID, TimedPlayer>> iterator = runningPlayers.entrySet().iterator(); iterator.hasNext(); ) {
                    TimedPlayer timedPlayer = iterator.next().getValue();

                    if ((tick - timedPlayer.getStartTime()) / 20.0 > MAX_RACE_SECONDS) {
                        iterator.remove();
                    }

                    String message = ChatColor.WHITE + df.format((tick - timedPlayer.getStartTime()) / 20.0) + "s";
                    timedPlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                }
            }
        };
    }

    /**
     * Starts a race attempt for this player
     * @param player The player
     * @param mapID The id of the map
     */
    public void startAttempt(Player player, int mapID) {
        runningPlayers.put(player.getUniqueId(), new TimedPlayer(player, tick, mapID));
    }

    /**
     * Gets the time the player has been running this map.
     * Calling this method removes the player from the map, essentially ending their run.
     * @return A CompletedRace instance or null.
     */
    @Nullable
    public CompletedRace getCompletionObject(Player player) {
        if (!runningPlayers.containsKey(player.getUniqueId())) return null;

        TimedPlayer timedPlayer = runningPlayers.remove(player.getUniqueId());
        return new CompletedRace(timedPlayer.getMapID(), tick - timedPlayer.getStartTime());
    }

    /**
     * Handles when the player leaves a course.
     * Removes the player from the timer list and clears their ActionBar
     * @param player The player
     */
    public void onCourseLeave(Player player) {
        if (runningPlayers.containsKey(player.getUniqueId())) {
            runningPlayers.remove(player.getUniqueId());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
        }
    }

    /**
     * @param player The player
     * @return If the player is currently in a race
     */
    public boolean isPlayerInRace(Player player) {
        return runningPlayers.containsKey(player.getUniqueId());
    }

}
