package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class GiveItemCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "giveItem";
    }

    @Override
    public String permission() {
        return "companionsPlus.giveItem";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String s, String[] args) {
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));

        String playerName = args[1];
        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerName);
        AssertUtil.isTrue(onlinePlayer.isPresent(), sender, BaseUtil.getMsgNotColor("playerNotOnline"));
        Player player = onlinePlayer.get();
        Integer amount = AssertUtil.isNumericToInt(args[2], sender, BaseUtil.getMsgNotColor("amountFailureMsg"));

        ItemStack companionToken = CompanionUtil.getToken();
        ItemStackUtil.addItem(player, companionToken, amount);

        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("itemGiven"));
        MessageUtil.sendMessage(playerName, StrUtil.replace(BaseUtil.getMsgNotColor("itemReceived"), "item", BaseUtil.getDisplayName(companionToken)));
    }

}