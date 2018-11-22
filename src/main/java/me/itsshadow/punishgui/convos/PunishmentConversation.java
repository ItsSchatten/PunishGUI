package me.itsshadow.punishgui.convos;

import me.itsshadow.libs.Utils;
import me.itsshadow.punishgui.PunishGUI;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class PunishmentConversation {

    public PunishmentConversation(Player player) {
        final Conversation convo = new ConversationFactory(PunishGUI.getInstance())
                .withModality(false)
                .withPrefix(conversationContext -> "")
                .withTimeout(60)
                .withEscapeSequence("stop")
                .thatExcludesNonPlayersWithMessage("")
                .addConversationAbandonedListener(e -> {
                final Conversable pl = e.getContext().getForWhom();
                final Map<Object, Object> data = e.getContext().getAllSessionData();

                    if (e.gracefulExit()) {
                        Utils.tell(player, "");

                        for (String key : InventoryConfig.getInstance().getKeys(false)) {
                            List<String> commands = InventoryConfig.getInstance().getStringList(key + ".commands");

                            commands.forEach(command -> player.performCommand(command.replace("{sender}", player.getName()).replace("{player}", PunishInv.getTarget().getName()).replace("{reason}", data.get(Reason.REASON).toString())));
                        }
                    } else
                        Utils.tell(player, "");
                })
                .withFirstPrompt(new ReasonPrompt())
                .buildConversation(player);

        player.beginConversation(convo);
    }

    enum Reason {
        REASON;
    }

    class ReasonPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return "";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData(Reason.REASON, input);

            return Prompt.END_OF_CONVERSATION;
        }
    }

}
