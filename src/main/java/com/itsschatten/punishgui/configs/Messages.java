package com.itsschatten.punishgui.configs;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.configutils.SimpleConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class Messages extends SimpleConfig {

    // See Settings.java

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    public static Messages instance;

    public static String PREFIX, NO_PERMS, PLAYER_DOESNT_EXIST, NOT_ENOUGH_ARGS,
            PUNISHGUI_HELP, PUNISH_HELP,
            RELOAD_CONFIGS, RELOAD_CONFIG_SPECIFIC,
            PUNISHMENT_SUCCESSFUL,
            UPDATE_AVAILABLE;

    public Messages(String fileName) {
        super(fileName);

        setHeader(new String[]{
                "--------------------------------------------------------",
                " This configuration file has been automatically updated!",
                "",
                " Unfortunately, due to the way Bukkit saves .yml files,",
                " all comments in your file where lost. To read them,",
                " please open " + fileName + " directly to browse the default values.",
                " Don't know how to do this? You can also check our github",
                " page for the default file.",
                "(https://github.com/ItsSchatten/PunishGUI)",
                "--------------------------------------------------------"});

        setInstance(this);
    }

    public static void init() {
        new Messages("messages.yml").onLoad();
        Utils.setPrefix(Messages.PREFIX);
        Utils.setUpdateAvailableMessage(Messages.UPDATE_AVAILABLE);
    }

    private void onLoad() {
        PREFIX = getString("prefix");
        NO_PERMS = getString("no-perms");

        PLAYER_DOESNT_EXIST = getString("player-doesnt-exist");
        NOT_ENOUGH_ARGS = getString("not-enough-arguments");

        PUNISHGUI_HELP = getString("punishgui-help");
        PUNISH_HELP = getString("punish-help");

        RELOAD_CONFIGS = getString("reload-configs");
        RELOAD_CONFIG_SPECIFIC = getString("reload-config-specific");

        PUNISHMENT_SUCCESSFUL = getString("punishment-successful");

        UPDATE_AVAILABLE = getString("update-available");
    }

    public void reload() {
        setInstance(null);

        init();

        Utils.debugLog("Reloaded messages.yml");
        setInstance(this);
    }

}
