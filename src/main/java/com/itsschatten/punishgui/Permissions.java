package com.itsschatten.punishgui;

import com.itsschatten.libs.interfaces.IPermissions;

/**
 * The permissions class contains all he permissions that are used in the plugin.
 * It also helps to reduce the amount of times that the permission must be typed.
 * All permissions are also listed in the plugin.yml for ease of use for the average chum.
 */
public class Permissions {

    // The general permissions for staff.
    public enum GeneralPermissions implements IPermissions {
        PUNISH_USE {
            @Override
            public String getPermission() {
                return "punishgui.use";
                // This permission is needed to be able to use the punish command.
                // By default this is given to op players.
            }
        }
    }

    public enum AdminPermissions implements IPermissions {
        PUNISHGUI_RELOAD {
            @Override
            public String getPermission() {
                return "punishgui.admin.reload";
                // This permission is needed to be able to reload the files.
                // By default this is given to op players.
            }
        },

        PUNISHGUI_VERSION {
            @Override
            public String getPermission() {
                return "punishgui.admin.version";
                // This permission is needed to be able to see the version of the plugin.
                // By default this is given to op players.
            }
        },

        PUNISHGUI_UPDATE_NOTIFICATIONS {
            @Override
            public String getPermission() {
                return "punishgui.admin.update";
                // This permission is needed to be able to see the update message when there is an update available.
                // By default this is given to op players.
            }
        },

        TEST_INVENTORY {
            @Override
            public String getPermission() {
                return "punishgui.admin.testinv";
            }
        }
    }

}
