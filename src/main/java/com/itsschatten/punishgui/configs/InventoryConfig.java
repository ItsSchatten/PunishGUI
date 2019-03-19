package com.itsschatten.punishgui.configs;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.configutils.SimpleConfig;
import lombok.*;

public class InventoryConfig extends SimpleConfig {

    // See Settings.java

    @Getter
    private static InventoryConfig instance;

    public InventoryConfig(String fileName) {
        super(fileName);
        instance = this;
    }

    public static void init() {
        new InventoryConfig("inventory.yml");
    }

    public void reload() {
        init();                                                   ;
        Utils.debugLog("Reloaded inventory.yml");
    }


}
