package com.itsschatten.punishgui;

import com.itsschatten.libs.Permissions;
import com.itsschatten.punishgui.configs.Messages;

public class Perms {

    private static final String noPerms = Messages.NO_PERMS;

    public enum GeneralPermissions implements Permissions {
        PUNISH_USE {
            @Override
            public String getPermission() {
                return "punishgui.use";
            }

            @Override
            public String getNoPermission() {
                return noPerms;
            }
        }
    }

    public enum AdminPermissions implements Permissions {
        PUNISHGUI_RELOAD {
            @Override
            public String getPermission() {
                return "punishgui.admin.reload";
            }

            @Override
            public String getNoPermission() {
                return noPerms;
            }
        },

        PUNISHGUI_VERSION {
            @Override
            public String getPermission() {
                return "punishgui.admin.version";
            }

            @Override
            public String getNoPermission() {
                return noPerms;
            }
        },

        PUNISHGUI_UPDATE_NOTIFICATIONS {
            @Override
            public String getPermission() {
                return "punishgui.admin.update";
            }

            @Override
            public String getNoPermission() {
                return noPerms;
            }
        }
    }

}
