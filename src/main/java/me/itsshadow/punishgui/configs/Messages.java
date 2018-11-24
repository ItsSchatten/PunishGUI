package me.itsshadow.punishgui.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.Utils;
import me.itsshadow.libs.configutils.SimpleConfig;

public class Messages extends SimpleConfig {

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    public static Messages instance;

    public static String PREFIX, NO_PERMS, PLAYER_DOESNT_EXIST, NOT_ENOUGH_ARGS,
            PUNISHGUI_HELP, PUNISH_HELP,
            CONVO_PREFIX, REASON_PROMPT, CONVO_CANCELED,
            RELOAD_CONFIGS, RELOAD_CONFIG_SPECIFIC,
            PUNISHMENT_SUCCESSFUL, ALREADY_IN_CONVO,
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
                "(https://github.com/ItsShadow13/PunishGUI)",
                "--------------------------------------------------------"});

        setInstance(this);
    }

    public static void init() {
        new Messages("messages.yml").onLoad();
    }

    private void onLoad() {
        PREFIX = getString("prefix");
        NO_PERMS = getString("no-perms");

        PLAYER_DOESNT_EXIST = getString("player-doesnt-exist");
        NOT_ENOUGH_ARGS = getString("not-enough-arguments");

        PUNISHGUI_HELP = getString("punishgui-help");
        PUNISH_HELP = getString("punish-help");

        REASON_PROMPT = getString("reason-prompt");
        CONVO_PREFIX = getString("convo-prefix");
        CONVO_CANCELED = getString("convo-canceled-message");

        RELOAD_CONFIGS = getString("reload-configs");
        RELOAD_CONFIG_SPECIFIC = getString("reload-config-specific");

        PUNISHMENT_SUCCESSFUL = getString("punishment-successful");
        ALREADY_IN_CONVO = getString("already-in-convo");

        UPDATE_AVAILABLE = getString("update-available");
    }

    public void reload() {
        setInstance(null);

        new Messages("messages.yml").onLoad();

        Utils.setPrefix(Messages.PREFIX);
        Utils.setNoPermsMessage(Messages.NO_PERMS);
        Utils.setUpdateAvailableMessage(Messages.UPDATE_AVAILABLE);


        setInstance(this);
    }

}
