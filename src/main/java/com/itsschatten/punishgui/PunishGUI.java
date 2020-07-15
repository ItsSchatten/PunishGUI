package com.itsschatten.punishgui;

import com.itsschatten.libs.UpdateNotifications;
import com.itsschatten.libs.Utils;
import com.itsschatten.punishgui.commands.PunishCommand;
import com.itsschatten.punishgui.commands.PunishGUICommand;
import com.itsschatten.punishgui.configs.Messages;
import com.itsschatten.punishgui.configs.Settings;
import com.itsschatten.punishgui.events.InventoryListener;
import com.itsschatten.punishgui.events.PlayerJoinListener;
import com.itsschatten.punishgui.inventories.PunishInventory;
import com.itsschatten.punishgui.tasks.CheckForUpdateTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PunishGUI extends JavaPlugin {

    // The getter and setter for the instance of this class.
    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    // The instance.
    private static PunishGUI instance;

    @Getter
    private Map<String, YamlConfiguration> inventoryFiles;

    // The onEnable method.
    @Override
    public void onEnable() {
        // Sets the instance for the Utils class and the plugin instance.
        Utils.setInstance(this);
        setInstance(this);

        inventoryFiles = new HashMap<>();

        // Sets the header text when loading the plugin.
        PluginDescriptionFile pdf = this.getDescription();
        String authors = String.join(" ,", pdf.getAuthors());
        Utils.log("",
                "&c+-----------------------------------------------------+",
                "&b _____             _     _         &6 _____ _    _ _____",
                "&b|  __ \\           (_)   | |        &6/ ____| |  | |_   _|",
                "&b| |__) |   _ _ __  _ ___| |__     &6| |  __| |  | | | |",
                "&b|  ___/ | | | '_ \\| / __| '_ \\    &6| | |_ | |  | | | |",
                "&b| |   | |_| | | | | \\__ \\ | | |   &6| |__| | |__| |_| |_",
                "&b|_|    \\__,_|_| |_|_|___/_| |_|    &6\\_____|\\____/|_____|",
                "",
                "&cVersion &7» " + pdf.getVersion(),
                "&cDeveloped By &7» " + authors,
                "");

        // Initializes all configuration files.
        loadInventoryConfigs();
        Settings.init();
        Messages.init();
        Utils.debugLog(Settings.DEBUG,
                "&7Config files have been initialized.");

        // Registers commands.
        Utils.registerCommand(new PunishCommand());
        Utils.registerCommand(new PunishGUICommand());
        Utils.debugLog(Settings.DEBUG,
                "&7Commands have been initialized.");

        // Registers Events.
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);
        Utils.debugLog(Settings.DEBUG,
                "&7Events have been initialized.");

        if (Settings.USE_METRICS) {
            Utils.log("&7Metrics are enabled! You can see the information collect at the following link: &chttps://bstats.org/plugin/bukkit/PunishGUI&7", "If you don't wish for this information to be collected you can disable it in the settings.yml.");
            new MetricsLite(this, 5726);
        }

        // Starts the updater, and starts the update task.
        if (Settings.USE_UPDATER) {
            new UpdateNotifications(62679) {
                @Override
                public void onUpdateAvailable() {
                    if (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_16_R1")) {
                        Utils.log(UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                                .replace("{newVer}", UpdateNotifications.getLatestVersion())
                                .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));
                    } else {
                        Utils.debugLog(Settings.DEBUG, "There is an update to the plugin available but the version is not the latest supported version. To ensure that we don't spam the user's console we won't send a message.");
                        Utils.log("&4&l[WARNING]&c Hey! Just wanted to let you know that you are using an older version of the plugin on an unsupported version of Minecraft. If you don't wish to see this message you can disable update checking in the settings.yml.");
                    }
                }
            }.runTaskAsynchronously(this);

            if (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_16_R1")) {
                new CheckForUpdateTask().runTaskTimerAsynchronously(this, 30 * 60 * 20, 30 * 60 * 20); // Wait 30 minutes and check for another update.
                Utils.debugLog(Settings.DEBUG, "Checked for update, and set timer running.");
            }
        }

        Utils.log("&c+-----------------------------------------------------+");
    }

    @Override
    public void onDisable() {
        // Sets the instance of the plugin, and the one used in the utils to null.
        Utils.setInstance(null);
        setInstance(null);

        PunishInventory.clearMaps();
    }

    public void loadInventoryConfigs() {

        if (getInventoryFiles().size() != 0) {
            inventoryFiles.clear();
        }

        File inventoryFolder = new File(getDataFolder(), "/inventories");


        if (!inventoryFolder.exists()) {
            inventoryFolder.mkdirs();
            File defaultInventoryFile = new File(getDataFolder() + "/inventories/inventory.yml"), defaultDescriptionFile = new File(getDataFolder() + "/inventories/read-me.txt");

            try {
                defaultInventoryFile.createNewFile();
                defaultDescriptionFile.createNewFile();
            } catch (IOException e) {
                Utils.log("&c= [ ----------------------------------------------------- ] =");
                e.printStackTrace();
                Utils.log("&c= [ ----------------------------------------------------- ] =");

            }

            try (InputStream is = this.getResource("inventories/inventory.yml")) {
                Objects.requireNonNull(is, "Inbuilt file not found: inventory.yml");

                // Now copy the content of the default file to there
                Files.copy(is, Paths.get(defaultInventoryFile.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                Utils.log("&c= [ ----------------------------------------------------- ] =");
                e.printStackTrace();
                Utils.log("&c= [ ----------------------------------------------------- ] =");
            }

            try (InputStream is = this.getResource("inventories/read-me.txt")) {
                Objects.requireNonNull(is, "Inbuilt file not found: read-me.txt");

                // Now copy the content of the default file to there
                Files.copy(is, Paths.get(defaultDescriptionFile.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                Utils.log("&c= [ ----------------------------------------------------- ] =");
                e.printStackTrace();
                Utils.log("&c= [ ----------------------------------------------------- ] =");
            }
        }

        File[] invFiles = inventoryFolder.listFiles((file, fileName) -> fileName.endsWith(".yml"));

        for (File invFile : invFiles) {
            YamlConfiguration invConfig = YamlConfiguration.loadConfiguration(invFile);

            inventoryFiles.put(invFile.getName(), invConfig);

            try {
                if (!invConfig.getKeys(false).contains("permission")) {
                    inventoryFiles.remove(invFile.getName());
                    throw new PunishInventory.InvalidConfigurationFile(invFile.getName(), "permission");
                }

                if (!invConfig.getKeys(false).contains("priority")) {
                    inventoryFiles.remove(invFile.getName());
                    throw new PunishInventory.InvalidConfigurationFile(invFile.getName(), "priority");
                }

                if (invConfig.getInt("priority") == 0) {
                    inventoryFiles.remove(invFile.getName());
                    throw new PunishInventory.InvalidValueException(invFile.getName(), "priority", invConfig.getInt("priority") + "", "value greater than 0");
                }

                if (!invConfig.getKeys(false).contains("name")) {
                    inventoryFiles.remove(invFile.getName());
                    throw new PunishInventory.InvalidConfigurationFile(invFile.getName(), "name");
                }

                if (!invConfig.getKeys(false).contains("size")) {
                    inventoryFiles.remove(invFile.getName());
                    throw new PunishInventory.InvalidConfigurationFile(invFile.getName(), "size");
                }

                if ((invConfig.getInt("size") > 54) && !(invConfig.getInt("size") < 9) || invConfig.getInt("size") % 9 != 0) {
                    Utils.debugLog(Settings.DEBUG, "Field 'size', in the file '" + invConfig.getName() + "' contains an inappropriate entry.");
                    throw new PunishInventory.InvalidValueException(invFile.getName(), "size", invConfig.getInt("size") + "", "must be a whole number from 9-54 and must be a multiple of 9");
                }

            } catch (PunishInventory.InvalidConfigurationFile invalid) {
                Utils.log("&c= [ ----------------------------------------------------- ] =", "&cAn error occurred.", invalid.getCause().toString(), "&c= [ ----------------------------------------------------- ] =");

            } catch (PunishInventory.InvalidValueException ex) {
                Utils.log("&c= [ ----------------------------------------------------- ] =", "&cAn error occurred.", ex.getCause().toString(), "&c= [ ----------------------------------------------------- ] =");
            }
        }

        if (inventoryFiles.size() == 0) {
            Utils.log("&cNo inventory files could be loaded, was there an error?");
            return;
        }

        Utils.log("&7Loaded &b" + inventoryFiles.size() + "&7 inventory files.");
    }

}
