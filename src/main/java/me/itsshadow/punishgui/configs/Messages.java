package me.itsshadow.punishgui.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.configutils.SimpleConfig;

public class Messages extends SimpleConfig {

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    public static Messages instance;

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

    }

    public void reload(){
        setInstance(null);

        new Messages("messages.yml").onLoad();

        setInstance(this);
    }

}
