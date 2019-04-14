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

/**
 * This class contains all the fancy things that make the inventory work.
 */
public class InventoryClickListener implements Listener {


    public InventoryClickListener() {
    }


    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player opener = (Player) event.getWhoClicked(); // Set the player to an object so it is easier to get later.

        ItemStack clicked = event.getCurrentItem(); // Set the clicked item to a object so it is easier to get later.
        InventoryType.SlotType slotType = event.getSlotType(); // Sets the slot type to an object.

        InventoryConfig invConfig = InventoryConfig.getInstance(); // Get the instance of the InventoryConfig.

        if (slotType == InventoryType.SlotType.OUTSIDE) // If the player clicks outside of the inventory, ignore it.
            return;

        if (event.getClickedInventory().getName().contains(Utils.colorize(Settings.PUNISH_INV_NAME.replace("{target}", "")))) // The name of the clicked inventory is similar to the one defined in the config.
            if (clicked.hasItemMeta()) { // Make sure that the item has item meta.
                event.setCancelled(true); // Set the event to canceled so no items can be removed from the inventory.

                if (Settings.DISALLOW_SHIFTCLICKING) // A check to make sure that the items cannot be pulled from the inventory when shift clicking them.
                    if (event.isShiftClick() || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                        event.setCancelled(true); // If it is a shift click, cancel the event (again) and return the method so nothing will happen.
                        return;
                    }

                for (String key : invConfig.getKeys(false)) { // Get all the keys inside of the InventoryConfig file.

                    if (event.getRawSlot() == invConfig.getInt(key + ".where") - 1) { // Checking to make sure that the clicked slot is equal to the one defined in the config.
                        if (invConfig.getBoolean(key + ".close-on-click")) // If "close-on-click" is true, closed the menu for the player.
                            opener.closeInventory();

                        Utils.debugLog(Settings.DEBUG, "Checking if item contains 'commands'."); // Checking if an
                        if (!invConfig.getConfigurationSection(key).getKeys(false).contains("commands") || invConfig.getString(key + ".type").equalsIgnoreCase("NONE")) {
                            continue; // If the inventory doesn't contain commands, or the type ios of "NONE" continue the for loop.
                        }

                        List<String> commands = new ArrayList<>(invConfig.getStringList(key + ".commands")); // Set all the commands to an ArrayList so we can iterate through it.

                        for (String command : commands) {
                            if (command.contains("[message]")) { // If the message contains "[message]" anywhere throughout the command it will take that and send a message to the player.

                                Utils.debugLog(Settings.DEBUG, "Found [message] within a command."); // The debug, saying that it found "[message]" in a command.
                                Utils.tell(opener, command.replace("[message]", "")
                                        .replace("{prefix}", Messages.PREFIX)
                                        .replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{sender}", opener.getName())
                                        .replace("{reason}", PunishInventory.getReasonMap().get(opener.getUniqueId()))); // Replace the placeholders in the message and send it to the player.

                            } else if (command.contains("[console]")) { // if the message contains "[console]" anywhere in the command, it will execute what ever is set as the console.

                                Utils.debugLog(Settings.DEBUG, "Found [console] within a command, executing '" + command + "' as console."); // The debug message that is sent when it finds "[console]" in a command.

                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("[console]", "")
                                        .replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{sender}", opener.getName()).replace("{reason}", PunishInventory.getReasonMap().get(opener.getUniqueId()))); // Replace placeholders and ruins the command.

                            } else if (invConfig.getString(key + ".type").equalsIgnoreCase("CONSOLE")) { // If the type of the item is CONSOLE, run all commands as console.

                                Utils.debugLog(Settings.DEBUG, "Found 'CONSOLE' as type, executing the command as console."); // The message that is sent when we find "CONSOLE" as the command type.
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{sender}", opener.getName())
                                        .replace("{reason}", PunishInventory.getReasonMap().get(opener.getUniqueId()))); // Replace placeholders, and run the commands as console.

                            } else {

                                Utils.debugLog(Settings.DEBUG, "Executing command '" + command + "' for '" + opener.getName() + "'.");
                                // Nothing is found for the command, the type is COMMAND, so we execute the command as the player that clicked in the item.
                                opener.performCommand(command
                                        .replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())
                                        .replace("{sender}", opener.getName())
                                        .replace("{reason}", PunishInventory.getReasonMap().get(opener.getUniqueId()))); // Replace the placeholders and run the command.

                            }
                        }
                        Utils.tell(opener, Messages.PUNISHMENT_SUCCESSFUL.replace("{target}", PunishInventory.getTargetMap().get(opener.getUniqueId()).getName())); // Sends the message to the player if everything is successful.
                    }
                }
            }
    }


}
