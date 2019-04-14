package com.itsschatten.punishgui.commands;

import com.itsschatten.libs.commandutils.UniversalCommand;
import com.itsschatten.punishgui.Perms;
import com.itsschatten.punishgui.PunishGUI;
import com.itsschatten.punishgui.configs.InventoryConfig;
import com.itsschatten.punishgui.configs.Messages;
import com.itsschatten.punishgui.configs.Settings;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The PunishGUI command is the command that administrative users can use to reload the files and get the version of the plugin.
 */
public class PunishGUICommand extends UniversalCommand {

    private final List<String> TAB_COMPLETE = Arrays.asList("reload", "rl", "version", "ver", "help"); // Static tab completable strings.
    private final List<String> CONFIGS = Arrays.asList("settings", "messages", "inventory"); // Static strings for the configs.

    public PunishGUICommand() {
        super("punishgui"); // The command.
        setPermission(Perms.GeneralPermissions.PUNISH_USE.getPermission()); // Check if the player has permission.
        setPermissionMessage(Perms.GeneralPermissions.PUNISH_USE.getNoPermission().replace("{prefix}", Messages.PREFIX).replace("{permission}", Perms.GeneralPermissions.PUNISH_USE.getPermission())); // Message if not.
    }

    @Override
    protected void run(CommandSender sender, String[] args) {

        checkPerms(sender, Perms.GeneralPermissions.PUNISH_USE); // Check permission again.
        checkArgs(1, Messages.NOT_ENOUGH_ARGS + "\n" + Messages.PUNISHGUI_HELP); // Makes sure that the command contains at least one argument.

        String param = args[0].toLowerCase(); // The argument of which we want to switch.

        switch (param) {

            case "reload": // If reload is found,  we can reload all files or ones that are defined as param2.
            case "rl": {
                checkPerms(sender, Perms.AdminPermissions.PUNISHGUI_RELOAD); // We check if the player has permission to run the reload command.
                if (args.length == 1) { // Check if there are more than one argument, if not reloads all files.
                    Settings.getInstance().reload();
                    Messages.getInstance().reload();
                    InventoryConfig.getInstance().reload();
                    returnTell(Messages.RELOAD_CONFIGS);
                }

                String param2 = args[1].toLowerCase(); // The second argument in the command.

                switch (param2) { // We switch the parameter to see if it equals settings, messages, or inventory.
                    case "settings":
                        Settings.getInstance().reload(); // Reloads only the settings file, returns a message saying that we reloaded the settings.yml.
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "settings.yml"));

                    case "messages":
                        Messages.getInstance().reload(); // Reloads only the messages file, returns a message saying that we reloaded the messages.yml.
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "messages.yml"));

                    case "inventory":
                        InventoryConfig.getInstance().reload(); // Reloads only the inventory file, returns a message saying that we reloaded the messages.yml.
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "inventory.yml"));

                    default: // If nothing is found, we do nothing.
                        break;
                }
                break;
            }

            // Get the version for the plugin.
            case "version":
            case "ver": {
                checkPerms(sender, Perms.AdminPermissions.PUNISHGUI_VERSION);
                returnTell("The version of the plugin is " + PunishGUI.getInstance().getDescription().getVersion());
            }

            // The help message for this command. (/punishgui)
            case "help": {
                returnTell(Messages.PUNISHGUI_HELP);
            }

            // End of switch.
            default:
                break;
        }
    }


    // The tabCompleter.
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> tab = new ArrayList<>(); // We get the tab list that we can return to the player that is tabbing.

        if (args.length == 1) { // If the arguments are one, we return what can be tabbed.
            String arg = args[0].toLowerCase(); // Get the first argument in the command.
            if (sender.hasPermission(Perms.GeneralPermissions.PUNISH_USE.getPermission())) { // Check if the person has permission.
                TAB_COMPLETE.forEach(complete -> { // Iterate over all TAB_COMPLETE values.
                    if (complete.startsWith(arg)) {
                        tab.add(complete); // Add them to tab.
                    }
                });

                return tab; // Return the tab.
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl") && sender.hasPermission(Perms.AdminPermissions.PUNISHGUI_RELOAD.getPermission())) { // If the command is reload, return file names.
            List<String> conf = new ArrayList<>();
            CONFIGS.forEach(confs -> {
                if (confs.startsWith(args[1])) {
                    conf.add(confs);
                }
            });

            return conf;
        }
        return null; // If nothing is correct, return nothing.
    }
}
