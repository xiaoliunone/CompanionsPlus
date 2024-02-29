package cn.handyplus.companions.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;


public class PlaceholderApiUtil {

    private PlaceholderApiUtil() {
    }

    
    public static String set(Player player, String str) {
        if (!HookUtil.USE_PAPI || player == null) {
            return str;
        }

        if (PlaceholderAPI.containsPlaceholders(str)) {
            return PlaceholderAPI.setPlaceholders(player, str);
        }
        return str;
    }

    
    public static String set(OfflinePlayer offlinePlayer, String str) {
        if (!HookUtil.USE_PAPI || offlinePlayer == null) {
            return str;
        }

        if (PlaceholderAPI.containsPlaceholders(str)) {
            return PlaceholderAPI.setPlaceholders(offlinePlayer, str);
        }
        return str;
    }

    
    public static List<String> set(OfflinePlayer offlinePlayer, List<String> strList) {
        if (!HookUtil.USE_PAPI || offlinePlayer == null) {
            return strList;
        }
        return PlaceholderAPI.setPlaceholders(offlinePlayer, strList);
    }

    
    public static String set(UUID playerUuid, String str) {
        if (!HookUtil.USE_PAPI || playerUuid == null) {
            return str;
        }

        if (PlaceholderAPI.containsPlaceholders(str)) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
            return PlaceholderAPI.setPlaceholders(offlinePlayer, str);
        }
        return str;
    }

    
    public static List<String> set(Player player, List<String> strList) {
        if (!HookUtil.USE_PAPI || player == null) {
            return strList;
        }
        return PlaceholderAPI.setPlaceholders(player, strList);
    }

}