package me.itsshadow.punishgui.commands;

import me.itsshadow.libs.commandutils.PlayerCommand;
import me.itsshadow.punishgui.configs.Messages;
import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishCommand extends PlayerCommand {

    public PunishCommand() {
        super("punish");
    }

    @Override
    protected void run(Player player, String[] args) {
        checkPerms(player, Messages.NO_PERMS.replace("{permission}", "punishgui.use"), "punishgui.use");
        checkArgs(1, Messages.NOT_ENOUGH_ARGS + "\n" + Messages.PUNISH_HELP);

        Player target = Bukkit.getPlayerExact(args[0]);

        checkNotNull(target, Messages.PLAYER_DOESNT_EXIST.replace("{player}", args[0]));

        PunishInv.getInstance().createPunishInv(player, target);
        return;
    }
}
