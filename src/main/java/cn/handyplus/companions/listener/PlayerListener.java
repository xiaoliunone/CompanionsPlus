package cn.handyplus.companions.listener;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.util.HandyHttpUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


@HandyListener
public class PlayerListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        CompanionUtil.removeCompanion(event.getEntity());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        CompanionUtil.delayCompanionSpawn(event.getPlayer());
        PlayerData.instanceOf(event.getPlayer()).setRespawned(true);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();

        if (PlayerData.instanceOf(player).isRespawned()) {
            PlayerData.instanceOf(player).setRespawned(false);
            return;
        }
        CompanionUtil.delayToggleCompanion(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        HandySchedulerUtil.runTaskAsynchronously(() -> {

            CacheUtil.db2ActiveCache(event.getPlayer().getUniqueId());

            CompanionUtil.delayCompanionSpawn(event.getPlayer());
        });
    }

    @EventHandler
    public void onOpPlayerJoin(PlayerJoinEvent event) {

        if (!ConfigUtil.CONFIG.getBoolean(BaseConstants.IS_CHECK_UPDATE_TO_OP_MSG)) {
            return;
        }
        HandyHttpUtil.checkVersion(event.getPlayer(), CompanionsConstants.PLUGIN_VERSION_URL);
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {

        clearCache(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {

        clearCache(event.getPlayer());
    }

    private void clearCache(Player player) {

        CompanionUtil.removeCompanion(player);

        CacheUtil.removeCache(player.getUniqueId());

        PlayerData.instanceOf(player).remove();
    }

}
