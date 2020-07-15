package com.itsschatten.punishgui.configs;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.configutils.SimpleConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * This class is for the settings.yml.
 * It static values, and some methods to initialize and reload the file.
 */

public class Settings extends SimpleConfig {

    // Creates global variables to use throughout the plugin.
    public static String FILL_ITEM, PUNISH_OPEN_SOUND, UPDATE_AVAILABLE_SOUND, DEFAULT_REASON;

    public static float SOUND_PITCH;

    public static boolean DEBUG, FILL_SPARE_INV_SPACES, USE_RANDOM_SOUND_PITCH, USE_SOUNDS, USE_UPDATER, PERMISSION_ITEMS, DISALLOW_SHIFTCLICKING, USE_METRICS;

    // Instance stuffs.
    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static Settings instance;

    // Settings constructor.
    public Settings(String fileName) {
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

    // Initialize the plugin.
    public static void init() {
        new Settings("settings.yml").onLoad();
    }

    // Set the global variables to something.
    private void onLoad() {
        FILL_ITEM = getString("fill-item");
        PUNISH_OPEN_SOUND = getString("punish-open-sound");
        UPDATE_AVAILABLE_SOUND = getString("update-available-sound");
        DEFAULT_REASON = getString("default-reason");
        USE_METRICS = (boolean) get("metrics");
        SOUND_PITCH = getInt("sound-pitch");

        DEBUG = (boolean) get("debug");

        FILL_SPARE_INV_SPACES = (boolean) get("fill-spare-inv-spaces");
        USE_RANDOM_SOUND_PITCH = (boolean) get("use-random-sound-pitch");
        USE_SOUNDS = (boolean) get("use-sounds");
        USE_UPDATER = (boolean) get("use-updater");
        PERMISSION_ITEMS = (boolean) get("permission-items");
        DISALLOW_SHIFTCLICKING = (boolean) get("disallow-shiftclicking");
    }

    // The reload method.
    public void reload() {
        setInstance(null);
        init();
        Utils.debugLog(Settings.DEBUG, "Reloaded settings.yml");

    }
}
