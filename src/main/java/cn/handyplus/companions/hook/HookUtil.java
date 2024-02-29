package cn.handyplus.companions.hook;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Optional;


public class HookUtil {
    
    protected static Economy ECON;
    
    protected static boolean USE_PAPI;

    protected static PlayerPoints PLAYER_POINTS;

    public static void init() {

        loadEconomy();
        loadPlaceholder();
        loadPlayerPoints();
    }

    
    public static void loadEconomy() {
        Optional<Plugin> vaultOpt = BaseUtil.hook(BaseConstants.VAULT);
        if (!vaultOpt.isPresent()) {
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("vaultFailureMsg"));
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("vaultFailureMsg"));
            return;
        }
        ECON = rsp.getProvider();
        MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("vaultSucceedMsg"));
    }

    
    public static void loadPlaceholder() {
        Optional<Plugin> placeholderApiOpt = BaseUtil.hook(BaseConstants.PLACEHOLDER_API);
        if (placeholderApiOpt.isPresent()) {
            new PlaceholderHook().register();
            USE_PAPI = true;
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("placeholderAPISucceedMsg"));
            return;
        }
        USE_PAPI = false;
        MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("placeholderAPIFailureMsg"));
    }

    
    private static void loadPlayerPoints() {
        Optional<Plugin> playerPointsOpt = BaseUtil.hook(BaseConstants.PLAYER_POINTS);
        if (playerPointsOpt.isPresent()) {
            final Plugin plugin = Bukkit.getPluginManager().getPlugin(BaseConstants.PLAYER_POINTS);
            PLAYER_POINTS = (PlayerPoints) plugin;
            MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("playerPointsSucceedMsg"));
            return;
        }
        MessageUtil.sendConsoleMessage(BaseUtil.getLangMsg("playerPointsFailureMsg"));
    }

}
