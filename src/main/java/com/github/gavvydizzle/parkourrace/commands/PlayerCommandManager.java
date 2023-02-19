package com.github.gavvydizzle.parkourrace.commands;

import com.github.gavvydizzle.parkourrace.ParkourRace;
import com.github.gavvydizzle.parkourrace.commands.player.CourseListCommand;
import com.github.gavvydizzle.parkourrace.commands.player.CourseTeleportCommand;
import com.github.gavvydizzle.parkourrace.commands.player.PlayerHelpCommand;
import com.github.gavvydizzle.parkourrace.configs.CommandsConfig;
import com.github.gavvydizzle.parkourrace.gui.InventoryManager;
import com.github.gavvydizzle.parkourrace.parkour.ParkourManager;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerCommandManager implements TabExecutor {

    private final PluginCommand command;
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    private final ArrayList<String> subcommandStrings = new ArrayList<>();
    private String commandDisplayName, helpCommandPadding;

    public PlayerCommandManager(PluginCommand command, ParkourManager parkourManager, InventoryManager inventoryManager) {
        this.command = command;
        command.setExecutor(this);

        subcommands.add(new CourseListCommand(this, inventoryManager));
        subcommands.add(new CourseTeleportCommand(this, parkourManager));
        subcommands.add(new PlayerHelpCommand(this));

        for (SubCommand subCommand : subcommands) {
            subcommandStrings.add(subCommand.getName());
        }

        reload();
    }

    // Call before AdminCommandManager's reload
    public void reload() {
        FileConfiguration config = CommandsConfig.get();
        config.options().copyDefaults(true);

        config.addDefault("commandDisplayName.player", command.getName());
        config.addDefault("helpCommandPadding.player", "&6-----(" + ParkourRace.getInstance().getName() + " Commands)-----");

        commandDisplayName = config.getString("commandDisplayName.player");
        helpCommandPadding = Colors.conv(config.getString("helpCommandPadding.player"));
    }

    public String getCommandDisplayName() {
        return commandDisplayName;
    }

    public String getHelpCommandPadding() {
        return helpCommandPadding;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {

                    SubCommand subCommand = subcommands.get(i);

                    subCommand.perform(sender, args);
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "Invalid command");
        }
        sender.sendMessage(ChatColor.YELLOW + "Use '/" + commandDisplayName + " help' to see a list of valid commands");

        return true;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> subcommandsArguments = new ArrayList<>();

            StringUtil.copyPartialMatches(args[0], subcommandStrings, subcommandsArguments);

            return subcommandsArguments;
        }
        else if (args.length >= 2) {
            for (SubCommand subcommand : subcommands) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    return subcommand.getSubcommandArguments((Player) sender, args);
                }
            }
        }

        return null;
    }

    public void sendHelpMessage(CommandSender sender) {
        String padding = getHelpCommandPadding();

        if (!padding.isEmpty()) sender.sendMessage(padding);
        for (SubCommand subCommand : subcommands) {
            sender.sendMessage(ChatColor.GOLD + subCommand.getSyntax() + " - " + ChatColor.YELLOW + subCommand.getDescription());
        }
        if (!padding.isEmpty()) sender.sendMessage(padding);
    }

    public PluginCommand getCommand() {
        return command;
    }
}