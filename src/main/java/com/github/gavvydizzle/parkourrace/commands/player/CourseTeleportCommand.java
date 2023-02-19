package com.github.gavvydizzle.parkourrace.commands.player;

import com.github.gavvydizzle.parkourrace.commands.PlayerCommandManager;
import com.github.gavvydizzle.parkourrace.parkour.ParkourManager;
import com.github.gavvydizzle.parkourrace.parkour.ParkourCourse;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CourseTeleportCommand extends SubCommand {

    private final PlayerCommandManager playerCommandManager;
    private final ParkourManager parkourManager;

    public CourseTeleportCommand(PlayerCommandManager playerCommandManager, ParkourManager parkourManager) {
        this.playerCommandManager = playerCommandManager;
        this.parkourManager = parkourManager;
    }

    @Override
    public String getName() {
        return "tp";
    }

    @Override
    public String getDescription() {
        return "Teleport to a parkour course";
    }

    @Override
    public String getSyntax() {
        return "/" + playerCommandManager.getCommandDisplayName() + " tp <id>";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length < 2) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid number provided");
            return;
        }

        ParkourCourse parkourMap = parkourManager.getCourseByID(id);
        if (parkourMap == null) {
            sender.sendMessage(ChatColor.RED + "No map exists with the id: " + id);
            return;
        }

        if (parkourMap.getTeleportLoc() != null) {
            ((Player) sender).teleport(parkourMap.getTeleportLoc());
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}