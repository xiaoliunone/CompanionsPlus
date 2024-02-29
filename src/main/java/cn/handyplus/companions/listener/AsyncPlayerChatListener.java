package cn.handyplus.companions.listener;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.PriceTypeEnum;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.List;


@HandyListener
public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        if (PlayerData.instanceOf(player).getChatNameById() != null) {
            event.setCancelled(this.changeName(player, event.getMessage()));
        }
    }

    private boolean changeName(Player player, String customName) {

        if ("T".equalsIgnoreCase(customName) || "cancel".equalsIgnoreCase(customName)) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("actionSuccess"));
            PlayerData.instanceOf(player).setChatNameById(null);
            return true;
        }

        List<String> blacklistedNameList = ConfigUtil.CONFIG.getStringList("settings.blacklistedNames");
        if (!player.hasPermission(CompanionsConstants.BLACK_LIST_PERMISSION)) {
            if (blacklistedNameList.contains(customName)) {
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("blacklistedName"));
                return true;
            }
        }

        int maxNameLength = ConfigUtil.CONFIG.getInt("settings.maxNameLength");
        if (customName.length() > maxNameLength) {
            MessageUtil.sendMessage(player, StrUtil.replace(BaseUtil.getMsgNotColor("maxNameLength"), "length", String.valueOf(maxNameLength)));
            return true;
        }


        String rawPrice = ConfigUtil.UPGRADE_CONFIG.getString("name.price");
        if (CompanionUtil.takeMoney(player, rawPrice)) {
            PlayerData.instanceOf(player).setChatNameById(null);
            return true;
        }


        CompanionUtil.changeName(player, customName, PlayerData.instanceOf(player).getChatNameById());

        PriceTypeEnum.PriceType priceType = PriceTypeEnum.getPrice(rawPrice);
        HashMap<String, String> map = MapUtil.of("${price}", priceType.getFormatPrice(), "${name}", customName);
        MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("renamedCompanion", map));

        PlayerData.instanceOf(player).setChatNameById(null);
        return true;
    }

}
