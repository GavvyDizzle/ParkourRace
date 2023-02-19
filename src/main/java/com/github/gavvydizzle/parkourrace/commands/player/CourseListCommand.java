package com.github.gavvydizzle.parkourrace.commands.player;

import com.github.gavvydizzle.parkourrace.commands.PlayerCommandManager;
import com.github.gavvydizzle.parkourrace.gui.InventoryManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CourseListCommand extends SubCommand {

    private final PlayerCommandManager playerCommandManager;
    private final InventoryManager inventoryManager;

    public CourseListCommand(PlayerCommandManager playerCommandManager, InventoryManager inventoryManager) {
        this.playerCommandManager = playerCommandManager;
        this.inventoryManager = inventoryManager;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Opens a list of parkour courses";
    }

    @Override
    public String getSyntax() {
        return "/" + playerCommandManager.getCommandDisplayName() + " list";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        inventoryManager.openCourseListInventory((Player) sender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}