package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.listener.gui.UpgradeClickEvent;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ForceUpgradeCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "forceUpgrade";
    }

    @Override
    public String permission() {
        return "companionsPlus.forceUpgrade";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String s, String[] args) {

        AssertUtil.notTrue(args.length < 3, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));

        String playerName = args[1];
        String type = args[2];
        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerName);
        AssertUtil.isTrue(onlinePlayer.isPresent(), sender, StrUtil.replace(BaseUtil.getMsgNotColor("playerNotOnline"), "player", playerName));
        Player player = onlinePlayer.get();
        Optional<CompanionsOwnedEnter> companionsOwnedOpt = CacheUtil.getCache(player.getUniqueId());
        if (!companionsOwnedOpt.isPresent()) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noActiveCompanion"));
            return;
        }
        CompanionsOwnedEnter companionsOwned = companionsOwnedOpt.get();

        if ("ability".equalsIgnoreCase(type)) {
            boolean upgrade = true;
            if (args.length > 3) {
                upgrade = "true".equalsIgnoreCase(args[3]);
            }
            CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionsOwned.getCompanion());
            if (!upgrade) {
                if (companionsOwned.getAbilityLevel() != 1) {
                    UpgradeClickEvent.upgradeAbility(companionsOwned, player, false, false);
                } else {
                    MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("abilityDowngradedMaxed"));
                    return;
                }
            } else if (!companionDetails.getMaxAbilityLevel().equals(companionsOwned.getAbilityLevel())) {
                UpgradeClickEvent.upgradeAbility(companionsOwned, player, false, true);
                MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("forceUpgradeSuccessful"));
            } else {
                MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("abilityMaxed"));
            }
            return;
        }

        if ("reName".equalsIgnoreCase(type)) {
            AssertUtil.notTrue(args.length < 4, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
            String newName = args[3];
            CompanionUtil.changeName(player, newName, companionsOwned);
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("companionReNameForPlayer", MapUtil.of("${name}", newName)));
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("succeedMsg"));
            return;
        }

        if ("hideName".equalsIgnoreCase(type)) {
            CompanionUtil.changeNameVisible(player, companionsOwned);
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("companionHideNameForPlayer"));
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("succeedMsg"));
            return;
        }

        if ("changeWeapon".equalsIgnoreCase(type)) {
            AssertUtil.notTrue(args.length < 4, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
            String weapon = args[3];
            CompanionUtil.changeWeapon(player, weapon, companionsOwned);
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("companionWeaponForPlayer", MapUtil.of("${name}", weapon)));
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("succeedMsg"));
        }

    }

}
