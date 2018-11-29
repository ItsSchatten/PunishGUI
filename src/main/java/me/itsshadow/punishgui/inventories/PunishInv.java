package me.itsshadow.punishgui.inventories;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.itsshadow.libs.Utils;
import me.itsshadow.libs.inventories.InventoryUtils;
import me.itsshadow.punishgui.PunishGUI;
import me.itsshadow.punishgui.configs.InventoryConfig;
import me.itsshadow.punishgui.configs.Messages;
import me.itsshadow.punishgui.configs.Settings;
import me.itsshadow.punishgui.convos.PunishmentConversation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishInv implements Listener {

    @Getter(value = AccessLevel.PUBLIC)
    @Setter
    private static PunishInv instance;

    public Map<String, UUID> targetMap = new HashMap<>(), inConvoMap = new HashMap<>();
    public Map<String, String> keyMap = new HashMap<>();


    // Creates the inventory.
    public void createPunishInv(Player player, Player target) {

        Inventory inventory = Bukkit.createInventory(null, Settings.INV_SIZE, Utils.colorize(Settings.PUNISH_INV_NAME).replace("{player}", target.getName()));

        if (Settings.FILL_SPARE_INV_SPACES) {
            InventoryUtils.setSparePanels(inventory, Material.matchMaterial(Settings.FILL_ITEM.toUpperCase()));
        }


        for (String key : InventoryConfig.getInstance().getKeys(false)) {
            final int where = InventoryConfig.getInstance().getInt(key + ".where");
            final Material mat = Material.matchMaterial(InventoryConfig.getInstance().getString(key + ".item").toUpperCase());
            final int amount = InventoryConfig.getInstance().getInt(key + ".amount");
            final boolean glow = InventoryConfig.getInstance().getBoolean(key + ".glow");
            final String name = Utils.colorize(InventoryConfig.getInstance().getString(key + ".name"));
            final List<String> lore = InventoryConfig.getInstance().getStringList(key + ".lore");

            if (Settings.PERMISSION_ITEMS) {
                if (player.hasPermission(InventoryConfig.getInstance().getString(key + ".permission"))) {
                    InventoryUtils.createItem(inventory, where, mat, amount, glow, name, lore.stream().map(entry -> Utils.colorize(entry)).collect(Collectors.toList()));
                }
            }
        }

        if (Settings.USE_CONVOS) {
            if (inConvoMap.containsValue(player.getUniqueId())) {
                Utils.tell(player, Messages.ALREADY_IN_CONVO.replace("{cancelphrase}", Settings.STOP_CONVERSTATION_PHRASE));
                return;
            }
        }


        targetMap.put(player.getName(), target.getUniqueId());
        if (Settings.USE_CONVO_MAP)
            inConvoMap.put(player.getName(), player.getUniqueId());


        if (Settings.USE_SOUNDS) {
            if (Settings.USE_RANDOM_SOUND_PITCH) {
                player.playSound(player.getLocation(), Sound.valueOf(Settings.PUNISH_OPEN_SOUND), 1, (float) Math.random());
            } else {
                player.playSound(player.getLocation(), Sound.valueOf(Settings.PUNISH_OPEN_SOUND), 1, Settings.SOUND_PITCH);
            }
        }

        player.openInventory(inventory);
    }


    // The listener for the inventory.
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Player target;

        if (targetMap.containsKey(player.getName())) {
            target = (Player) Bukkit.getOfflinePlayer(targetMap.get(player.getName()));
        } else {
            target = null;
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        InventoryType.SlotType slotType = event.getSlotType();

        if (slotType == InventoryType.SlotType.OUTSIDE) {
            return;
        }

        if (event.getInventory().getName().contains(Utils.colorize(Settings.PUNISH_INV_NAME.replace("{player}", "")))) {
            if (clicked.hasItemMeta()) {
                event.setCancelled(true);
                for (String key : InventoryConfig.getInstance().getKeys(false)) {
                    if (Settings.DISALLOW_SHIFTCLICKING) {
                        if (event.isShiftClick() || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (event.getRawSlot() == InventoryConfig.getInstance().getInt(key + ".where") - 1) {

                        if (InventoryConfig.getInstance().getBoolean(key + ".close-on-click")) {
                            player.closeInventory();
                        }

                        if (Settings.USE_CONVOS) {
                            keyMap.put(player.getName(), key);
                            new PunishmentConversation(player);
                            return;
                        }

                        List<String> commands = InventoryConfig.getInstance().getStringList(key + ".commands");


                        if (Settings.CONSOLE) {
                            ConsoleCommandSender console = PunishGUI.getInstance().getServer().getConsoleSender();

                            commands.forEach(command -> Bukkit.getServer().dispatchCommand(console, command.replace("{sender}", player.getName()).replace("{player}", target.getName())));
                            Utils.tell(player, Messages.PUNISHMENT_SUCCESSFUL.replace("{player}", target.getName()));
                            return;
                        }

                        commands.forEach(command -> player.performCommand(command.replace("{sender}", player.getName()).replace("{player}", target.getName())));
                        Utils.tell(player, Messages.PUNISHMENT_SUCCESSFUL.replace("{player}", target.getName()));
                        return;
                    }
                }
            }
        }

        return;

    }


}
