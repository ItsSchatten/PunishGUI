package me.itsshadow.punishgui.inventories;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.Utils;
import me.itsshadow.libs.inventories.InventoryUtils;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class PunishInv implements Listener {

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PRIVATE)
    private static String reason = null;

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PRIVATE)
    private static Player player, target;


    public static void createPunishInv(Player player, Player target, String reason) {

        Inventory inventory= Bukkit.createInventory(null, 54, Utils.colorize(Settings.PUNISH_INV_NAME).replace("{player}", target.getName()));

        if (Settings.FILL_SPARE_INV_SPACES) {
            InventoryUtils.setSparePanels(inventory, Material.matchMaterial(Settings.FILL_ITEM.toUpperCase()));
        }


        for (String key : InventoryConfig.getInstance().getKeys(false)) {
            final int where = InventoryConfig.getInstance().getInt(key + ".where");
            final Material mat = Material.matchMaterial(InventoryConfig.getInstance().getString(key + ".item").toUpperCase());
            final int amount = InventoryConfig.getInstance().getInt(key + ".amount");
            final boolean glow = InventoryConfig.getInstance().getBoolean(key + ".glow");
            final String name = Utils.colorize(InventoryConfig.getInstance().getString(key + ".name"));
            final List<String> lore = InventoryConfig.getInstance().getStringList(key + ".lore");

            InventoryUtils.createItem(inventory, where, mat, amount, glow, name, lore.stream().map(entry -> Utils.colorize(entry)).collect(Collectors.toList()));
        }

        setReason(reason);
        setPlayer(player);
        setTarget(target);

        if (Settings.USE_SOUNDS) {
            if (Settings.USE_RANDOM_SOUND_PITCH) {
                player.playSound(player.getLocation(), Sound.valueOf(Settings.PUNISH_OPEN_SOUND), 1, (float) Math.random());
            } else {
                player.playSound(player.getLocation(), Sound.valueOf(Settings.PUNISH_OPEN_SOUND), 1, Settings.SOUND_PITCH);
            }
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Player target = getTarget();
        String reason = getReason();

        ItemStack clicked = event.getCurrentItem();
        InventoryType.SlotType slotType = event.getSlotType();

        if (slotType == InventoryType.SlotType.OUTSIDE) {
            return;
        }

        if (event.getInventory().getName().equalsIgnoreCase(Utils.colorize(Settings.PUNISH_INV_NAME.replace("{player}", target.getName())))) {
            if (clicked.hasItemMeta()) {
                for (String key : InventoryConfig.getInstance().getKeys(false)) {
                    event.setCancelled(true);
                    if (event.getRawSlot() == InventoryConfig.getInstance().getInt(key + ".where") - 1) {
                        Utils.tell(player, InventoryConfig.getInstance().getList(key + ".commands").toString());
                        List<String> commands = InventoryConfig.getInstance().getStringList(key + ".commands");

                        if (InventoryConfig.getInstance().getBoolean(key + ".close-on-click")) {
                            player.closeInventory();
                        }

                        commands.forEach(command -> player.performCommand(command.replace("{sender}", player.getName()).replace("{player}", target.getName()).replace("{reason}", reason)));
                        return;
                    }
                }
            }
        }

        return;

    }


}
