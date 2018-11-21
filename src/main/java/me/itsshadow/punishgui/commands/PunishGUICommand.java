package me.itsshadow.punishgui.commands;

import me.itsshadow.libs.Utils;
import me.itsshadow.libs.commandutils.UniversalCommand;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Settings;
import org.bukkit.command.CommandSender;

public class PunishGUICommand extends UniversalCommand {

    public PunishGUICommand() {
        super("punishgui");
    }


    @Override
    protected void run(CommandSender sender, String[] args) {
        InventoryConfig.getInstance().reloadConfig();
        Settings.getInstance().reload();
        Utils.tell(sender, "merp.");
    }
}
