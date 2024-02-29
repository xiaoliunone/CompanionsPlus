package cn.handyplus.companions.core;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.PotionEffectEnum;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PotionEffectUtil {

    
    public static void give(Player player) {
        Optional<CompanionsOwnedEnter> companionsOwnedOpt = CacheUtil.getCache(player.getUniqueId());
        if (!companionsOwnedOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = companionsOwnedOpt.get();
        List<PotionEffect> potionEffectList = new ArrayList<>();
        for (String ability : activeCompanion.getAbilityList()) {
            if (CompanionsConstants.NONE.equals(ability)) {
                continue;
            }
            if (!PotionEffectEnum.getAll().contains(ability)) {
                continue;
            }
            PotionEffectType potionEffectType = PotionEffectType.getByName(ability);
            if (potionEffectType == null) {
                continue;
            }
            PotionEffect potionEffect = new PotionEffect(potionEffectType, 1728000, activeCompanion.getAbilityLevel() - 1);
            potionEffectList.add(potionEffect);
        }
        if (CollUtil.isNotEmpty(potionEffectList)) {
            PlayerSchedulerUtil.addPotionEffects(player, potionEffectList);
        }
    }

    
    public static void remove(Player player) {
        Optional<CompanionsOwnedEnter> companionsOwnedOpt = CacheUtil.getCache(player.getUniqueId());
        if (!companionsOwnedOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = companionsOwnedOpt.get();
        for (String ability : activeCompanion.getAbilityList()) {
            if (CompanionsConstants.NONE.equals(ability)) {
                continue;
            }
            if (!PotionEffectEnum.getAll().contains(ability)) {
                continue;
            }
            PotionEffectType potionEffectType = PotionEffectType.getByName(ability);
            if (potionEffectType == null) {
                continue;
            }
            if (player.hasPotionEffect(potionEffectType)) {
                PlayerSchedulerUtil.removePotionEffect(player, potionEffectType);
            }
        }

        PlayerData.instanceOf(player).setMiningVision(false);
    }

}
