package com.github.gavvydizzle.parkourrace.parkour;

import com.github.gavvydizzle.parkourrace.ParkourRace;
import com.github.gavvydizzle.parkourrace.configs.CoursesConfig;
import com.github.gavvydizzle.parkourrace.parkour.leaderboard.LeaderboardEntry;
import com.github.gavvydizzle.parkourrace.storage.Database;
import com.github.mittenmc.serverutils.Colors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

public class ParkourManager implements Listener {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final ParkourRace instance;
    private final Database database;
    private final HashMap<Integer, ParkourCourse> maps;
    private final HashMap<UUID, ParkourCourse> selectedCourses;
    private final TimingManager timingManager;
    private int nextAvailableCourseID;

    public ParkourManager(ParkourRace instance, Database database) {
        this.instance = instance;
        this.database = database;
        this.maps = new HashMap<>();
        this.selectedCourses = new HashMap<>();
        timingManager = new TimingManager(instance);
        nextAvailableCourseID = 1;

        loadCourses();
        // Load data from the database async
        Bukkit.getScheduler().runTaskAsynchronously(instance, this::loadLeaderboards);
    }

    /**
     * Reloads course locations of loaded courses from the config
     */
    public void reload() {
        FileConfiguration config = CoursesConfig.get();

        for (ParkourCourse course : maps.values()) {
            course.setTeleportLoc(config.getLocation("courses." + course.getId() + ".teleportLocation"));
            course.setStartLoc(config.getLocation("courses." + course.getId() + ".startLocation"));
            course.setFinishLoc(config.getLocation("courses." + course.getId() + ".finishLocation"));
        }
    }

    public void loadCourses() {
        FileConfiguration config = CoursesConfig.get();
        config.options().copyDefaults(true);
        config.addDefault("courses", new HashMap<>());
        CoursesConfig.save();

        if (config.getConfigurationSection("courses") != null) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("courses")).getKeys(false)) {
                String path = "courses." + key;
                int id;
                try {
                    id = Integer.parseInt(key);
                } catch (Exception ignored) {
                    instance.getLogger().warning("Map " + key + " in courses.yml does not have a valid name! It will not be loaded");
                    continue;
                }

                // Store the max ID
                if (id >= nextAvailableCourseID) {
                    nextAvailableCourseID = id + 1;
                }

                maps.put(id, new ParkourCourse(
                        id,
                        config.getLocation(path + ".teleportLocation"),
                        config.getLocation(path + ".startLocation"),
                        config.getLocation(path + ".finishLocation")
                ));
            }
        }
    }

    private void loadLeaderboards() {
        for (ParkourCourse course : maps.values()) {
            database.createCourseLeaderboard(course); // Try to create it in case it has been deleted
            course.getLeaderboard().addEntries(database.getCourseLeaderboard(course));
        }
    }

    public void createNewCourse(CommandSender sender) {
        ParkourCourse course = new ParkourCourse(nextAvailableCourseID);
        maps.put(nextAvailableCourseID, course);

        FileConfiguration config = CoursesConfig.get();
        config.addDefault("courses." + nextAvailableCourseID + ".teleportLocation", null);
        config.addDefault("courses." + nextAvailableCourseID + ".startLocation", null);
        config.addDefault("courses." + nextAvailableCourseID + ".finishLocation", null);
        CoursesConfig.save();

        sender.sendMessage(ChatColor.GREEN + "Successfully created course " + nextAvailableCourseID);

        nextAvailableCourseID++;

        instance.getInventoryManager().getCourseListMenu().updateOnCourseCreation();
        database.createCourseLeaderboard(course);
    }


    /**
     * Attempts to complete a race for any player that steps on a LIGHT_WEIGHTED_PRESSURE_PLATE.
     * If the player is not currently in a race then this will fail silently.
     * @param e The event
     */
    @EventHandler
    private void onCourseFinish(PlayerInteractEvent e) {
        // Must be valid pressure plate interaction
        if (e.getAction() != Action.PHYSICAL) return;

        // Player must be running a race
        if (!timingManager.isPlayerInRace(e.getPlayer())) return;

        // Must have interacted with a golden pressure plate
        if (e.getClickedBlock() != null && e.getClickedBlock().getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) return;

        CompletedRace completedRace = timingManager.getCompletionObject(e.getPlayer());
        if (completedRace == null) return;

        ParkourCourse course = maps.get(completedRace.getMapID());
        if (course == null) return;

        if (course.getFinishLoc() == null || !course.getFinishLoc().getBlock().equals(e.getClickedBlock())) return;

        e.setCancelled(true);

        String message = df.format(completedRace.getTicks() / 20.0) + "s";

        LeaderboardEntry leaderboardEntry = course.getLeaderboard().addEntry(e.getPlayer().getUniqueId(), completedRace.getTicks());

        if (leaderboardEntry != null) {
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + message));
            ParkourRace.getInstance().getParkourScoreboardManager().updatePlayerScoreboard(e.getPlayer(), course);
            database.saveNewBestTime(course, leaderboardEntry);
        }
        else {
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                    Colors.conv("<SOLID:ffa08b>" + message)));
        }

        // Teleport the player back to the start on completion
        if (course.getStartLoc() != null) {
            e.getPlayer().teleport(course.getStartLoc());
        }
    }

    @Nullable
    public ParkourCourse getCourseByID(int id) {
        return maps.get(id);
    }

    public ArrayList<Integer> getSortedCourseIDs() {
        ArrayList<Integer> arr = new ArrayList<>(maps.keySet());
        Collections.sort(arr);

        return arr;
    }

    public void setCourseLocation(CourseLocationType locationType, ParkourCourse parkourMap, Location location) {
        FileConfiguration config = CoursesConfig.get();

        switch (locationType) {
            case FINISH:
                parkourMap.setFinishLoc(location);
                config.set("courses." + parkourMap.getId() + ".finishLocation", location);
                break;
            case START:
                parkourMap.setStartLoc(location);
                config.set("courses." + parkourMap.getId() + ".startLocation", location);
                break;
            case TELEPORT:
                parkourMap.setTeleportLoc(location);
                config.set("courses." + parkourMap.getId() + ".teleportLocation", location);
                break;
        }
        CoursesConfig.save();
    }


    public void selectCourse(Player player, ParkourCourse course) {
        selectedCourses.put(player.getUniqueId(), course);
        ParkourRace.getInstance().getParkourScoreboardManager().updatePlayerScoreboard(player, course);
    }

    public void removeCourse(Player player) {
        selectedCourses.remove(player.getUniqueId());
        timingManager.onCourseLeave(player);
    }

    @Nullable
    public ParkourCourse getSelectedCourse(Player player) {
        return selectedCourses.get(player.getUniqueId());
    }


    public TimingManager getTimingManager() {
        return timingManager;
    }
}
