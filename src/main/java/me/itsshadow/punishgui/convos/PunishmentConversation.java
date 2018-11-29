package me.itsshadow.punishgui.convos;

import me.itsshadow.libs.Utils;
import me.itsshadow.punishgui.PunishGUI;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Messages;
import me.itsshadow.punishgui.configs.Settings;
import me.itsshadow.punishgui.inventories.PunishInv;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class PunishmentConversation {

    // The punishment constructor or the main conversation.
    public PunishmentConversation(Player player) {
        final Conversation convo = new ConversationFactory(PunishGUI.getInstance())
                .withModality(false)
                .withPrefix(conversationContext -> Utils.colorize(Messages.CONVO_PREFIX.endsWith(" ") ? Messages.CONVO_PREFIX : Messages.CONVO_PREFIX + " "))
                .withTimeout(60)
                .withEscapeSequence(Settings.STOP_CONVERSTATION_PHRASE)
                .thatExcludesNonPlayersWithMessage(Utils.colorize("&cOnly players."))
                .addConversationAbandonedListener(e -> {
                    final Map<Object, Object> data = e.getContext().getAllSessionData();

                    if (e.gracefulExit()) {
                        Utils.tell(player, Messages.PUNISHMENT_SUCCESSFUL.replace("{player}", Bukkit.getPlayer(PunishInv.getInstance().targetMap.get(player.getName())).getName()));

                        String key = PunishInv.getInstance().keyMap.get(player.getName());

                        List<String> commands = InventoryConfig.getInstance().getStringList(key + ".commands");
                        Player target = Bukkit.getPlayer(PunishInv.getInstance().targetMap.get(player.getName()));

                        if (Settings.CONSOLE) {
                            ConsoleCommandSender console = PunishGUI.getInstance().getServer().getConsoleSender();

                            commands.forEach(command -> Bukkit.getServer().dispatchCommand(console, command.replace("{sender}", player.getName()).replace("{player}", target.getName()).replace("{reason}", data.get(Reason.REASON).toString())));

                            if (PunishInv.getInstance().inConvoMap.containsValue(player.getUniqueId()))
                                PunishInv.getInstance().inConvoMap.remove(player.getName());

                            PunishInv.getInstance().targetMap.remove(player.getName());
                            return;
                        }


                        commands.forEach(command -> player.performCommand(command.replace("{sender}", player.getName()).replace("{player}", target.getName()).replace("{reason}", data.get(Reason.REASON).toString())));
                    } else
                        Utils.tell(player, Messages.CONVO_CANCELED.replace("{player}", Bukkit.getPlayer(PunishInv.getInstance().targetMap.get(player.getName())).getName()));

                    if (PunishInv.getInstance().inConvoMap.containsValue(player.getUniqueId()))
                        PunishInv.getInstance().inConvoMap.remove(player.getName());
                    PunishInv.getInstance().targetMap.remove(player.getName());
                })
                .withFirstPrompt(new ReasonPrompt())
                .buildConversation(player);

        player.beginConversation(convo);
    }

    enum Reason {
        REASON
    }

    class ReasonPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            Player player = (Player) context.getForWhom();
            Player target = Bukkit.getPlayer(PunishInv.getInstance().targetMap.get(player.getName()));
            if (target == null) {
                return PunishInv.getInstance().targetMap.toString();
            }
            return Utils.colorize(Messages.REASON_PROMPT.replace("{player}", target.getName())).replace("{cancelphrase}", Settings.STOP_CONVERSTATION_PHRASE);
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData(Reason.REASON, input);

            return Prompt.END_OF_CONVERSATION;
        }
    }

}
