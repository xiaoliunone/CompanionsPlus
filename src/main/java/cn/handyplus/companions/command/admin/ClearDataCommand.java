package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.companions.service.CompanionsActiveService;
import cn.handyplus.companions.service.CompanionsCoinService;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ClearDataCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "clearData";
    }

    @Override
    public String permission() {
        return "companionsPlus.clearData";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String s, String[] args) {

        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));

        String playerName = args[1];
        if (CompanionsConstants.ALL.equalsIgnoreCase(playerName)) {
            CompanionsOwnedService.getInstance().remove();
            CompanionsActiveService.getInstance().remove();
            CompanionsCoinService.getInstance().remove();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                CompanionUtil.removeCompanion(onlinePlayer);

                CacheUtil.removeCache(onlinePlayer.getUniqueId());

                PlayerData.instanceOf(onlinePlayer).remove();
            }
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("clearDataAllMsh"));
            return;
        }
        OfflinePlayer offlinePlayer = BaseUtil.getOfflinePlayer(playerName);
        CompanionsOwnedService.getInstance().remove(offlinePlayer.getUniqueId());
        CompanionsActiveService.getInstance().remove(offlinePlayer.getUniqueId());
        CompanionsCoinService.getInstance().remove(offlinePlayer.getUniqueId());

        Optional<Player> onlinePlayer = BaseUtil.getOnlinePlayer(playerName);
        if (onlinePlayer.isPresent()) {
            Player player = onlinePlayer.get();

            CompanionUtil.removeCompanion(player);

            CacheUtil.removeCache(player.getUniqueId());

            PlayerData.instanceOf(player).remove();
        }
        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("clearDataPlayerMsh", MapUtil.of("${player}", playerName)));
    }

}