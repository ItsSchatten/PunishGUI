package com.itsschatten.punishgui;

import com.itsschatten.libs.UpdateNotifications;
import com.itsschatten.libs.Utils;
import com.itsschatten.punishgui.commands.PunishCommand;
import com.itsschatten.punishgui.commands.PunishGUICommand;
import com.itsschatten.punishgui.configs.InventoryConfig;
import com.itsschatten.punishgui.configs.Messages;
import com.itsschatten.punishgui.configs.Settings;
import com.itsschatten.punishgui.events.InventoryClickListener;
import com.itsschatten.punishgui.events.PlayerJoinListener;
import com.itsschatten.punishgui.tasks.CheckForUpdateTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishGUI extends JavaPlugin {

    // The getter and setter for the instance of this class.
    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    // The instance.
    private static PunishGUI instance;

    // The onEnable method.
    @Override
    public void onEnable() {
        // Sets the instance for the Utils class and the plugin instance.
        Utils.setInstance(this);
        setInstance(this);

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
        InventoryConfig.init();
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
        pm.registerEvents(new InventoryClickListener(), this);
        pm.registerEvents(new PlayerJoinListener(), this);
        Utils.debugLog(Settings.DEBUG,
                "&7Events have been initialized.");

        if (Settings.USE_METRICS) {
            Utils.log("&7Metrics are enabled! You can see the information collect at the following link: &chttps://bstats.org/plugin/bukkit/PunishGUI&7", "If you don't wish for this information to be collected you can disable it in the settings.yml.");
            Metrics mets = new Metrics(this);
        }

        // Starts the updater, and starts the update task.
        if (Settings.USE_UPDATER) {
            new UpdateNotifications(62679) {
                @Override
                public void onUpdateAvailable() {
                    if (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_15_R1")) {
                        Utils.log(UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                                .replace("{newVer}", UpdateNotifications.getLatestVersion())
                                .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));
                    } else {
                        Utils.debugLog(Settings.DEBUG, "There is an update to the plugin available but the version is not the latest supported version. To ensure that we don't spam the user's console we won't send a message.");
                        Utils.log("&4&l[WARNING]&c Hey! Just wanted to let you know that you are using an older version of the plugin on an unsupported version of Minecraft. If you don't wish to see this message you can disable update checking in the settings.yml.");
                    }
                }
            }.runTaskAsynchronously(this);

            if (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_15_R1")) {
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
    }
}
