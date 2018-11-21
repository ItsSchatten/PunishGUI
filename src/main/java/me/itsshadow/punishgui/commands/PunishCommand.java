package me.itsshadow.punishgui.commands;

import me.itsshadow.libs.commandutils.PlayerCommand;
import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishCommand extends PlayerCommand {

    public PunishCommand() {
        super("punish");
    }

    @Override
    protected void run(Player player, String[] args) {
        checkPerms(player, "", "punishgui.use");
        checkArgs(2, "");

        Player target = Bukkit.getPlayer(args[0]);

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String reason = sb.toString();

        PunishInv.createPunishInv(player, target, reason);
        returnTell("");
    }
}
