package cn.handyplus.companions.hook;

import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class VaultUtil {

    private VaultUtil() {
    }

    
    public static boolean buy(Player player, long price) {

        if (HookUtil.ECON == null) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor(("vaultFailureMsg")));
            return false;
        }

        if (!HookUtil.ECON.has(player, price)) {
            return false;
        }
        HookUtil.ECON.withdrawPlayer(player, price);
        return true;
    }

    
    public static double getPlayerVault(Player player) {
        if (HookUtil.ECON == null || player == null) {
            return 0.0;
        }
        return HookUtil.ECON.getBalance(player);
    }

    
    public static boolean buy(OfflinePlayer offlinePlayer, long price) {

        if (HookUtil.ECON == null) {
            return false;
        }

        if (!HookUtil.ECON.has(offlinePlayer, price)) {
            return false;
        }
        HookUtil.ECON.withdrawPlayer(offlinePlayer, price);
        return true;
    }

}