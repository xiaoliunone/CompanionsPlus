package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ForceActiveCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "forceActive";
    }

    @Override
    public String permission() {
        return "companionsPlus.forceActive";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String s, String[] args) {

        AssertUtil.notTrue(args.length < 3, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));

        String playerName = args[1];
        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerName);
        AssertUtil.isTrue(onlinePlayer.isPresent(), sender, StrUtil.replace(BaseUtil.getMsgNotColor("playerNotOnline"), "player", playerName));
        Player target = onlinePlayer.get();
        String companionName = args[2];


        List<CompanionsOwnedEnter> companionsOwnedList = CompanionsOwnedService.getInstance().findByPlayer(target.getUniqueId());
        List<String> nameList = companionsOwnedList.stream().map(CompanionsOwnedEnter::getCompanion).collect(Collectors.toList());
        if (!nameList.contains(companionName)) {
            MessageUtil.sendMessage(sender, StrUtil.replace(BaseUtil.getMsgNotColor("companionNotOwned"), "player", playerName));
            return;
        }
        CompanionUtil.removeCompanion(target);
        CompanionUtil.addActiveCompanion(target, companionName);
        CompanionsConstants.COMPANION_PACKET.loadHandyCompanion(target);
        MessageUtil.sendMessage(sender, StrUtil.replace(BaseUtil.getMsgNotColor("forceActiveSuccess"), "player", playerName));
        MessageUtil.sendMessage(target, StrUtil.replace(BaseUtil.getMsgNotColor("companionSetForPlayer"), "companion", companionName));
    }

}