package dev.arenamenu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ArenaMenu extends JavaPlugin implements Listener {

    /** Spawn 1 of each arena (slot corner + spawn offset), Y 150 so you land above it. */
    private record Arena(String name, Material icon, int x, int y, int z) {}

    private static final List<Arena> ARENAS = List.of(
            new Arena("Pinewood",  Material.SPRUCE_LOG,    512,  150, 481),
            new Arena("Mesa",      Material.TERRACOTTA,    1536, 150, 481),
            new Arena("Greenfield", Material.GRASS_BLOCK,  2560, 150, 481),
            new Arena("Dunes",     Material.SAND,          3584, 150, 481),
            new Arena("Tundra",    Material.SNOW_BLOCK,    4608, 150, 481),
            new Arena("Savanna",   Material.ACACIA_LOG,    5632, 150, 481)
    );

    /** 9-slot chest, six arenas in slots 1-6. */
    private static final int FIRST_SLOT = 1;

    private static final class MenuHolder implements InventoryHolder {
        private Inventory inventory;
        @Override public @NotNull Inventory getInventory() { return inventory; }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        openMenu(player);
        return true;
    }

    private void openMenu(Player player) {
        MenuHolder holder = new MenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 9, Component.text("Arenas"));
        holder.inventory = inv;

        for (int i = 0; i < ARENAS.size(); i++) {
            Arena arena = ARENAS.get(i);
            ItemStack item = new ItemStack(arena.icon());
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(arena.name(), NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(Component.text("Click to teleport", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)));
            item.setItemMeta(meta);
            inv.setItem(FIRST_SLOT + i, item);
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof MenuHolder)) return;
        event.setCancelled(true); // nothing can be moved in or out of the menu

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int index = event.getSlot() - FIRST_SLOT;
        if (index < 0 || index >= ARENAS.size()) return;

        Arena arena = ARENAS.get(index);
        player.closeInventory();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "execute in duelcore:arenas run tp " + player.getName()
                        + " " + arena.x() + " " + arena.y() + " " + arena.z());
        player.sendMessage(Component.text("Teleported to " + arena.name() + ".", NamedTextColor.GREEN));
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof MenuHolder) {
            event.setCancelled(true);
        }
    }
}
