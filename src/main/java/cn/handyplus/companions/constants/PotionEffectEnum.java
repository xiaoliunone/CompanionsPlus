package cn.handyplus.companions.constants;

import cn.handyplus.companions.util.ConfigUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
public enum PotionEffectEnum {
    
    REGENERATION("REGENERATION", "生命恢复"),
    INCREASE_DAMAGE("INCREASE_DAMAGE", "伤害提升"),
    GLOWING("GLOWING", "荧光"),
    INVISIBILITY("INVISIBILITY", "隐身"),
    SPEED("SPEED", "速度提升"),
    FIRE_RESISTANCE("FIRE_RESISTANCE", "火焰抗性"),
    JUMP("JUMP", "跳跃提升"),
    DAMAGE_RESISTANCE("DAMAGE_RESISTANCE", "伤害抵抗"),
    FAST_DIGGING("FAST_DIGGING", "挖掘速度提升"),
    ABSORPTION("ABSORPTION", "吸收伤害"),
    LUCK("LUCK", "幸运"),
    WITHER("WITHER", "凋零"),
    SLOW("SLOW", "缓慢"),
    SLOW_DIGGING("SLOW_DIGGING", "挖掘速度降低"),
    CONFUSION("CONFUSION", "恶心"),
    WEAKNESS("WEAKNESS", "虚弱"),
    LEVITATION("LEVITATION", "飘浮"),
    POISON("POISON", "中毒"),
    WATER_BREATHING("WATER_BREATHING", "水下呼吸"),
    DOLPHINS_GRACE("DOLPHINS_GRACE", "海豚的恩惠"),
    HUNGER("HUNGER", "饥饿");

    private final String type;
    private final String desc;

    public static List<String> getAll() {
        return Arrays.stream(PotionEffectEnum.values()).map(s -> s.type).collect(Collectors.toList());
    }

    public static int getChance(String potionEffect) {
        return ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability." + potionEffect.toLowerCase() + ".chance");
    }

    public static int getDuration(String potionEffect) {
        return ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability." + potionEffect.toLowerCase() + ".duration");
    }

}