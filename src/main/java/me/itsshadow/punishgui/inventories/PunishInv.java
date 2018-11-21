package me.itsshadow.punishgui.inventories;

import lombok.AccessLevel;
import lombok.Setter;
import me.itsshadow.libs.Utils;
import me.itsshadow.libs.inventories.InventoryUtils;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PunishInv implements Listener {


    @Setter(value = AccessLevel.PRIVATE)
    private static String reason = null;

    @Setter(value = AccessLevel.PRIVATE)
    private static Player player, target;

    private static Inventory inventory;

    private static InventoryConfig invConfig = InventoryConfig.getInstance();


    public static void createPunishInv(Player player, Player target, String reason) {
        setReason(reason);
        setPlayer(player);
        setTarget(target);

        inventory = Bukkit.createInventory(null, 54, Utils.colorize(Settings.PUNISHINV_NAME).replace("{player}", target.getName()));

        for (String key : invConfig.getKeys(false)) {
            InventoryUtils.createItem(inventory, invConfig.getInt(key + ".where"), Material.matchMaterial(invConfig.getString(key + ".item").toUpperCase()),
                    invConfig.getInt(key + ".amount"), invConfig.getBoolean(key + ".glow"), Utils.colorize(invConfig.getString(key + ".name")), invConfig.getList(key + ".lore"));
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {

    }


}
