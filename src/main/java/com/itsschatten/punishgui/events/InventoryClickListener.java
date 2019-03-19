package com.itsschatten.punishgui.events;

import com.itsschatten.libs.Utils;
import com.itsschatten.punishgui.configs.InventoryConfig;
import com.itsschatten.punishgui.configs.Messages;
import com.itsschatten.punishgui.configs.Settings;
import com.itsschatten.punishgui.inventories.PunishInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryClickListener implements Listener {


    public InventoryClickListener() {
    }


    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player opener = (Player) event.getWhoClicked();

        ItemStack clicked = event.getCurrentItem();
        InventoryType.SlotType slotType = event.getSlotType();

        InventoryConfig invConfig = InventoryConfig.getInstance();

        if (slotType == InventoryType.SlotType.OUTSIDE)
            return;

        if (event.getClickedInventory().getName().contains(Utils.colorize(Settings.PUNISH_INV_NAME.replace("{target}", ""))))
            if (clicked.hasItemMeta()) {
                event.setCancelled(true);

                if (Settings.DISALLOW_SHIFTCLICKING)
                    if (event.isShiftClick() || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                        event.setCancelled(true);
                        return;
                    }

                for (String key : invConfig.getKeys(false)) {

                    if (event.getRawSlot() == invConfig.getInt(key + ".where") - 1) {
                        if (invConfig.getBoolean(key + ".close-on-click"))
                            opener.closeInventory();

                        Utils.debugLog(Settings.DEBUG, "Checking if item contains 'commands'.");
                        if (!invConfig.getConfigurationSection(key).getKeys(false).contains("commands")) {
                            continue;
                        }

                        List<String> commands = new ArrayList<>(invConfig.getStringList(key + ".commands"));

                        for (String command : commands) {
                            if (command.contains("[message]")) {

                                Utils.debugLog(Settings.DEBUG, "Found [message] within a command.");
                                Utils.tell(opener, command.replace("[message]", "")
                                        .replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{sender}", opener.getName())
                                        .replace("{reason}", PunishInventory.getReasonMap().get(opener.getUniqueId())));

                            } else if (command.contains("[console]")) {

                                Utils.debugLog(Settings.DEBUG, "Found [console] within a command, executing '" + command + "' as console.");

                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("[console]", "")
                                        .replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{sender}", opener.getName()).replace("{reason}", PunishInventory.getReasonMap().get(opener.getUniqueId())));
                            } else if (invConfig.getString(key + ".type").equalsIgnoreCase("CONSOLE")) {

                                Utils.debugLog(Settings.DEBUG, "Found 'CONSOLE' as type, executing the command as console.");
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{targetMap}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{sender}", opener.getName())
                                        .replace("{reason}", PunishInventory.getReasonMap().get(opener.getUniqueId())));

                            } else {

                                Utils.debugLog(Settings.DEBUG, "Executing command '" + command + "' for '" + opener.getName() + "'.");
                                opener.performCommand(command
                                        .replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{sender}", opener.getName())
                                        .replace("{reason}", PunishInventory.getReasonMap().get(opener.getUniqueId())));

                            }
                        }
                        Utils.tell(opener, Messages.PUNISHMENT_SUCCESSFUL.replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName()));
                    }
                }
            }
    }


}
