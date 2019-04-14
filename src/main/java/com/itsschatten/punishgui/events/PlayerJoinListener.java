package com.itsschatten.punishgui.events;

import com.itsschatten.libs.UpdateNotifications;
import com.itsschatten.libs.Utils;
import com.itsschatten.punishgui.Perms;
import com.itsschatten.punishgui.PunishGUI;
import com.itsschatten.punishgui.configs.Settings;
import com.itsschatten.punishgui.tasks.CheckForUpdateTask;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This is dedicated to sending a message to the player in chat, when the join the server.
 */
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Settings.USE_UPDATER &&
                (CheckForUpdateTask.isUpdateAvailableInside() ||
                UpdateNotifications.isUpdateAvailable())
                && event.getPlayer().hasPermission(Perms.AdminPermissions.PUNISHGUI_UPDATE_NOTIFICATIONS.getPermission())) { // If everything is true, and the player has permission, we send them the update message.
            Utils.tell(event.getPlayer(), UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                    .replace("{newVer}", UpdateNotifications.getLatestVersion())
                    .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));

            if (Settings.USE_SOUNDS) { // If sounds are true, play them a sound.
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf(Settings.UPDATE_AVAILABLE_SOUND), 1, Settings.USE_RANDOM_SOUND_PITCH ? (float) Math.random() : Settings.SOUND_PITCH);
            }
        }
    }

}
