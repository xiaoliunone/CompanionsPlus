package cn.handyplus.companions.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
public enum AbilitiesEnum {
    
    FLY("FLY", "允许玩家飞行"),
    FIREBALL("FIREBALL", "向敌人发射火球"),
    DODGE("DODGE", "增加玩家的闪避几率"),
    LEAP("LEAP", "使玩家能够跳跃"),
    LIGHTNING("LIGHTNING", "召唤闪电"),
    MINING_VISION("MINING_VISION", "在挖矿时穿透方块获得视野"),
    SPEED_BURST("SPEED_BURST", "提供临时的速度提升"),
    FLING("FLING", "将实体抛向空中"),
    END_ERMAN("ENDERMAN", "获得末影人的能力"),
    NONE("NONE", "没有自定义能力");

    private final String type;
    private final String desc;

    public static List<String> getAll() {
        return Arrays.stream(AbilitiesEnum.values()).map(s -> s.type).collect(Collectors.toList());
    }

}
