package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.service.CompanionsActiveService;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class RemoveCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public String permission() {
        return "companionsPlus.remove";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String s, String[] args) {
        AssertUtil.notTrue(args.length < 3, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));

        String playerName = args[1];
        String companionName = args[2];


        if (!CompanionsConstants.COMPANION_DETAILS_MAP.containsKey(companionName)) {
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("companionNotFound"));
            return;
        }
        OfflinePlayer offlinePlayer = BaseUtil.getOfflinePlayer(playerName);

        Optional<CompanionsOwnedEnter> ownedEnterOptional = CompanionsOwnedService.getInstance().findByPlayerAndCompanion(offlinePlayer.getUniqueId(), companionName);
        if (!ownedEnterOptional.isPresent()) {
            MessageUtil.sendMessage(sender, StrUtil.replace(BaseUtil.getMsgNotColor("companionNotOwned"), "player", playerName));
            return;
        }

        CompanionsOwnedService.getInstance().remove(offlinePlayer.getUniqueId(), companionName);
        CompanionsActiveService.getInstance().remove(offlinePlayer.getUniqueId());


        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerName);
        if (onlinePlayer.isPresent()) {
            Player target = onlinePlayer.get();
            if (companionName.equalsIgnoreCase(CacheUtil.getActiveCompanionName(offlinePlayer.getUniqueId()))) {

                CompanionUtil.removeCompanion(target);

                CacheUtil.removeCache(target.getUniqueId());

                PlayerData.instanceOf(target).remove();
            }
            MessageUtil.sendMessage(sender, StrUtil.replace(BaseUtil.getMsgNotColor("companionRemovedFromPlayer"), "companion", companionName));
        }

        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("companionRemoved"));
    }

}