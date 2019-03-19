package com.itsschatten.punishgui.inventories;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.inventories.InventoryUtils;
import com.itsschatten.punishgui.configs.InventoryConfig;
import com.itsschatten.punishgui.configs.Settings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class PunishInventory {

    @Getter
    public static Map<UUID, Player> targetMap;

    @Getter
    public static Map<UUID, String> reasonMap;

    public PunishInventory() {
        reasonMap = new HashMap<>();
        targetMap = new HashMap<>();
    }

    public void loadInv(Player opener, Player methodTarget, String reason) {
        InventoryConfig invConfig = InventoryConfig.getInstance();
        Inventory inv = Bukkit.createInventory(opener, Settings.INV_SIZE, Utils.colorize(Settings.PUNISH_INV_NAME.replace("{target}", methodTarget.getName())));

        for (String key : invConfig.getKeys(false)) {
            try {
                // Checks if the inventory is of appropriate size.
                if ((Settings.getInstance().getInt("inv-size") > 54) && !(Settings.getInstance().getInt("inv-size") < 9)) {
                    Utils.debugLog(Settings.DEBUG, "Field 'inv-size', in the file settings.yml contains an inappropriate entry.");
                    throw new InvalidValueException("'inv-size' in the file 'settings.yml' contains an inappropriate value. Found " + Settings.getInstance().getInt("inv-size") + ": Needs a value from 9 to 54 in multiples of 9.", "SETTINGS.YML: inv-size");
                }

                // Checks if type is set. If not set throws a
                if (!invConfig.getConfigurationSection(key).getKeys(false).contains("type")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'type' is undefined. Sending an error message to sender and console.");
                    throw new NotSetException(key + ".type is undefined. Please define one of the following: COMMAND, CONSOLE, or NONE.", key + ".type");
                }

                // If type is set, checks to make sure that the value is supported. If not throws an error, and skips the item.
                if (!invConfig.get(key + ".type").equals("COMMAND") && !invConfig.get(key + ".type").equals("CONSOLE") && !invConfig.get(key + ".type").equals("NONE")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'type' contains an inappropriate value. Sending an error message to sender and console.");
                    throw new InvalidValueException(key + ".type Field 'type' contains an inappropriate value. Requires: COMMAND, CONSOLE, or NONE. Found:" + invConfig.getString(key + ".type"), key + ".type");
                }

                // Checks if the type is a command type, if it is checks if a string array exists, if it doesn't throws and error.
                if (invConfig.getString(key + ".type").equalsIgnoreCase("command") || invConfig.getString(key + ".type").equalsIgnoreCase("console")) {
                    if (WordUtils.capitalizeFully(invConfig.getString(key + ".type")).equalsIgnoreCase("command")) {
                        if (!invConfig.getConfigurationSection(key).getKeys(false).contains("commands")) {
                            Utils.debugLog(Settings.DEBUG, "Found type 'command' or 'console', could not find 'commands' entry.");
                            throw new NotSetException(key + ".commands is unset. Please set a list of commands to execute.", key + ".commands");
                        }
                    }
                }

                // Checks if the item is set, if not throws an error.
                if (!invConfig.getConfigurationSection(key).getKeys(false).contains("item")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'item' is undefined. Sending an error message to sender and console.");
                    throw new NotSetException(key + ".item is undefined. Please provide an appropriate Material name.", key + ".item");
                }

                // Checks if the where is set, if not throws an error.
                if (!invConfig.getConfigurationSection(key).getKeys(false).contains("where")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'where' is undefined. Sending an error message to sender and console.");
                    throw new NotSetException(key + ".where is undefined. Please define a number, from 1 to your max inventory size.", key + ".where");
                }

                // Checks if close-on-click is set, if not throws an error.
                if (!invConfig.getConfigurationSection(key).getKeys(false).contains("close-on-click")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'close-on-click' is undefined. Sending an error message to sender and console.");
                    throw new NotSetException(key + ".close-on-click is undefined. Please define either true or false.", key + ".close-on-click");
                }

            } catch (final NotSetException ex) {
                Utils.log(ex.getCause().toString());
                Utils.log(ex.itemNotSet + "\n");
                Utils.tell(opener, "&eAn error occurred. \n", "&cPlease check console for more details.");
                continue;
            } catch (final InvalidValueException ex) {
                Utils.log(ex.getCause().toString());
                Utils.log(ex.invalidItem + "\n");
                Utils.tell(opener, "&eAn error occurred. \n", "&cPlease check console for more details.");

                if (ex.invalidItem.equalsIgnoreCase("SETTINGS.YML: inv-size")) {
                    return;
                }
            }

            // Sets values that are required for for the item to be set.
            final int where = invConfig.getInt(key + ".where"), amount = !invConfig.getConfigurationSection(key).contains("amount") ? 1 : invConfig.getInt(key + ".amount");
            final Material mat = Material.getMaterial(invConfig.getString(key + ".item"));
            final boolean glow = invConfig.getConfigurationSection(key).contains("glow") ? invConfig.getBoolean(key + ".glow") : false;
            final String name = !invConfig.getConfigurationSection(key).contains("name") ? mat.name() : invConfig.getString(key + ".name");
            List<String> lore = !invConfig.getConfigurationSection(key).contains("lore") ? new ArrayList<>() : invConfig.getStringList(key + ".lore");
            List<String> coloredLore = new ArrayList<>();
            coloredLore.clear();

            lore.forEach((line) -> {
                String lineColored = Utils.colorize(line);
                coloredLore.add(lineColored);
            });

            // Checks if an item contains the permission, and that PERMISSION_ITEMS are enabled. If they are only adds the item
            // if the opener has permission to see it. Otherwise if both are false
            if (invConfig.getConfigurationSection(key).contains("permission")) {
                if (!Settings.PERMISSION_ITEMS) {

                    Utils.log("Detected that an item contains a permission section, but using permission items is not set in the config, this item will be shown to every opener of the inventory.");
                    InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore);

                } else if (opener.hasPermission(invConfig.getString(key + ".permission"))) {
                    InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore);
                }
            } else {
                InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore);
            }
        }

        if (Settings.USE_SOUNDS) {
            opener.playSound(opener.getLocation(), Sound.valueOf(Settings.PUNISH_OPEN_SOUND), (float) 100, Settings.USE_RANDOM_SOUND_PITCH ? (float) Math.random() : Settings.SOUND_PITCH);
        }

        targetMap.put(opener.getUniqueId(), methodTarget);

        if (reason == null) {
            Utils.debugLog(Settings.DEBUG, "Reason was null, setting the map to the default message.");
            reasonMap.put(opener.getUniqueId(), Settings.DEFAULT_REASON);
        } else {
            Utils.debugLog(Settings.DEBUG, "Reason was found! Setting the map to the reason.");
            reasonMap.put(opener.getUniqueId(), reason);
        }

        opener.openInventory(inv);
    }


    @RequiredArgsConstructor
    private final class NotSetException extends NullPointerException {
        private static final long serialVersionUID = 1L;
        private final String itemNotSet;
        private final String fieldName;

        @Override
        public synchronized Throwable getCause() {
            return new NullPointerException("'" + fieldName + "' is undefined.");
        }
    }


    @RequiredArgsConstructor
    private final class InvalidValueException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final String invalidItem;
        private final String fieldName;

        @Override
        public synchronized Throwable getCause() {
            return new RuntimeException("'" + fieldName + "' contains an invalid type.");
        }
    }

}
