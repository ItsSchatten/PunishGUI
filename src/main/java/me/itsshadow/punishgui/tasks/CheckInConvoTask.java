package me.itsshadow.punishgui.tasks;

import me.itsshadow.punishgui.configs.Settings;
import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckInConvoTask extends BukkitRunnable {

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (PunishInv.getInstance().inConvoMap.containsValue(player.getUniqueId())) {
                if (player.getOpenInventory() == null) {
                    PunishInv.getInstance().inConvoMap.remove(player.getName());
                }

                if (!player.getOpenInventory().getTitle().contains(Settings.PUNISH_INV_NAME.replace("{player}", ""))) {
                    PunishInv.getInstance().inConvoMap.remove(player.getName());
                }
            }
        });
    }


}
