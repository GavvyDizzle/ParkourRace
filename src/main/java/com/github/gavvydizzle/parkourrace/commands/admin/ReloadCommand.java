package com.github.gavvydizzle.parkourrace.commands.admin;

import com.github.gavvydizzle.parkourrace.ParkourRace;
import com.github.gavvydizzle.parkourrace.commands.AdminCommandManager;
import com.github.gavvydizzle.parkourrace.configs.CommandsConfig;
import com.github.gavvydizzle.parkourrace.configs.CoursesConfig;
import com.github.gavvydizzle.parkourrace.configs.GUIConfig;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends SubCommand {

    private final AdminCommandManager adminCommandManager;
    private final ArrayList<String> argsList;

    public ReloadCommand(AdminCommandManager adminCommandManager) {
        this.adminCommandManager = adminCommandManager;

        argsList = new ArrayList<>();
        argsList.add("commands");
        argsList.add("courses");
        argsList.add("gui");
        argsList.add("sounds");
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads this plugin or a specified portion";
    }

    @Override
    public String getSyntax() {
        return "/" + adminCommandManager.getCommandDisplayName() + " reload [type]";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            switch (args[1].toLowerCase()) {
                case "commands":
                    reloadCommands();
                    sender.sendMessage(ChatColor.GREEN + "[" + ParkourRace.getInstance().getName() + "] " + "Successfully reloaded commands");
                    break;
                case "courses":
                    reloadCourses();
                    sender.sendMessage(ChatColor.GREEN + "[" + ParkourRace.getInstance().getName() + "] " + "Successfully reloaded course locations");
                    break;
                case "gui":
                    reloadGUI();
                    sender.sendMessage(ChatColor.GREEN + "[" + ParkourRace.getInstance().getName() + "] " + "Successfully reloaded GUI");
                    break;
                case "sounds":
                    reloadSounds();
                    sender.sendMessage(ChatColor.GREEN + "[" + ParkourRace.getInstance().getName() + "] " + "Successfully reloaded sounds");
                    break;
            }
        }
        else {
            reloadCommands();
            reloadCourses();
            reloadGUI();
            reloadSounds();
            sender.sendMessage(ChatColor.GREEN + "[" + ParkourRace.getInstance().getName() + "] " + "Successfully reloaded");
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], argsList, list);
        }

        return list;
    }

    private void reloadCommands() {
        CommandsConfig.reload();
        ParkourRace.getInstance().getPlayerCommandManager().reload();
        ParkourRace.getInstance().getCourseCommandManager().reload();
        ParkourRace.getInstance().getAdminCommandManager().reload();
    }

    private void reloadCourses() {
        CoursesConfig.reload();
        ParkourRace.getInstance().getParkourManager().reload();
    }

    private void reloadGUI() {
        GUIConfig.reload();
        ParkourRace.getInstance().getInventoryManager().reload();
    }

    private void reloadSounds() {
        //SoundsConfig.reload();
        //Sounds.reload();
    }
}