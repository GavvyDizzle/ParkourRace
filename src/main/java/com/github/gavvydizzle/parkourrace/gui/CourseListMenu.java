package com.github.gavvydizzle.parkourrace.gui;

import com.github.gavvydizzle.parkourrace.ParkourRace;
import com.github.gavvydizzle.parkourrace.parkour.ParkourManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CourseListMenu {

    private Inventory inventory;
    private final ParkourManager parkourManager;
    private ArrayList<Integer> courseIDs;

    private ItemStack template;

    public CourseListMenu(ParkourManager parkourManager) {
        this.parkourManager = parkourManager;
        courseIDs = new ArrayList<>();
        reload();

        Bukkit.getScheduler().runTaskLater(ParkourRace.getInstance(), this::updateOnCourseCreation, 10);
    }

    public void reload() {
        inventory = Bukkit.createInventory(null, 54, "Course Selection");

        template = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta meta = template.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW + "Course {id}");
        template.setItemMeta(meta);
    }

    public void updateOnCourseCreation() {
        courseIDs = new ArrayList<>(parkourManager.getSortedCourseIDs());

        ItemStack[] contents = new ItemStack[54];
        for (int i = 0; i < Math.min(54, courseIDs.size()); i++) {
            ItemStack itemStack = template.clone();
            ItemMeta meta = template.getItemMeta();
            assert meta != null;
            meta.setDisplayName(meta.getDisplayName().replace("{id}", "" + courseIDs.get(i)));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
            contents[i] = itemStack;
        }

        inventory.setContents(contents);
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    public int getCourseIDFromSlot(int slot) {
        if (slot < 0 || slot >= Math.min(54, courseIDs.size())) return -1;

        return courseIDs.get(slot);
    }

}
