package com.itsschatten.punishgui.events;

import com.itsschatten.libs.UpdateNotifications;
import com.itsschatten.libs.Utils;
import com.itsschatten.punishgui.Permissions;
import com.itsschatten.punishgui.configs.Settings;
import com.itsschatten.punishgui.tasks.CheckForUpdateTask;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * This is dedicated to sending a message to the player in chat, when the join the server.
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Settings.USE_UPDATER && (CheckForUpdateTask.isUpdateAvailableInside() || UpdateNotifications.isUpdateAvailable()) && event.getPlayer().hasPermission(Permissions.AdminPermissions.PUNISHGUI_UPDATE_NOTIFICATIONS.getPermission())) { // If everything is true, and the player has permission, we send them the update message.
            if (Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_15_R1")) {
                PluginDescriptionFile pdf = Utils.getInstance().getDescription(); // So we can get the version.
                Utils.debugLog(Settings.DEBUG, "Found an update for the plugin, sending the message to the player.");
                Utils.tell(event.getPlayer(), StringUtils.replaceEach(UpdateNotifications.getUpdateMessage(), new String[]{"{currentVer}", "{newVer}", "{link}"},
                        new String[]{pdf.getVersion(), UpdateNotifications.getLatestVersion(), "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()}));
                return;
            }
            Utils.debugLog(Settings.DEBUG, "There is an update to the plugin available but the version is not the latest supported version. Not sending a message to the player...");

            if (Settings.USE_SOUNDS) { // If sounds are true, play them a sound.
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf(Settings.UPDATE_AVAILABLE_SOUND), 1, Settings.USE_RANDOM_SOUND_PITCH ? (float) Math.random() : Settings.SOUND_PITCH);
            }
        }
    }

}
