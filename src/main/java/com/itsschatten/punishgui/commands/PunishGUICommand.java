package com.itsschatten.punishgui.commands;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.commandutils.UniversalCommand;
import com.itsschatten.punishgui.Permissions;
import com.itsschatten.punishgui.PunishGUI;
import com.itsschatten.punishgui.configs.Messages;
import com.itsschatten.punishgui.configs.Settings;
import com.itsschatten.punishgui.inventories.PunishInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The PunishGUI command is the command that administrative users can use to reload the files and get the version of the plugin.
 */
public class PunishGUICommand extends UniversalCommand {

    private final List<String> TAB_COMPLETE = Arrays.asList("reload", "rl", "test", "testInv", "version", "ver", "help"); // Static tab completable strings.
    private final List<String> CONFIGS = Arrays.asList("settings", "messages", "inventory"); // Static strings for the configs.

    public PunishGUICommand() {
        super("punishgui"); // The command.
        setPermission(Permissions.GeneralPermissions.PUNISH_USE.getPermission()); // Check if the player has permission.
        setPermissionMessage(Utils.getNoPermsMessage().replace("{prefix}", Messages.PREFIX).replace("{permission}", Permissions.GeneralPermissions.PUNISH_USE.getPermission())); // Message if not.
    }

    @Override
    protected void run(CommandSender sender, String[] args) {

        checkPerms(sender, Permissions.GeneralPermissions.PUNISH_USE); // Check permission again.
        checkArgs(1, Messages.NOT_ENOUGH_ARGS + "\n" + Messages.PUNISHGUI_HELP); // Makes sure that the command contains at least one argument.

        String param = args[0].toLowerCase(); // The argument of which we want to switch.

        switch (param) {

            case "reload": // If reload is found,  we can reload all files or ones that are defined as param2.
            case "rl": {
                checkPerms(sender, Permissions.AdminPermissions.PUNISHGUI_RELOAD); // We check if the player has permission to run the reload command.
                if (args.length == 1) { // Check if there are more than one argument, if not reloads all files.
                    Settings.getInstance().reload();
                    Messages.getInstance().reload();
                    PunishGUI.getInstance().loadInventoryConfigs();
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
                        PunishGUI.getInstance().loadInventoryConfigs(); // Reloads only the inventory file, returns a message saying that we reloaded the messages.yml.
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "inventories."));

                    default: // If nothing is found, we do nothing.
                        break;
                }
                break;
            }

            case "test":
            case "testinv": {
                checkPerms(sender, Permissions.AdminPermissions.TEST_INVENTORY);
                checkArgs(2, Messages.NOT_ENOUGH_ARGS);

                if (!(sender instanceof Player)) {
                    returnTell("This is a player only inventory.");
                    return;
                }

                Player player = (Player) sender;

                new PunishInventory().loadInvTest(player, player, "test reason", args[1]);
                break;
            }

            // Get the version for the plugin.
            case "version":
            case "ver": {
                checkPerms(sender, Permissions.AdminPermissions.PUNISHGUI_VERSION);
                returnTell("The version of the plugin is " + PunishGUI.getInstance().getDescription().getVersion());
                break;
            }

            // The help message for this command. (/punishgui)
            case "help": {
                returnTell(Messages.PUNISHGUI_HELP);
                break;
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
            if (sender.hasPermission(Permissions.GeneralPermissions.PUNISH_USE.getPermission())) { // Check if the person has permission.
                TAB_COMPLETE.forEach(complete -> { // Iterate over all TAB_COMPLETE values.
                    if (complete.startsWith(arg)) {
                        tab.add(complete); // Add them to tab.
                    }
                });

                return tab; // Return the tab.
            }
        }

        if (args.length == 2) { // If the command is reload, return file names.

            switch (args[0].toLowerCase()) {

                case "reload":
                case "rl": {
                    if (sender.hasPermission(Permissions.AdminPermissions.PUNISHGUI_RELOAD.getPermission())) {
                        List<String> conf = new ArrayList<>();
                        CONFIGS.forEach(confs -> {
                            if (confs.startsWith(args[1])) {
                                conf.add(confs);
                            }
                        });
                        return conf;
                    }
                    break;
                }

                case "test":
                case "testinv": {
                    if (sender.hasPermission(Permissions.AdminPermissions.TEST_INVENTORY.getPermission())) {

                        final File inventoryFolder = new File(PunishGUI.getInstance().getDataFolder() + "/inventories");
                        List<String> inventoryConfigs = new ArrayList<>();

                        for (File file : inventoryFolder.listFiles((file, fileName) -> fileName.toLowerCase().endsWith(".yml"))) {

                            if (file.getName().startsWith(args[1]))
                                inventoryConfigs.add(file.getName());
                        }

                        return inventoryConfigs;

                    }
                }

                default:
                    return Collections.emptyList();

            }
        }
        return Collections.emptyList(); // If nothing is correct, return nothing.
    }
}
