package com.github.gavvydizzle.parkourrace;

import com.github.gavvydizzle.parkourrace.commands.AdminCommandManager;
import com.github.gavvydizzle.parkourrace.commands.CourseCommandManager;
import com.github.gavvydizzle.parkourrace.commands.PlayerCommandManager;
import com.github.gavvydizzle.parkourrace.gui.InventoryManager;
import com.github.gavvydizzle.parkourrace.parkour.ParkourManager;
import com.github.gavvydizzle.parkourrace.parkour.scoreboard.ParkourScoreboardManager;
import com.github.gavvydizzle.parkourrace.storage.Database;
import com.github.gavvydizzle.parkourrace.storage.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ParkourRace extends JavaPlugin {

    // TODO
    // Make list inventory
    // Add inventory items for control
    // Add sounds

    private static ParkourRace instance;
    private Database database;
    private ParkourManager parkourManager;
    private InventoryManager inventoryManager;
    private ParkourScoreboardManager parkourScoreboardManager;
    private AdminCommandManager adminCommandManager;
    private CourseCommandManager courseCommandManager;
    private PlayerCommandManager playerCommandManager;

    @Override
    public void onEnable() {
        instance = this;
        database = new SQLite(this);

        saveDefaultConfig();

        parkourManager = new ParkourManager(instance, database);
        inventoryManager = new InventoryManager(parkourManager);
        parkourScoreboardManager = new ParkourScoreboardManager();

        getServer().getPluginManager().registerEvents(parkourManager, this);
        getServer().getPluginManager().registerEvents(inventoryManager, this);

        try {
            playerCommandManager = new PlayerCommandManager(Objects.requireNonNull(getCommand("parkour")), parkourManager, inventoryManager);
        } catch (NullPointerException e) {
            getLogger().severe("The admin command name was changed in the plugin.yml file. Please make it \"parkour\" and restart the server. You can change the aliases but NOT the command name.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            courseCommandManager = new CourseCommandManager(Objects.requireNonNull(getCommand("course")), parkourManager);
        } catch (NullPointerException e) {
            getLogger().severe("The admin command name was changed in the plugin.yml file. Please make it \"course\" and restart the server. You can change the aliases but NOT the command name.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            adminCommandManager = new AdminCommandManager(Objects.requireNonNull(getCommand("parkouradmin")));
        } catch (NullPointerException e) {
            getLogger().severe("The admin command name was changed in the plugin.yml file. Please make it \"parkouradmin\" and restart the server. You can change the aliases but NOT the command name.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static ParkourRace getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    public ParkourManager getParkourManager() {
        return parkourManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ParkourScoreboardManager getParkourScoreboardManager() {
        return parkourScoreboardManager;
    }

    public AdminCommandManager getAdminCommandManager() {
        return adminCommandManager;
    }

    public CourseCommandManager getCourseCommandManager() {
        return courseCommandManager;
    }

    public PlayerCommandManager getPlayerCommandManager() {
        return playerCommandManager;
    }
}
