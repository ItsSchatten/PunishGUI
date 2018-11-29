package me.itsshadow.punishgui.commands;

import me.itsshadow.libs.commandutils.UniversalCommand;
import me.itsshadow.punishgui.PunishGUI;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Messages;
import me.itsshadow.punishgui.configs.Settings;
import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PunishGUICommand extends UniversalCommand {

    List<String> TABCOMPLETE = Arrays.asList("reload", "rl", "version", "ver", "help", "resettimer");
    List<String> CONFIGS = Arrays.asList("settings", "messages", "inventory");

    public PunishGUICommand() {
        super("punishgui");
    }


    // The command method.
    @Override
    protected void run(CommandSender sender, String[] args) {

        // Check args and perms.
        checkArgs(1, Messages.NOT_ENOUGH_ARGS + "\n" +Messages.PUNISHGUI_HELP);
        checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.use"), "punishgui.use");

        // Create a String value to switch.
        String param = args[0].toLowerCase();

        switch (param) {
            // The reload argument.
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

            // Get the version for the plugin.
            case "version":
                checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.admin.version"), "punishgui.admin.version");
                returnTell("The version of the plugin is " + PunishGUI.getInstance().getDescription().getVersion());

            case "ver":
                checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.admin.version"), "punishgui.admin.version");
                returnTell("The version of the plugin is " + PunishGUI.getInstance().getDescription().getVersion());

            // If a player is in the Convo map it will remove them.
            case "resettimer": {
                checkArgs(2, Messages.NOT_ENOUGH_ARGS + "\n" + Messages.PUNISHGUI_HELP);
                Player player = Bukkit.getPlayerExact(args[1]);
                checkNotNull(player, Messages.PLAYER_DOESNT_EXIST);

                if (Settings.USE_CONVO_MAP) {
                    checkPerms(sender, Messages.NO_PERMS.replace("{permission}", "punishgui.admin.resettimer"), "punishgui.admin.resettimer");

                    if (PunishInv.getInstance().inConvoMap.containsValue(player.getUniqueId())) {
                        PunishInv.getInstance().inConvoMap.remove(player.getName(), player.getUniqueId());

                        returnTell(Messages.RESET_TIMER_SUCCESS.replace("{player}", player.getName()));
                    } else {
                        returnTell(Messages.RESET_TIMER_FAILED.replace("{player}", player.getName()));
                    }
                } else {
                    returnTell(Messages.RESET_TIMER_FAILED.replace("{player}", player.getName()));
                }
                break;
            }

            // The help message for this command. (/punishgui)
            case "help":
               returnTell(Messages.PUNISHGUI_HELP);

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
            if (sender.hasPermission("punishgui.use")) {
                TABCOMPLETE.forEach(complete -> {
                    if (complete.startsWith(arg)) {
                        tab.add(complete);
                    }
                });

                return tab;
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl") && sender.hasPermission("punishgui.admin.reload")) {
            List<String> conf = new ArrayList<>();
            CONFIGS.forEach(confs -> {
                if (confs.startsWith(args[1])) {
                    conf.add(confs);
                }
            });

            return conf;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("resettimer") && sender.hasPermission("punishgui.admin.resettimer")) {
            List<String> players = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(pl -> {
                if (pl.getName().contains(args[1]))
                    players.add(pl.getName());
            });

            return players;
        }

        return null;
    }
}
