package me.itsshadow.punishgui.commands;

import me.itsshadow.libs.commandutils.UniversalCommand;
import me.itsshadow.punishgui.PunishGUI;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Messages;
import me.itsshadow.punishgui.configs.Settings;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PunishGUICommand extends UniversalCommand {

    List<String> TABCOMPLETE = Arrays.asList("reload", "rl", "version", "ver", "help");
    List<String> CONFIGS = Arrays.asList("settings", "messages", "inventory");

    public PunishGUICommand() {
        super("punishgui");
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        checkArgs(1, "");
        checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.use"), "punishgui.use");
        if (args.length == 0) {
            returnTell(Messages.NOT_ENOUGH_ARGS + "\n" + Messages.PUNISHGUI_HELP);
        }

        String param = args[0].toLowerCase();

        switch (param) {
            case "reload":
                checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.admin.reload"), "punishgui.admin.reload");
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

            case "rl":
                checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.admin.reload"), "punishgui.admin.reload");
                if (args.length == 1) {
                    Settings.getInstance().reload();
                    Messages.getInstance().reload();
                    InventoryConfig.getInstance().reload();
                    returnTell(Messages.RELOAD_CONFIGS);
                }

                String rlparam2 = args[1].toLowerCase();

                switch (rlparam2) {
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

            case "version":
                checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.admin.version"), "punishgui.admin.version");
                returnTell("The version of the plugin is " + PunishGUI.getInstance().getDescription().getVersion());

            case "ver":
                checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.admin.version"), "punishgui.admin.version");
                returnTell("The version of the plugin is " + PunishGUI.getInstance().getDescription().getVersion());

            case "help":
               returnTell(Messages.PUNISHGUI_HELP);

            default:
                break;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            String arg = args[0].toLowerCase();
            if (sender.hasPermission("punishgui.use")) {
                TABCOMPLETE.forEach(complete -> {
                    if (complete.startsWith(arg)) {
                        tab.add(complete);
                    }
                });

                return tab;
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
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
