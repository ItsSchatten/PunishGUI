package me.itsshadow.punishgui.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.Utils;
import me.itsshadow.libs.configutils.SimpleConfig;

public class InventoryConfig extends SimpleConfig {

    // See Settings.java

    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private static InventoryConfig instance;

    public InventoryConfig(String fileName) {
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

    public static void init() {
        new InventoryConfig("inventory.yml");
    }


    public void reload(){

        new InventoryConfig("inventory.yml");

        Utils.log("Reloaded inventory.yml");
   }


}
