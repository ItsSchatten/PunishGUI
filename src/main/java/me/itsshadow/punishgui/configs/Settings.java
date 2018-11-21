package me.itsshadow.punishgui.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.configutils.SimpleConfig;

public class Settings extends SimpleConfig {

    public static String PUNISH_INV_NAME, FILL_ITEM, PUNISH_OPEN_SOUND, UPDATE_AVALIABLE_SOUND;

    public static int INV_SIZE;

    public static float SOUND_PITCH;

    public static boolean FILL_SPARE_INV_SPACES, USE_RANDOM_SOUND_PITCH, USE_SOUNDS, USE_UPDATER, USE_CONVOS;

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static Settings instance;

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
                "(https://github.com/ItsShadow13/PunishGUI)",
                "--------------------------------------------------------"});

        setInstance(this);
    }

    public static void init() {
        new Settings("settings.yml").onLoad();
    }

    private void onLoad() {
        PUNISH_INV_NAME = getString("punish-inv-name");
        FILL_ITEM = getString("fill-item");
        PUNISH_OPEN_SOUND = getString("punish-open-sound");
        UPDATE_AVALIABLE_SOUND = getString("update-available-sound");


        INV_SIZE = getInt("inv-size");
        SOUND_PITCH = getInt("sound-pitch");

        FILL_SPARE_INV_SPACES = (boolean) get("fill-spare-inv-spaces");
        USE_RANDOM_SOUND_PITCH = (boolean) get("use-random-sound-pitch");
        USE_SOUNDS = (boolean) get("use-sounds");
        USE_UPDATER = (boolean) get("use-updater");
    }

    public void reload() {
        setInstance(null);

        new Settings("settings.yml").onLoad();

        setInstance(this);
    }
}
