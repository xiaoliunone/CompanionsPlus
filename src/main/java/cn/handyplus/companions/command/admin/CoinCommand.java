package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.service.CompanionsCoinService;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CoinCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "coin";
    }

    @Override
    public String permission() {
        return "companionsPlus.coin";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String s, String[] args) {

        AssertUtil.notTrue(args.length < 4, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));

        Long amount = AssertUtil.isNumericToLong(args[3], sender, BaseUtil.getMsgNotColor("amountFailureMsg"));
        boolean rst;
        OfflinePlayer offlinePlayer = BaseUtil.getOfflinePlayer(args[2]);

        CompanionsCoinService.getInstance().init(offlinePlayer);
        switch (args[1].toLowerCase()) {
            case "give":
                rst = CompanionsCoinService.getInstance().give(offlinePlayer.getUniqueId(), amount);
                break;
            case "set":
                rst = CompanionsCoinService.getInstance().set(offlinePlayer.getUniqueId(), amount);
                break;
            case "take":
                rst = CompanionsCoinService.getInstance().take(offlinePlayer.getUniqueId(), amount);
                break;
            default:
                MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("typeFailureMsg"));
                return;
        }
        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor(rst ? "succeedMsg" : "failureMsg"));
    }

}