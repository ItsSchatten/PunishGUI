package com.itsschatten.punishgui.tasks;

import com.itsschatten.libs.UpdateNotifications;
import com.itsschatten.libs.Utils;
import com.itsschatten.punishgui.Perms;
import com.itsschatten.punishgui.PunishGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class is dedicated for looking for an update every 30 minutes, if one is found we send a message to all online players and to the console.
 */
public class CheckForUpdateTask extends BukkitRunnable {

    @Setter
    @Getter
    private static boolean updateAvailableInside = false; // Set a boolean variable to false, by default.

    @Override
    public void run() {
        Utils.log("Checking for update..."); // We log that we are checking for an update.

        new UpdateNotifications(62679) { // We call the UpdateNotifications class that is found in my utilities library.
            @Override
            public void onUpdateAvailable() { // When an update is available, we run this method.
                if (isUpdateAvailableInside()) { // We check if the update is available, and if it is we cancel this task.
                    cancel();
                }

                Bukkit.getOnlinePlayers().forEach(admins -> {
                    if (admins.hasPermission(Perms.AdminPermissions.PUNISHGUI_UPDATE_NOTIFICATIONS.getPermission())) { // We check if any online players have permission, if they do we send them a message that an update is available.
                        Utils.tell(admins, UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                                .replace("{newVer}", UpdateNotifications.getLatestVersion())
                                .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));
                    }
                });

                Utils.log(UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                        .replace("{newVer}", UpdateNotifications.getLatestVersion())
                        .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId())); // We log it to the console.

                setUpdateAvailableInside(true); // Sets the isUpdateAvailable boolean to true.
            }
        };
    }
}
