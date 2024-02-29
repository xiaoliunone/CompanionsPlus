package cn.handyplus.companions.util;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.enter.CompanionsActiveEnter;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.service.CompanionsActiveService;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class CacheUtil {

    
    private final static Map<UUID, CompanionsOwnedEnter> PLAYER_ACTIVE_CACHE_MAP = new HashMap<>();

    
    public static void db2ActiveCache(UUID playerUuid) {
        removeCache(playerUuid);
        Optional<CompanionsActiveEnter> activeOptional = CompanionsActiveService.getInstance().findByUid(playerUuid);
        if (activeOptional.isPresent()) {
            Optional<CompanionsOwnedEnter> companionsOwned = CompanionsOwnedService.getInstance().findByPlayerAndCompanion(playerUuid, activeOptional.get().getCompanion());
            if (companionsOwned.isPresent()) {
                CompanionsOwnedEnter enter = companionsOwned.get();
                enter.setAbilityList(new ArrayList<>());

                enter.setCustomName(BaseUtil.replaceChatColor(enter.getCustomName()));
                CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(enter.getCompanion());
                if (!CompanionsConstants.NONE.equalsIgnoreCase(companionDetails.getAbility())) {
                    enter.setAbilityList(StrUtil.strToStrList(companionDetails.getAbility(), ";"));
                }
                PLAYER_ACTIVE_CACHE_MAP.put(playerUuid, enter);
            } else {
                CompanionsActiveService.getInstance().remove(playerUuid);
            }
        }
    }

    public static void removeCache(UUID playerUuid) {
        PLAYER_ACTIVE_CACHE_MAP.remove(playerUuid);
    }

    public static boolean isCache(UUID playerUuid) {
        return getCache(playerUuid).isPresent();
    }

    public static String getActiveCompanionName(UUID playerUuid) {
        return getCache(playerUuid).map(CompanionsOwnedEnter::getCompanion).orElse(null);
    }

    public static Integer getActiveId(UUID playerUuid) {
        return getCache(playerUuid).map(CompanionsOwnedEnter::getId).orElse(null);
    }

    public static Optional<CompanionsOwnedEnter> getCache(UUID playerUuid) {
        return Optional.ofNullable(PLAYER_ACTIVE_CACHE_MAP.get(playerUuid));
    }

}
