package cn.handyplus.companions.listener;

import cn.handyplus.companions.CompanionsPlus;
import cn.handyplus.companions.command.admin.GiveCommand;
import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.core.ParticleUtil;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.companions.util.ArmorStandUtil;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@HandyListener
public class CompanionListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        CompanionsConstants.COMPANION_PACKET.followHandyCompanion(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerArmorStandManipulateEvent event) {
        if (event.isCancelled()) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.getRightClicked().equals(PlayerData.instanceOf(player).getMysteryCompanion())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVanish(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String vanishPerms = ConfigUtil.CONFIG.getString("settings.vanish.perms", "");
        if (!player.hasPermission(vanishPerms)) {
            return;
        }
        String commandListStr = ConfigUtil.CONFIG.getString("settings.vanish.commands", "");
        List<String> commands = StrUtil.strToStrList(commandListStr, ",");
        if (!commands.contains(event.getMessage())) {
            return;
        }
        if (!CacheUtil.isCache(player.getUniqueId())) {
            return;
        }
        CompanionUtil.removeCompanion(player);
        CompanionUtil.delActiveCompanion(player);
        MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("playerInVanish"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (!Action.RIGHT_CLICK_BLOCK.equals(event.getAction()) || event.getClickedBlock() == null) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemInMainHand = ItemStackUtil.getItemInMainHand(player.getInventory());
        ItemStack companionToken = CompanionUtil.getToken();

        if (itemInMainHand.getAmount() > 1) {
            return;
        }

        if (!ItemStackUtil.isSimilar(itemInMainHand, companionToken)) {
            return;
        }
        event.setCancelled(true);

        ItemStackUtil.removeItem(player.getInventory(), companionToken, 1);

        List<String> companionNames = new ArrayList<>(CompanionsConstants.COMPANION_DETAILS_MAP.keySet());
        String companionName = companionNames.get(new Random().nextInt(companionNames.size()));

        GiveCommand.give(player, companionName, player.getName(), false);

        summonMysteryCompanion(player, companionName, event.getClickedBlock().getLocation());

    }

    
    private static void summonMysteryCompanion(Player player, String companionName, Location loc) {
        if (PlayerData.instanceOf(player).getMysteryCompanion() != null) {
            return;
        }

        ArmorStand armorStand = ArmorStandUtil.createArmorStand(player, loc.add(0, 1, 0), companionName);
        PlayerData.instanceOf(player).setMysteryCompanion(armorStand);

        ParticleUtil.spawnParticle(player, loc);

        String soundOnUse = ConfigUtil.CONFIG.getString("items.companionToken.soundOnUse", "");
        Optional<Sound> soundOpt = BaseUtil.getSound(soundOnUse);
        if (soundOpt.isPresent()) {
            PlayerSchedulerUtil.playSound(player, soundOpt.get(), 1.0F, 1.0F);
        } else {
            MessageUtil.sendConsoleMessage(BaseUtil.getMsgNotColor("notSoundMsg", MapUtil.of("${sound}", soundOnUse)));
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(CompanionsPlus.INSTANCE, () -> {
            PlayerData.instanceOf(player).getMysteryCompanion().remove();
            PlayerData.instanceOf(player).setMysteryCompanion(null);
        }, 60L);
    }

}