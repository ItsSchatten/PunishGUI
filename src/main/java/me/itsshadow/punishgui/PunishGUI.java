package me.itsshadow.punishgui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.Utils;
import org.apache.commons.lang.StringUtils;
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
                StringUtils.center("SomeThingTHisTime", 0));

        Utils.log("",
                "&9+---------------------------------------------------+ ",
                "");
    }



}
