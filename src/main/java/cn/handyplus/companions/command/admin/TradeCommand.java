package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TradeCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "trade";
    }

    @Override
    public String permission() {
        return "companionsPlus.trade";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        AssertUtil.notTrue(args.length < 3, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));

        String playerName = args[1];
        String companionName = args[2];


        if (!CompanionsConstants.COMPANION_DETAILS_MAP.containsKey(companionName)) {
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("companionNotFound"));
            return;
        }
        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerName);
        AssertUtil.isTrue(onlinePlayer.isPresent(), sender, BaseUtil.getMsgNotColor("playerNotOnline"));
        Player target = onlinePlayer.get();

        List<CompanionsOwnedEnter> companionsOwnedList = CompanionsOwnedService.getInstance().findByPlayer(target.getUniqueId());
        List<String> nameList = companionsOwnedList.stream().map(CompanionsOwnedEnter::getCompanion).collect(Collectors.toList());
        if (nameList.contains(companionName)) {
            MessageUtil.sendMessage(sender, StrUtil.replace(BaseUtil.getMsgNotColor("tradeAlreadyOwn"), "companion", companionName));
            return;
        }

        List<CompanionsOwnedEnter> playerCompanionsOwnedList = CompanionsOwnedService.getInstance().findByPlayer(player.getUniqueId());
        List<String> playerNameList = playerCompanionsOwnedList.stream().map(CompanionsOwnedEnter::getCompanion).collect(Collectors.toList());

        if (playerNameList.contains(companionName)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cp remove " + player.getName() + " " + companionName);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cp give " + target.getName() + " " + companionName);
            MessageUtil.sendMessage(sender, StrUtil.replace(BaseUtil.getMsgNotColor("tradeSuccessful"), "player", target.getName()));
            return;
        }
        MessageUtil.sendMessage(sender, StrUtil.replace(BaseUtil.getMsgNotColor("tradeUnSuccessful"), "companion", companionName));
    }

}