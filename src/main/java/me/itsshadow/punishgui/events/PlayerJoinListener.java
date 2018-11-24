package me.itsshadow.punishgui.events;

import me.itsshadow.libs.UpdateNotifications;
import me.itsshadow.libs.Utils;
import me.itsshadow.punishgui.PunishGUI;
import me.itsshadow.punishgui.configs.Settings;
import me.itsshadow.punishgui.tasks.CheckForUpdateTask;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Settings.USE_UPDATER && CheckForUpdateTask.isUpdateAvailableInside() || UpdateNotifications.isUpdateAvailable() && event.getPlayer().hasPermission("punishgui.notify.update")) {
            Utils.tell(event.getPlayer(), UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                    .replace("{newVer}", UpdateNotifications.getLatestVersion())
                    .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));

            if (Settings.USE_SOUNDS) {
                if (Settings.USE_RANDOM_SOUND_PITCH) {
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf(Settings.UPDATE_AVAILABLE_SOUND), 1, (float) Math.random());
                } else {
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.valueOf(Settings.UPDATE_AVAILABLE_SOUND), 1, Settings.SOUND_PITCH);
                }
            }
        }
    }

}
