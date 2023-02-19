package com.github.gavvydizzle.parkourrace.gui;

import com.github.gavvydizzle.parkourrace.ParkourRace;
import com.github.gavvydizzle.parkourrace.configs.GUIConfig;
import com.github.gavvydizzle.parkourrace.parkour.ParkourManager;
import com.github.gavvydizzle.parkourrace.parkour.ParkourCourse;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.ConfigUtils;
import com.github.mittenmc.serverutils.Numbers;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class InventoryManager implements Listener {

    private final ParkourManager parkourManager;
    private final ArrayList<UUID> playersInGUI;
    private final CourseListMenu courseListMenu;

    private InventoryItem restartItem, courseListItem, leaveCourseItem;

    public InventoryManager(ParkourManager parkourManager) {
        this.parkourManager = parkourManager;
        playersInGUI = new ArrayList<>();
        courseListMenu = new CourseListMenu(parkourManager);
        reload();
    }

    public void reload() {
        FileConfiguration config = GUIConfig.get();
        config.options().copyDefaults(true);
        config.addDefault("hotbarItems.restart.slot", 0);
        config.addDefault("hotbarItems.restart.material", Material.STICK.toString());
        config.addDefault("hotbarItems.restart.name", "&eRestart");
        config.addDefault("hotbarItems.restart.lore", new ArrayList<>());
        config.addDefault("hotbarItems.courseList.slot", 1);
        config.addDefault("hotbarItems.courseList.material", Material.PAPER.toString());
        config.addDefault("hotbarItems.courseList.name", "&eCourse List");
        config.addDefault("hotbarItems.courseList.lore", new ArrayList<>());
        config.addDefault("hotbarItems.leaveCourse.slot", 2);
        config.addDefault("hotbarItems.leaveCourse.material", Material.FEATHER.toString());
        config.addDefault("hotbarItems.leaveCourse.name", "&cLeave Course");
        config.addDefault("hotbarItems.leaveCourse.lore", new ArrayList<>());

        ItemStack restart = new ItemStack(ConfigUtils.getMaterial(config.getString("hotbarItems.restart.material"), Material.STICK));
        ItemMeta meta = restart.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv(config.getString("hotbarItems.restart.name")));
        meta.setLore(Colors.conv(config.getStringList("hotbarItems.restart.lore")));
        restart.setItemMeta(meta);
        restartItem = new InventoryItem(restart, Numbers.constrain(config.getInt("hotbarItems.restart.slot"), 0, 8));

        ItemStack courseList = new ItemStack(ConfigUtils.getMaterial(config.getString("hotbarItems.courseList.material"), Material.PAPER));
        meta = courseList.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv(config.getString("hotbarItems.courseList.name")));
        meta.setLore(Colors.conv(config.getStringList("hotbarItems.courseList.lore")));
        courseList.setItemMeta(meta);
        courseListItem = new InventoryItem(courseList, Numbers.constrain(config.getInt("hotbarItems.courseList.slot"), 0, 8));

        ItemStack leave = new ItemStack(ConfigUtils.getMaterial(config.getString("hotbarItems.leaveCourse.material"), Material.PAPER));
        meta = leave.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv(config.getString("hotbarItems.leaveCourse.name")));
        meta.setLore(Colors.conv(config.getStringList("hotbarItems.leaveCourse.lore")));
        leave.setItemMeta(meta);
        leaveCourseItem = new InventoryItem(leave, Numbers.constrain(config.getInt("hotbarItems.leaveCourse.slot"), 0, 8));

        courseListMenu.reload();

        GUIConfig.save();
    }

    public void openCourseListInventory(Player player) {
        playersInGUI.add(player.getUniqueId());
        courseListMenu.openInventory(player);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }

        if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;
        if (!playersInGUI.contains(e.getWhoClicked().getUniqueId())) return;

        int slot = e.getSlot();

        ParkourCourse parkourMap = parkourManager.getCourseByID(courseListMenu.getCourseIDFromSlot(slot));

        if (parkourMap != null && parkourMap.getTeleportLoc() != null) {
            parkourManager.removeCourse((Player) e.getWhoClicked());
            e.getWhoClicked().teleport(parkourMap.getTeleportLoc());
            parkourManager.selectCourse((Player) e.getWhoClicked(), parkourMap);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
        playersInGUI.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.getInventory().setItem(restartItem.getSlot(), restartItem.getItemStack());
        player.getInventory().setItem(courseListItem.getSlot(), courseListItem.getItemStack());
        player.getInventory().setItem(leaveCourseItem.getSlot(), leaveCourseItem.getItemStack());
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        playersInGUI.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onSpecialItemRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

            int slot = e.getPlayer().getInventory().getHeldItemSlot();

            if (slot == restartItem.getSlot()) {
                ParkourCourse course = parkourManager.getSelectedCourse(e.getPlayer());
                if (course == null) return;

                if (course.getStartLoc() != null) {
                    e.getPlayer().teleport(course.getStartLoc());
                    parkourManager.getTimingManager().startAttempt(e.getPlayer(), course.getId());
                }
            }
            else if (slot == courseListItem.getSlot()) {
                openCourseListInventory(e.getPlayer());
            }
            else if (slot == leaveCourseItem.getSlot()) {
                parkourManager.removeCourse(e.getPlayer());
                ParkourRace.getInstance().getParkourScoreboardManager().hideScoreboard(e.getPlayer());
            }

        }
    }

    public CourseListMenu getCourseListMenu() {
        return courseListMenu;
    }
}
