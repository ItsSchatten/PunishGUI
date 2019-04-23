package com.itsschatten.punishgui.commands;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.commandutils.PlayerCommand;
import com.itsschatten.punishgui.Perms;
import com.itsschatten.punishgui.configs.Messages;
import com.itsschatten.punishgui.configs.Settings;
import com.itsschatten.punishgui.inventories.PunishInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The PunishCommand is the main command that most staff members will use to punish players.
 * Staff members that have permission to use the command will be able to open the inventory.
 * You can set a custom reason by running the command like so: <code>/punish (PLAYERNAME) (Reason)</code>
 */
public class PunishCommand extends PlayerCommand {

    public PunishCommand() {
        super("punish"); // The command name.
        setPermission(Perms.GeneralPermissions.PUNISH_USE.getPermission()); // We set the permission that is needed for the player to run the command.
        setPermissionMessage(Perms.GeneralPermissions.
                PUNISH_USE.getNoPermission().replace("{prefix}", Messages.PREFIX).replace("{permission}", Perms.GeneralPermissions.PUNISH_USE.getPermission())); // If player doesn't have perms, the message sent.
    }

    @Override
    protected void run(Player player, String[] args) {
        checkPerms(player, Perms.GeneralPermissions.PUNISH_USE); // Secondary check, for permissions.
        checkArgs(1, Messages.NOT_ENOUGH_ARGS + "\n" + Messages.PUNISH_HELP); // Requires the user to have at least one argument for the command to run successfully.

        Player target = Bukkit.getPlayerExact(args[0]); // We set the target player.

        checkNotNull(target, Messages.PLAYER_DOESNT_EXIST.replace("{target}", args[0])); // Ensure that the target is not null. (Not online or doesn't exist in general.)

        if (args.length <= 1) { // Checking if the command is less than or equal to 1.
            Utils.debugLog(Settings.DEBUG, "No reason was set, setting reason to null to be set to the default later."); // Debug message to be able to tell that there wasn't a custom reason set.
            new PunishInventory().loadInv(player, target, null); // Open the inventory, for the player and set the reason to null.
            return; // Return the command.
        }

        Utils.debugLog(Settings.DEBUG, "Found a reason! Setting it..."); // We've found the command contains arguments that are larger than 1.

        String reason;
        StringBuilder sb = new StringBuilder(" ");
        for (int i = 1; i < args.length; i++) { // We build the message using a string builder.
            sb.append(args[i]); // If not the last, append a space.
        }

        reason = sb.toString().trim(); // Set the result of the string builder to the reason string, and remove any trailing whitespaces.

        new PunishInventory().loadInv(player, target, reason); // Open the inventory for the player, and sets the custom reason.
        return;
    }
}
