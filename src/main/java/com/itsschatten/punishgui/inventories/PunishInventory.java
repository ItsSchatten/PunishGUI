package com.itsschatten.punishgui.inventories;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.inventories.InventoryUtils;
import com.itsschatten.punishgui.PunishGUI;
import com.itsschatten.punishgui.configs.Messages;
import com.itsschatten.punishgui.configs.Settings;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * The class that creates and opens the inventory for the player that executes the /punish command.
 */
public class PunishInventory {

    @Getter
    private static Map<UUID, Player> targetMap; // We set the target map with the senders UUID so we can always get the correct target.

    @Getter
    private static Map<UUID, String> reasonMap; // We set the reason map with the senders UUID so we can always get the correct reason.

    @Getter
    private static Map<UUID, YamlConfiguration> configMap;

    public PunishInventory() { // Defines the maps as HashMaps.
    }

    public static void loadMaps() {
        reasonMap = new HashMap<>();
        targetMap = new HashMap<>();
        configMap = new HashMap<>();
    }

    public static void clearMaps() {
        configMap.clear();
        reasonMap.clear();
        targetMap.clear();

        configMap = null;
        reasonMap = null;
        targetMap = null;
    }

    // This method will load the inventory for the player and will do some conditioning checks to make sure everything is in order.
    public void loadInv(Player opener, @NonNull Player methodTarget, String reason) {

        List<YamlConfiguration> capableConfigs = new ArrayList<>();

        PunishGUI.getInstance().getInventoryFiles().forEach((fileName, config) -> {

            if (opener.hasPermission(Objects.requireNonNull(config.getString("permission")))) {
                capableConfigs.add(config);
            }

        });

        int invPriority = 0;

        YamlConfiguration invConfig = null;

        if (capableConfigs.size() == 1) {
            invConfig = capableConfigs.get(0);
            Utils.debugLog(Settings.DEBUG, "Only one configuration file found, using it...");
        } else {
            for (YamlConfiguration config : capableConfigs) {
                if (invPriority < config.getInt("priority")) {
                    invPriority = config.getInt("priority");
                    invConfig = config;

                    Utils.debugLog(Settings.DEBUG, "Higher priority config found, \"" + config.getName() + "\" setting as new config.");
                }
            }
        }

        getConfigMap().put(opener.getUniqueId(), invConfig);


        try {
            // Checks if the inventory is of appropriate size.
            if ((invConfig.getInt("size") > 54) && !(invConfig.getInt("size") < 9) || invConfig.getInt("size") % 9 != 0) {
                Utils.debugLog(Settings.DEBUG, "Field 'size', in the file '" + invConfig.getName() + "' contains an inappropriate entry.");
                throw new InvalidTypeException("'size' in the file '" + invConfig.getName() + "' contains an inappropriate value. Found " + invConfig.getInt("size") + ": Needs a value from 9 to 54 in multiples of 9.", invConfig.getName().toUpperCase() + ": size");
            }
        } catch (InvalidTypeException ex) {
            Utils.log(ex.getCause().toString()); // Get the cause of the error.
            Utils.log(ex.invalidItem + "\n"); // We get the item.
            Utils.tell(opener, Messages.AN_ERROR_OCCURRED.replace("{error}", ex.getCause().toString())); // We send a message to the player that an error occurred.
            return;
        }

        Inventory inv = Bukkit.createInventory(opener, invConfig.getInt("size"), Utils.colorize(invConfig.getString("name").replace("{target}", methodTarget.getName())));
        // Creates the inventory, gets the size from the settings.yml, and the name from that file as well.

        if (Settings.FILL_SPARE_INV_SPACES) {
            final ItemStack fillItem = new ItemStack(Material.valueOf(Settings.FILL_ITEM), 1);
            final ItemMeta fillItemMeta = fillItem.getItemMeta();

            fillItemMeta.setDisplayName(Utils.colorize("&f"));
            fillItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            fillItem.setItemMeta(fillItemMeta);

            for (int i = 0; i < inv.getSize(); i++)
                inv.setItem(i, fillItem);
        }

        for (String key : invConfig.getKeys(false)) { // We get all the keys that are defined in the file, we assume that all keys in this file are items.

            if (invConfig.getConfigurationSection(key) == null) {
                continue;
            }

            try {

                // Checks if type is set. If not set throws a
                if (!invConfig.getConfigurationSection(key).getKeys(false).contains("type")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'type' is undefined. Sending an error message to sender and console.");
                    throw new NotSetException(key + ".type is undefined. Please define one of the following: COMMAND, CONSOLE, or NONE.", key + ".type");
                }

                // If type is set, checks to make sure that the value is supported. If not throws an error, and skips the item.
                if (!invConfig.get(key + ".type").equals("COMMAND") && !invConfig.get(key + ".type").equals("CONSOLE") && !invConfig.get(key + ".type").equals("NONE")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'type' contains an inappropriate value. Sending an error message to sender and console.");
                    throw new InvalidTypeException(key + ".type Field 'type' contains an inappropriate value. Requires: COMMAND, CONSOLE, or NONE. Found:" + invConfig.getString(key + ".type"), key + ".type");
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

            } catch (final NotSetException ex) { // We catch the NotSetException that is thrown and log that to the console so the user knows why an item isn't appearing.
                Utils.log(ex.getCause().toString()); // We get the cause of the error.
                Utils.log(ex.itemNotSet + "\n"); // We get what item(s) aren't set.
                Utils.tell(opener, Messages.AN_ERROR_OCCURRED.replace("{error}", ex.getCause().toString()));// We tell the player that an error occurred.
                continue;
            } catch (final InvalidTypeException ex) { // If a value is not valid we send an error the console as well.
                Utils.log(ex.getCause().toString()); // Get the cause of the error.
                Utils.log(ex.invalidItem + "\n"); // We get the item.
                Utils.tell(opener, Messages.AN_ERROR_OCCURRED.replace("{error}", ex.getCause().toString())); // We send a message to the player that an error occurred.
                continue;
            }

            // Sets values that are required for for the item to be set.
            final int where = invConfig.getInt(key + ".where"), amount = !invConfig.getConfigurationSection(key).contains("amount") ? 1 : invConfig.getInt(key + ".amount"); //  We set the where and the amount of items.
            final Material mat = Material.getMaterial((invConfig.getString(key + ".item").toUpperCase())); // We set the material for the item.
            final boolean glow = invConfig.getConfigurationSection(key).contains("glow") ? invConfig.getBoolean(key + ".glow") : false; // Get if the item should glow or not.
            final String name = !invConfig.getConfigurationSection(key).contains("name") ? "&r" + mat.name() : invConfig.getString(key + ".name"); // Get the name of the item, if not set we set it as material.
            List<String> lore = !invConfig.getConfigurationSection(key).contains("lore") ? new ArrayList<>() : invConfig.getStringList(key + ".lore"); // We get the lore of the item.
            List<String> coloredLore = new ArrayList<>(); // We define a list for lore lines that are colored.

            lore.forEach((line) -> {
                String lineColored = Utils.colorize(line); // We colorize the line for the lore, and then add it to the List.
                coloredLore.add(lineColored);
            });

            // Checks if an item contains the permission, and that PERMISSION_ITEMS are enabled. If the setting is true, we only adds the item
            // if the opener has permission to see it.
            if (invConfig.getConfigurationSection(key).contains("permission")) {
                if (!Settings.PERMISSION_ITEMS) {

                    Utils.debugLog(Settings.DEBUG, "Detected that an item contains a permission section, but using permission items" +
                            " is not set in the config, this item will be shown to every opener of the inventory."); // We send a debug message if the permission-items is false in the settings.yml.
                    InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore); // We create the item and set it in the inv.

                } else if (opener.hasPermission(invConfig.getString(key + ".permission"))) { // The permissions-items is true, so we check if the opener has permission to see it.
                    InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore); // If so we set the item in the inv.
                }
            } else {
                InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore); // If it doesn't contain a permission section we just set the item in the inventory.
            }
        }

        if (Settings.USE_SOUNDS) { // If use sounds is enabled in the config, we will the defined sound, and if the use random sound pitch is true, we play it at a random pitch.
            opener.playSound(opener.getLocation(), Sound.valueOf(Settings.PUNISH_OPEN_SOUND), (float) 10, Settings.USE_RANDOM_SOUND_PITCH ? (float) Math.random() : Settings.SOUND_PITCH);
        }

        targetMap.put(opener.getUniqueId(), methodTarget); // We put the target in the target so we can get it later.

        if (reason == null) {
            Utils.debugLog(Settings.DEBUG, "Reason was null, setting the map to the default message."); // We send a debug message, if the reason was null, and notifying that we are setting it to default.
            reasonMap.put(opener.getUniqueId(), Settings.DEFAULT_REASON); // If the reason is null we get the default reason from the settings.yml and add it to the reason map.
        } else {
            Utils.debugLog(Settings.DEBUG, "Reason was found! Setting the map to the reason."); // If we find a reason, we notify that we found one and say we are passing it to the reason map.
            reasonMap.put(opener.getUniqueId(), reason); // If it isn't null, we set the reason that was passed to the method, to the reason map.
        }

        opener.openInventory(inv); // Finally after everything is done, we open the inventory for the player.
    }


    public void loadInvTest(Player opener, @NonNull Player methodTarget, String reason, String configName) {

        YamlConfiguration invConfig = PunishGUI.getInstance().getInventoryFiles().get(configName);

        Utils.log(PunishGUI.getInstance().getInventoryFiles().get(configName).getName());

        getConfigMap().put(opener.getUniqueId(), invConfig);

        Inventory inv = Bukkit.createInventory(opener, invConfig.getInt("size"), Utils.colorize("&cTest Inventory&r " + invConfig.getString("name").replace("{target}", methodTarget.getName())));
        // Creates the inventory, gets the size from the settings.yml, and the name from that file as well.

        if (Settings.FILL_SPARE_INV_SPACES) {
            final ItemStack fillItem = new ItemStack(Material.valueOf(Settings.FILL_ITEM), 1);
            final ItemMeta fillItemMeta = fillItem.getItemMeta();

            fillItemMeta.setDisplayName(Utils.colorize("&f"));
            fillItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            fillItem.setItemMeta(fillItemMeta);

            for (int i = 0; i < inv.getSize(); i++)
                inv.setItem(i, fillItem);
        }

        for (String key : invConfig.getKeys(false)) { // We get all the keys that are defined in the file, we assume that all keys in this file are items.

            if (invConfig.getConfigurationSection(key) == null) {
                continue;
            }

            try {
                // Checks if the inventory is of appropriate size.
                if ((invConfig.getInt("size") > 54) && !(invConfig.getInt("size") < 9) || invConfig.getInt("size") % 9 != 0) {
                    Utils.debugLog(Settings.DEBUG, "Field 'size', in the file '" + invConfig.getName() + "' contains an inappropriate entry.");
                    throw new InvalidTypeException("'size' in the file '" + invConfig.getName() + "' contains an inappropriate value. Found " + invConfig.getInt("size") + ": Needs a value from 9 to 54 in multiples of 9.", invConfig.getName().toUpperCase() + ": size");
                }

                // Checks if type is set. If not set throws a
                if (!invConfig.getConfigurationSection(key).getKeys(false).contains("type")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'type' is undefined. Sending an error message to sender and console.");
                    throw new NotSetException(key + ".type is undefined. Please define one of the following: COMMAND, CONSOLE, or NONE.", key + ".type");
                }

                // If type is set, checks to make sure that the value is supported. If not throws an error, and skips the item.
                if (!invConfig.get(key + ".type").equals("COMMAND") && !invConfig.get(key + ".type").equals("CONSOLE") && !invConfig.get(key + ".type").equals("NONE")) {
                    Utils.debugLog(Settings.DEBUG, "Field 'type' contains an inappropriate value. Sending an error message to sender and console.");
                    throw new InvalidTypeException(key + ".type Field 'type' contains an inappropriate value. Requires: COMMAND, CONSOLE, or NONE. Found:" + invConfig.getString(key + ".type"), key + ".type");
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

            } catch (final NotSetException ex) { // We catch the NotSetException that is thrown and log that to the console so the user knows why an item isn't appearing.
                Utils.log(ex.getCause().toString()); // We get the cause of the error.
                Utils.log(ex.itemNotSet + "\n"); // We get what item(s) aren't set.
                Utils.tell(opener, Messages.AN_ERROR_OCCURRED.replace("{error}", ex.getCause().toString()));// We tell the player that an error occurred.
                continue;
            } catch (final InvalidTypeException ex) { // If a value is not valid we send an error the console as well.
                Utils.log(ex.getCause().toString()); // Get the cause of the error.
                Utils.log(ex.invalidItem + "\n"); // We get the item.
                Utils.tell(opener, Messages.AN_ERROR_OCCURRED.replace("{error}", ex.getCause().toString())); // We send a message to the player that an error occurred.
                continue;
            }

            // Sets values that are required for for the item to be set.
            final int where = invConfig.getInt(key + ".where"), amount = !invConfig.getConfigurationSection(key).contains("amount") ? 1 : invConfig.getInt(key + ".amount"); //  We set the where and the amount of items.
            final Material mat = Material.getMaterial((invConfig.getString(key + ".item").toUpperCase())); // We set the material for the item.
            final boolean glow = invConfig.getConfigurationSection(key).contains("glow") ? invConfig.getBoolean(key + ".glow") : false; // Get if the item should glow or not.
            final String name = !invConfig.getConfigurationSection(key).contains("name") ? "&r" + mat.name() : invConfig.getString(key + ".name"); // Get the name of the item, if not set we set it as material.
            List<String> lore = !invConfig.getConfigurationSection(key).contains("lore") ? new ArrayList<>() : invConfig.getStringList(key + ".lore"); // We get the lore of the item.
            List<String> coloredLore = new ArrayList<>(); // We define a list for lore lines that are colored.

            lore.forEach((line) -> {
                String lineColored = Utils.colorize(line); // We colorize the line for the lore, and then add it to the List.
                coloredLore.add(lineColored);
            });

            // Checks if an item contains the permission, and that PERMISSION_ITEMS are enabled. If the setting is true, we only adds the item
            // if the opener has permission to see it.
            if (invConfig.getConfigurationSection(key).contains("permission")) {
                if (!Settings.PERMISSION_ITEMS) {

                    Utils.debugLog(Settings.DEBUG, "Detected that an item contains a permission section, but using permission items" +
                            " is not set in the config, this item will be shown to every opener of the inventory."); // We send a debug message if the permission-items is false in the settings.yml.
                    InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore); // We create the item and set it in the inv.

                } else if (opener.hasPermission(invConfig.getString(key + ".permission"))) { // The permissions-items is true, so we check if the opener has permission to see it.
                    InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore); // If so we set the item in the inv.
                }
            } else {
                InventoryUtils.createItem(inv, where, mat, amount, glow, name, coloredLore); // If it doesn't contain a permission section we just set the item in the inventory.
            }
        }

        if (Settings.USE_SOUNDS) { // If use sounds is enabled in the config, we will the defined sound, and if the use random sound pitch is true, we play it at a random pitch.
            opener.playSound(opener.getLocation(), Sound.valueOf(Settings.PUNISH_OPEN_SOUND), (float) 10, Settings.USE_RANDOM_SOUND_PITCH ? (float) Math.random() : Settings.SOUND_PITCH);
        }

        targetMap.put(opener.getUniqueId(), methodTarget); // We put the target in the target so we can get it later.

        if (reason == null) {
            Utils.debugLog(Settings.DEBUG, "Reason was null, setting the map to the default message."); // We send a debug message, if the reason was null, and notifying that we are setting it to default.
            reasonMap.put(opener.getUniqueId(), Settings.DEFAULT_REASON); // If the reason is null we get the default reason from the settings.yml and add it to the reason map.
        } else {
            Utils.debugLog(Settings.DEBUG, "Reason was found! Setting the map to the reason."); // If we find a reason, we notify that we found one and say we are passing it to the reason map.
            reasonMap.put(opener.getUniqueId(), reason); // If it isn't null, we set the reason that was passed to the method, to the reason map.
        }

        opener.openInventory(inv); // Finally after everything is done, we open the inventory for the player.
    }


    @RequiredArgsConstructor
    private static final class NotSetException extends NullPointerException { // The NotSetException, it inherits the NullPointerException.
        private static final long serialVersionUID = 1L; // Set the unique id for the exception.
        private final String itemNotSet; // The item that is causing the error.
        private final String fieldName; // The fieldName where the error is located.

        @Override
        public synchronized Throwable getCause() {
            return new NullPointerException("'" + fieldName + "' is undefined."); // The cause that we send to the console and player.
        }
    }


    @RequiredArgsConstructor
    private static final class InvalidTypeException extends Exception {
        private static final long serialVersionUID = 1L; // Set the unique id for the exception.
        private final String invalidItem; // The item that is invalid.
        private final String fieldName;  // The field in which the invalidItem is invalid.

        @Override
        public synchronized Throwable getCause() {
            return new Exception("'" + fieldName + "' contains an invalid type."); // The cause that we send to the console and player.
        }
    }

    @RequiredArgsConstructor
    public static final class InvalidValueException extends Exception {
        private static final long serialVersionUID = 1L;
        private final String invalidConfig, invalidPath, fieldValue, whatItShouldBe;

        @Override
        public synchronized Throwable getCause() {
            return new Exception("\"" + invalidConfig + "\" contains an invalid value at \"" + invalidPath + "\". Found \"" + fieldValue + "\" but should be \"" + whatItShouldBe + "\".");
        }
    }

    @RequiredArgsConstructor
    public static final class InvalidConfigurationFile extends Exception {
        private static final long serialVersionUID = 1L;
        private final String config, lackedKey;

        @Override
        public synchronized Throwable getCause() {
            return new InvalidConfigurationException("'" + config + "' lacks required key " + "'" + lackedKey + "'");
        }
    }

}
