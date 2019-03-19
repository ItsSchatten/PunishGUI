package com.itsschatten.punishgui.commands;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.commandutils.PlayerCommand;
import com.itsschatten.punishgui.Perms;
import com.itsschatten.punishgui.configs.Settings;
import com.itsschatten.punishgui.inventories.PunishInventory;
import com.itsschatten.punishgui.configs.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishCommand extends PlayerCommand {

    public PunishCommand() {
        super("punish");
        setPermission(Perms.GeneralPermissions.PUNISH_USE.getPermission());
    }

    @Override
    protected void run(Player player, String[] args) {
        checkPerms(player, Perms.GeneralPermissions.PUNISH_USE);
        checkArgs(1, Messages.NOT_ENOUGH_ARGS + "\n" + Messages.PUNISH_HELP);

        Player target = Bukkit.getPlayerExact(args[0]);

        checkNotNull(target, Messages.PLAYER_DOESNT_EXIST.replace("{targetMap}", args[0]));

        if (args.length != 2) {
            Utils.debugLog(Settings.DEBUG, "No reason was set, setting reason to null to be set to the default later.");
            new PunishInventory().loadInv(player, target, null);
            return;
        }

        Utils.debugLog(Settings.DEBUG, "Found a reason! Setting it...");
        String reason;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        reason = sb.toString();

        new PunishInventory().loadInv(player, target, reason);
        return;
    }
}
