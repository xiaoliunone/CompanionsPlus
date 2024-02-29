package cn.handyplus.companions.hook;

import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.UUID;


public class PlayerPointsUtil {

    private PlayerPointsUtil() {
    }

    
    public static boolean buy(Player player, int price) {

        if (HookUtil.PLAYER_POINTS == null) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("playerPointsFailureMsg"));
            return false;
        }

        return HookUtil.PLAYER_POINTS.getAPI().take(player.getUniqueId(), price);
    }

    
    public static int getPlayerPoints(Player player) {
        if (HookUtil.PLAYER_POINTS == null || player == null) {
            return 0;
        }
        return HookUtil.PLAYER_POINTS.getAPI().look(player.getUniqueId());
    }

    
    public static boolean buy(UUID playerUid, int price) {

        if (HookUtil.PLAYER_POINTS == null) {
            return false;
        }

        return HookUtil.PLAYER_POINTS.getAPI().take(playerUid, price);
    }

}
