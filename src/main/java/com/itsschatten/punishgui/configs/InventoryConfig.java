package com.itsschatten.punishgui.configs;

import com.itsschatten.libs.Utils;
import com.itsschatten.libs.configutils.SimpleConfig;
import lombok.*;

/**
 * This class is for the Inventory.yml file. It contains some
 * simple methods that register and reload the file.
 *
 */
public class InventoryConfig extends SimpleConfig {

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
        init();
        Utils.debugLog("Reloaded inventory.yml");
    }


}
