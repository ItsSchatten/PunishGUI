package me.itsshadow.punishgui.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.configutils.SimpleConfig;

public class Settings extends SimpleConfig {

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
                " all comments in your file where lost. please open",
                " " + fileName + " directly to browse the default values.",
                " Don't know how to do this? You can also check our github",
                " page for a default file.",
                "--------------------------------------------------------"});

        setInstance(this);
    }

    public void init() {
        new Settings("settings.yml").onLoad();
    }

    private void onLoad(){

    }

}
