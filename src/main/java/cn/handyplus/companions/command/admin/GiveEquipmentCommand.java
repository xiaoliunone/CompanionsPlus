package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.constants.CompanionsConstants;
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

public class GiveEquipmentCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "giveEquipment";
    }

    @Override
    public String permission() {
        return "companionsPlus.giveEquipment";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String s, String[] args) {
        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        String equipmentKey = args[1];
        AssertUtil.isTrue(CompanionsConstants.COMPANION_EQUIPMENT_MAP.containsKey(equipmentKey), sender, BaseUtil.getMsgNotColor("noEquipmentKey"));
        String playerName = args[2];
        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerName);
        AssertUtil.isTrue(onlinePlayer.isPresent(), sender, BaseUtil.getMsgNotColor("playerNotOnline"));
        Player player = onlinePlayer.get();
        Integer amount = 1;
        if (args.length > 3) {
            amount = AssertUtil.isNumericToInt(args[3], sender, BaseUtil.getMsgNotColor("amountFailureMsg"));
        }
        ItemStack itemStack = CompanionsConstants.COMPANION_EQUIPMENT_MAP.get(equipmentKey).getItem();
        ItemStackUtil.addItem(player, itemStack, amount);
        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("itemGiven"));
        MessageUtil.sendMessage(playerName, StrUtil.replace(BaseUtil.getMsgNotColor("itemReceived"), "item", BaseUtil.getDisplayName(itemStack)));
    }

}