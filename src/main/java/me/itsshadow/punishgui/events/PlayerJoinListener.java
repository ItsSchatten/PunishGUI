package me.itsshadow.punishgui.events;

import me.itsshadow.libs.UpdateNotifications;
import me.itsshadow.libs.Utils;
import me.itsshadow.punishgui.PunishGUI;
import me.itsshadow.punishgui.tasks.CheckForUpdateTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (CheckForUpdateTask.isUpdateAvailableInside() || UpdateNotifications.isUpdateAvailable() && event.getPlayer().hasPermission("punishgui.notify.update")) {
            Utils.tell(event.getPlayer(), UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                    .replace("{newVer}", UpdateNotifications.getLatestVersion())
                    .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));
        }
    }

}
