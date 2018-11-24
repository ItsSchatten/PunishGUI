package me.itsshadow.punishgui.events;

import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (PunishInv.getInstance().inConvoMap.containsValue(event.getPlayer().getUniqueId())) {
            PunishInv.getInstance().inConvoMap.remove(event.getPlayer().getName());
        }
    }
}
