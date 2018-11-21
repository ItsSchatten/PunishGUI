package me.itsshadow.punishgui.commands;

import me.itsshadow.libs.commandutils.PlayerCommand;
import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.entity.Player;

public class PunishCommand extends PlayerCommand {

    public PunishCommand() {
        super("punish");
    }

    @Override
    protected void run(Player player, String[] strings) {
        PunishInv.createPunishInv(player, player, "merp");
    }
}
