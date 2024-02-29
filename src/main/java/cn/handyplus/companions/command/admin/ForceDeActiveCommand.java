package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ForceDeActiveCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "forceDeActive";
    }

    @Override
    public String permission() {
        return "companionsPlus.forceDeActive";
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
        AssertUtil.isTrue(onlinePlayer.isPresent(), sender, StrUtil.replace(BaseUtil.getMsgNotColor("playerNotOnline"), "player", playerName));
        Player target = onlinePlayer.get();

        if (!CacheUtil.isCache(target.getUniqueId())) {
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("forceUpgradeNotSuccessful"));
            return;
        }

        CompanionUtil.removeCompanion(target);

        CompanionUtil.delActiveCompanion(target);
        MessageUtil.sendMessage(target, BaseUtil.getMsgNotColor("removeCompanion"));
        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("companionRemoved"));
    }

}
