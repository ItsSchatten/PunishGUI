package me.itsshadow.punishgui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.UpdateNotifications;
import me.itsshadow.libs.Utils;
import me.itsshadow.punishgui.commands.PunishCommand;
import me.itsshadow.punishgui.commands.PunishGUICommand;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Messages;
import me.itsshadow.punishgui.configs.Settings;
import me.itsshadow.punishgui.events.PlayerJoinListener;
import me.itsshadow.punishgui.events.PlayerLeaveListener;
import me.itsshadow.punishgui.inventories.PunishInv;
import me.itsshadow.punishgui.tasks.CheckForUpdateTask;
import me.itsshadow.punishgui.tasks.CheckInConvoTask;
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
        Utils.setInstance(this);
        setInstance(this);

        register();
    }

    // The onDisable method.
    @Override
    public void onDisable() {
        Utils.setInstance(null);
        setInstance(null);
    }

    private void register() {

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

        Utils.registerCommand(new PunishCommand());
        Utils.registerCommand(new PunishGUICommand());
        Utils.log("",
                "&7Commands have been initialized.");

        PunishInv.setInstance(new PunishInv());

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(PunishInv.getInstance(), this);
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new PlayerLeaveListener(), this);
        Utils.log("",
                "&7Events have been initialized.");

        InventoryConfig.init();
        Settings.init();
        Messages.init();
        Utils.log("",
                "&7Config files have been initialized.");

        Utils.setPrefix(Messages.PREFIX);
        Utils.setNoPermsMessage(Messages.NO_PERMS);
        Utils.setUpdateAvailableMessage(Messages.UPDATE_AVAILABLE);

        if (Settings.USE_CONVOS && Settings.USE_CONVO_MAP) {
            new CheckInConvoTask().runTaskTimerAsynchronously(this, 20, 60 * 20);
        }

        if (Settings.USE_UPDATER) {
            new UpdateNotifications(62679) {
                @Override
                public void onUpdateAvailable() {
                    Utils.log(UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                            .replace("{newVer}", UpdateNotifications.getLatestVersion())
                            .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));
                }
            }.runTaskAsynchronously(this);

            new CheckForUpdateTask().runTaskTimerAsynchronously(this, 20, 30 * 60 * 20);
        }

        Utils.log("",
                "&c+-----------------------------------------------------+",
                "");
    }
}
