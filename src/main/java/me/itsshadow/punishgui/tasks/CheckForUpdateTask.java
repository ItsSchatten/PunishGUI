package me.itsshadow.punishgui.tasks;

import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.UpdateNotifications;
import me.itsshadow.libs.Utils;
import me.itsshadow.punishgui.PunishGUI;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckForUpdateTask extends BukkitRunnable {


    // This whole class checks every hour if there is an update available.

    @Setter
    @Getter
    private static boolean updateAvailableInside = false;

    @Override
    public void run() {
        Utils.log("Checking for update...");

        new UpdateNotifications(62679) {
            @Override
            public void onUpdateAvailable() {
                if (isUpdateAvailableInside()) {
                    cancel();
                }

                Bukkit.getOnlinePlayers().forEach(admins -> {
                    if (admins.hasPermission("punishgui.notify.update")) {
                        Utils.tell(admins, UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                                .replace("{newVer}", UpdateNotifications.getLatestVersion())
                                .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));
                    }
                });

                Utils.log(UpdateNotifications.getUpdateMessage().replace("{currentVer}", PunishGUI.getInstance().getDescription().getVersion())
                        .replace("{newVer}", UpdateNotifications.getLatestVersion())
                        .replace("{link}", "https://spigotmc.org/resources/" + UpdateNotifications.getProjectId()));

                setUpdateAvailableInside(true);
            }
        };
    }
}
