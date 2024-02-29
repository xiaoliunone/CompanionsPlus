package cn.handyplus.companions.core;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.event.CompanionBuffEvent;
import cn.handyplus.companions.hook.PlaceholderApiUtil;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.attribute.AttributeEnum;
import cn.handyplus.lib.attribute.AttributeUtil;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.FormulaUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class BuffManageUtil {

    private BuffManageUtil() {
    }

    
    private static void addBuff(Player player, List<String> buffList) {

        if (CollUtil.isEmpty(buffList)) {
            return;
        }

        String buffType = ConfigUtil.CONFIG.getString("buffType");
        AttributeEnum attributeEnum = AttributeEnum.getEnum(buffType);
        if (attributeEnum == null) {
            return;
        }
        AttributeUtil.getInstance().addAttribute(player, buffList, attributeEnum);
    }

    
    public static void removeBuff(Player player) {

        if (player == null) {
            return;
        }

        String buffType = ConfigUtil.CONFIG.getString("buffType");
        AttributeEnum attributeEnum = AttributeEnum.getEnum(buffType);
        if (attributeEnum == null) {
            return;
        }
        AttributeUtil.getInstance().removeAttribute(player, attributeEnum);
    }

    
    public static void setBuff(Player player, List<String> buffList) {

        removeBuff(player);

        addBuff(player, buffList);
    }

    
    public static void callBuffEvent(Player player) {
        if (player == null) {
            return;
        }
        Optional<CompanionsOwnedEnter> companionsOwnedOptional = CacheUtil.getCache(player.getUniqueId());
        if (!companionsOwnedOptional.isPresent()) {
            return;
        }
        CompanionsOwnedEnter companionsOwned = companionsOwnedOptional.get();
        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionsOwned.getCompanion());
        List<String> attributeList = companionDetails.getAttributeList();
        if (CollUtil.isEmpty(attributeList)) {
            return;
        }

        Map<String, String> map = MapUtil.of("level", companionsOwned.getAbilityLevel().toString());
        List<String> buffList = new ArrayList<>();
        for (String attribute : attributeList) {
            String formulaBuff = FormulaUtil.evaluateFormula(PlaceholderApiUtil.set(player, attribute), map);
            buffList.add(formulaBuff);
        }

        if (CompanionsConstants.COMPANION_EQUIPMENT_MAP.containsKey(companionsOwned.getCustomWeapon())) {
            CompanionEquipment companionEquipment = CompanionsConstants.COMPANION_EQUIPMENT_MAP.get(companionsOwned.getCustomWeapon());
            List<String> loreList = companionEquipment.getAttributeList();
            if (CollUtil.isNotEmpty(loreList)) {
                buffList.addAll(loreList);
            }
        }

        HandySchedulerUtil.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(new CompanionBuffEvent(player, buffList)));
    }

}