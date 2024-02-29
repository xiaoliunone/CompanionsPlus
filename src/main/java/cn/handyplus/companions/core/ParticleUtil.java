package cn.handyplus.companions.core;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.HandyRunnable;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;


public class ParticleUtil {

    
    public static void giveParticle(Player player) {
        if (!PlayerData.instanceOf(player).isParticle()) {
            return;
        }
        HandyRunnable handyRunnable = new HandyRunnable() {
            @Override
            public void run() {
                double x = Math.cos(Math.toRadians(player.getLocation().getYaw() - 180));
                double z = Math.sin(Math.toRadians(player.getLocation().getYaw() - 180));
                String activeCompanionName = CacheUtil.getActiveCompanionName(player.getUniqueId());
                if (StrUtil.isEmpty(activeCompanionName)) {
                    this.cancel();
                    return;
                }
                CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(activeCompanionName);
                if (companionDetails == null) {
                    this.cancel();
                    return;
                }
                if (BaseConstants.VERSION_ID > VersionCheckEnum.V_1_8.getVersionId()) {
                    for (int i = 0; i < 40; i += 5) {
                        Location newLoc = player.getLocation().add(x, companionDetails.getY() + 0.4, z);
                        newLoc.setZ(newLoc.getZ() + Math.cos(i) * 0.5);
                        newLoc.setX(newLoc.getX() + Math.sin(i) * 0.5);
                        player.getWorld().spawnParticle(Particle.DRIP_LAVA, newLoc, 1, 0, 0, 0, 10);
                    }
                } else {
                    for (int i = 0; i < 40; i += 5) {
                        Location newLoc = player.getLocation().add(x, companionDetails.getY() + 0.4, z);
                        newLoc.setZ(newLoc.getZ() + Math.cos(i) * 0.5);
                        newLoc.setX(newLoc.getX() + Math.sin(i) * 0.5);
                        player.getWorld().playEffect(newLoc, Effect.valueOf("LAVADRIP"), 1);
                    }
                }
            }
        };
        PlayerData.instanceOf(player).setParticleTask(handyRunnable);
        HandySchedulerUtil.runTaskTimerAsynchronously(handyRunnable, 60L, 60L);
    }

    
    public static void spawnParticle(Player player, Location loc) {
        String particleAnimation = ConfigUtil.CONFIG.getString("items.companionToken.particleAnimation");
        if (BaseConstants.VERSION_ID > VersionCheckEnum.V_1_8.getVersionId()) {
            for (int i = 0; i < 5; i += 1) {
                player.getWorld().spawnParticle(Particle.valueOf(particleAnimation), loc.add(0, 1, 0), 30, 0, 0, 0, 1);
            }
        } else {
            for (int i = 0; i < 30; i += 1) {
                player.getWorld().playEffect(loc.add(0, 1, 0), Effect.valueOf(particleAnimation), 51);
            }
        }
    }

    public static void removeParticles(Player player) {
        if (PlayerData.instanceOf(player).getParticleTask() != null) {
            PlayerData.instanceOf(player).getParticleTask().cancel();
            PlayerData.instanceOf(player).setParticleTask(null);
        }
    }

}
