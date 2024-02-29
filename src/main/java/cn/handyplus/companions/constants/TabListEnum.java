package cn.handyplus.companions.constants;

import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.db.enums.DbTypeEnum;
import cn.handyplus.lib.util.BaseUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Getter
@AllArgsConstructor
public enum TabListEnum {
    
    FIRST(Arrays.asList("clearData", "coin", "convert", "forceActive", "forceDeActive", "forceUpgrade", "give", "giveItem"
            , "reload", "remove", "trade", "open", "shop", "particle", "getIp", "giveEquipment"), 0, null, 1),

    CLEAR_DATA_TWO(null, 1, "clearData", 2),

    COIN_TWO(Arrays.asList("give", "set", "take"), 1, "coin", 2),
    COIN_THREE(null, 1, "coin", 3),
    COIN_FOUR(Collections.singletonList(BaseUtil.getLangMsg("tabHelp.number")), 1, "coin", 4),

    CONVERT_ONE(DbTypeEnum.getEnum(), 1, "convert", 2),

    FORCE_ACTIVE_TWO(null, 1, "forceActive", 2),
    FORCE_ACTIVE_THREE(Collections.singletonList("玩家宠物名"), 1, "forceActive", 3),

    FORCE_DE_ACTIVE_TWO(null, 1, "forceDeActive", 2),

    FORCE_UPGRADE_TWO(null, 1, "forceUpgrade", 2),
    FORCE_UPGRADE_THREE(Arrays.asList("ability", "reName", "hideName", "changeWeapon"), 1, "forceUpgrade", 3),

    FORCE_UPGRADE_FOUR(Collections.singletonList("武器名"), 3, "changeWeapon", 4),
    FORCE_UPGRADE_RE_NAME_FOUR(Collections.singletonList(BaseUtil.getLangMsg("tabHelp.name")), 3, "reName", 4),

    GIVE_TWO(null, 1, "give", 2),
    GIVE_THREE(Collections.singletonList("宠物名"), 1, "give", 3),

    GIVE_ITEM_TWO(null, 1, "giveItem", 2),
    GIVE_ITEM_THREE(Collections.singletonList(BaseUtil.getLangMsg("tabHelp.number")), 1, "giveItem", 3),

    REMOVE_TWO(null, 1, "remove", 2),
    REMOVE_THREE(Collections.singletonList("玩家宠物名"), 1, "remove", 3),

    TRADE_TWO(null, 1, "trade", 2),
    TRADE_THREE(Collections.singletonList("玩家宠物名"), 1, "trade", 3),

    GIVE_EQUIPMENT_TWO(Collections.singletonList("装备名"), 1, "giveEquipment", 2),
    GIVE_EQUIPMENT_THREE(null, 1, "giveEquipment", 3),
    GIVE_EQUIPMENT_FOUR(Collections.singletonList(BaseUtil.getLangMsg("tabHelp.number")), 1, "giveEquipment", 4),

    ;

    
    private final List<String> list;
    
    private final int befPos;
    
    private final String bef;
    
    private final int num;

    
    public static List<String> returnList(String[] args, int argsLength) {
        List<String> completions = new ArrayList<>();
        for (TabListEnum tabListEnum : TabListEnum.values()) {

            if (tabListEnum.getBefPos() - 1 >= args.length) {
                continue;
            }

            if (tabListEnum.getBef() != null && !tabListEnum.getBef().equalsIgnoreCase(args[tabListEnum.getBefPos() - 1])) {
                continue;
            }

            if (tabListEnum.getNum() != argsLength) {
                continue;
            }
            completions = tabListEnum.getList();
            if (GIVE_THREE.equals(tabListEnum)) {
                completions = new ArrayList<>(CompanionsConstants.COMPANION_DETAILS_MAP.keySet());
            }
            if (GIVE_EQUIPMENT_TWO.equals(tabListEnum)) {
                completions = new ArrayList<>(CompanionsConstants.COMPANION_EQUIPMENT_MAP.keySet());
            }
            if (FORCE_UPGRADE_FOUR.equals(tabListEnum)) {
                completions = new ArrayList<>(CompanionsConstants.COMPANION_EQUIPMENT_MAP.keySet());
            }

            return completions;
        }
        return completions;
    }

}
