package com.github.gavvydizzle.parkourrace.commands.course;

import com.github.gavvydizzle.parkourrace.commands.CourseCommandManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CourseHelpCommand extends SubCommand {

    private final CourseCommandManager courseCommandManager;

    public CourseHelpCommand(CourseCommandManager courseCommandManager) {
        this.courseCommandManager = courseCommandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Opens this help menu";
    }

    @Override
    public String getSyntax() {
        return "/" + courseCommandManager.getCommandDisplayName() + " help";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        courseCommandManager.sendHelpMessage(sender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}