package com.github.gavvydizzle.parkourrace.commands.course;

import com.github.gavvydizzle.parkourrace.commands.CourseCommandManager;
import com.github.gavvydizzle.parkourrace.parkour.CourseLocationType;
import com.github.gavvydizzle.parkourrace.parkour.ParkourCourse;
import com.github.gavvydizzle.parkourrace.parkour.ParkourManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SetLocationCommands extends SubCommand {

    private final CourseCommandManager courseCommandManager;
    private final ParkourManager parkourManager;
    private final ArrayList<String> subArgs;

    public SetLocationCommands(CourseCommandManager courseCommandManager, ParkourManager parkourManager) {
        this.courseCommandManager = courseCommandManager;
        this.parkourManager = parkourManager;
        this.subArgs = new ArrayList<>(3);
        subArgs.addAll(Arrays.asList("finish", "start", "teleport"));
    }

    @Override
    public String getName() {
        return "setLocation";
    }

    @Override
    public String getDescription() {
        return "Sets the location for a course";
    }

    @Override
    public String getSyntax() {
        return "/" + courseCommandManager.getCommandDisplayName() + " setLocation <id> <loc-type> [x] [y] [z] [pitch] [yaw]";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length < 3) {
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

        CourseLocationType locationType = CourseLocationType.get(args[2]);
        if (locationType == null) {
            sender.sendMessage(ChatColor.RED + "Invalid location type");
            return;
        }


        if (args.length < 8) { // Use player location
            parkourManager.setCourseLocation(locationType, parkourMap, ((Player) sender).getLocation());
            sender.sendMessage(ChatColor.GREEN + "Updated course " + id + "'s " + locationType + " location to your current location");
        }
        else { // Use provided location
            double x,y,z;
            float pitch,yaw;

            try {
                x = Double.parseDouble(args[3]);
                y = Double.parseDouble(args[4]);
                z = Double.parseDouble(args[5]);
                yaw = Float.parseFloat(args[6]);
                pitch = Float.parseFloat(args[7]);
            } catch (Exception ignored) {
                sender.sendMessage(ChatColor.RED + "Invalid location args. You need to input numbers");
                return;
            }

            Location location = new Location(((Player) sender).getWorld(), x, y, z, yaw, pitch);
            parkourManager.setCourseLocation(locationType, parkourMap, location);
            sender.sendMessage(ChatColor.GREEN + "Updated course " + id + "'s " + locationType + " location to " + location);
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 2) {
            return null;
        }
        else if (args.length == 3) {
            ArrayList<String> list = new ArrayList<>();
            StringUtil.copyPartialMatches(args[2], subArgs, list);
            return list;
        }

        return new ArrayList<>();
    }
}