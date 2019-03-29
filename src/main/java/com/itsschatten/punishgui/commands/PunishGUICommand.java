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

public class PunishGUICommand extends UniversalCommand {

    private final List<String> TAB_COMPLETE = Arrays.asList("reload", "rl", "version", "ver", "help");
    private final List<String> CONFIGS = Arrays.asList("settings", "messages", "inventory");

    public PunishGUICommand() {
        super("punishgui");
        setPermission(Perms.GeneralPermissions.PUNISH_USE.getPermission());
    }


    // The command method.
    @Override
    protected void run(CommandSender sender, String[] args) {

        // Check args and perms.
        checkPerms(sender, Perms.GeneralPermissions.PUNISH_USE);
        checkArgs(1, Messages.NOT_ENOUGH_ARGS + "\n" + Messages.PUNISHGUI_HELP);


        // Create a String value to switch.
        String param = args[0].toLowerCase();

        switch (param) {

            // Reloads the configuration files.
            case "reload": {
                checkPerms(sender, Perms.AdminPermissions.PUNISHGUI_RELOAD);
                if (args.length == 1) {
                    Settings.getInstance().reload();
                    Messages.getInstance().reload();
                    InventoryConfig.getInstance().reload();
                    returnTell(Messages.RELOAD_CONFIGS);
                }

                String param2 = args[1].toLowerCase();

                switch (param2) {
                    case "settings":
                        Settings.getInstance().reload();
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "settings.yml"));

                    case "messages":
                        Messages.getInstance().reload();
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "messages.yml"));

                    case "inventory":
                        InventoryConfig.getInstance().reload();
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "inventory.yml"));

                    default:
                        break;
                }
                break;
            }
            case "rl": {
                checkPerms(sender, Perms.AdminPermissions.PUNISHGUI_RELOAD);
                if (args.length == 1) {
                    Settings.getInstance().reload();
                    Messages.getInstance().reload();
                    InventoryConfig.getInstance().reload();
                    returnTell(Messages.RELOAD_CONFIGS);
                }

                String rlParam2 = args[1].toLowerCase();

                switch (rlParam2) {
                    case "settings":
                        Settings.getInstance().reload();
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "settings.yml"));

                    case "messages":
                        Messages.getInstance().reload();
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "messages.yml"));

                    case "inventory":
                        InventoryConfig.getInstance().reload();
                        returnTell(Messages.RELOAD_CONFIG_SPECIFIC.replace("{reloadedconfig}", "inventory.yml"));

                        break;

                    default:
                        break;
                }
                break;
            }

            // Get the version for the plugin.
            case "version": {
                checkPerms(sender, Perms.AdminPermissions.PUNISHGUI_VERSION);
                returnTell("The version of the plugin is " + PunishGUI.getInstance().getDescription().getVersion());
            }
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
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            String arg = args[0].toLowerCase();
            if (sender.hasPermission(Perms.GeneralPermissions.PUNISH_USE.getPermission())) {
                TAB_COMPLETE.forEach(complete -> {
                    if (complete.startsWith(arg)) {
                        tab.add(complete);
                    }
                });

                return tab;
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl") && sender.hasPermission(Perms.AdminPermissions.PUNISHGUI_RELOAD.getPermission())) {
            List<String> conf = new ArrayList<>();
            CONFIGS.forEach(confs -> {
                if (confs.startsWith(args[1])) {
                    conf.add(confs);
                }
            });

            return conf;
        }
        return null;
    }
}
