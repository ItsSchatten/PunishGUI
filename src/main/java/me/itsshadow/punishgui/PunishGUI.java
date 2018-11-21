package me.itsshadow.punishgui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.Utils;
import me.itsshadow.punishgui.commands.PunishCommand;
import me.itsshadow.punishgui.commands.PunishGUICommand;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Settings;
import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishGUI extends JavaPlugin {

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static PunishGUI instance;

    @Override
    public void onEnable() {
        Utils.setInstance(this);
        setInstance(this);

        register();
    }

    @Override
    public void onDisable() {
        Utils.setInstance(null);
        setInstance(null);
    }

    private void register() {
        Utils.log("",
                "&9+---------------------------------------------------+ ",
                " _____             _     _        _____ _    _ _____",
                "| __ \\           (_)   | |      / ____| |  | |_   _|",
                "| |__) |   _ _ __  _ ___| |__   | |  __| |  | | | |",
                "|  ___/ | | | '_ \\| / __| '_ \\  | | |_ | |  | | | |",
                "| |   | |_| | | | | \\__ \\ | | | | |__| | |__| |_| |_",
                "|_|    \\__,_|_| |_|_|___/_| |_|  \\_____|\\____/|_____|",


                "");

        Utils.registerCommand(new PunishCommand());
        Utils.registerCommand(new PunishGUICommand());
        Utils.log("&7All commands have been initialized..");

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new PunishInv(), this);

        InventoryConfig.init();
        Settings.init();

        Utils.log("",
                "&9+---------------------------------------------------+ ",
                "");
    }


}
