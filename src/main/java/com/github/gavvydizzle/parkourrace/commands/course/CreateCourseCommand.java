package com.github.gavvydizzle.parkourrace.commands.course;

import com.github.gavvydizzle.parkourrace.commands.CourseCommandManager;
import com.github.gavvydizzle.parkourrace.parkour.ParkourManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CreateCourseCommand extends SubCommand {

    private final CourseCommandManager courseCommandManager;
    private final ParkourManager parkourManager;

    public CreateCourseCommand(CourseCommandManager courseCommandManager, ParkourManager parkourManager) {
        this.courseCommandManager = courseCommandManager;
        this.parkourManager = parkourManager;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates a new parkour course";
    }

    @Override
    public String getSyntax() {
        return "/" + courseCommandManager.getCommandDisplayName() + " course";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        parkourManager.createNewCourse(sender);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Collections.emptyList();
    }
}