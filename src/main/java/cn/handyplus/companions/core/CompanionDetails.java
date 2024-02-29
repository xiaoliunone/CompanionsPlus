package cn.handyplus.companions.core;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.PriceTypeEnum;
import cn.handyplus.lib.db.enter.Page;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Data
public class CompanionDetails {

    
    private String key;

    
    private String name, sound, playerSkull, customModelData, weapon;
    
    private boolean nameVisible;
    
    private String ability, chestPlate;
    
    private List<String> attributeList;

    
    private Integer maxAbilityLevel;

    
    private int leatherColorRed, leatherColorGreen, leatherColorBlue;

    
    private float rightArmPose1, rightArmPose2, rightArmPose3;
    private float leftArmPose1, leftArmPose2, leftArmPose3;
    private float headPose1, headPose2, headPose3;
    private float bodyPose1, bodyPose2, bodyPose3;
    
    private double x, y, z;
    
    private ItemStack guiItem;
    
    private long itemPrice;

    
    private String formatPrice;

    
    private String rawPrice;

    
    private PriceTypeEnum priceType;

    
    private String permission;

    
    public static Page<CompanionDetails> page(Player player, List<String> existList, Integer pageNum, Integer pageSize) {
        Map<String, CompanionDetails> map = CompanionsConstants.COMPANION_DETAILS_MAP;

        Map<String, CompanionDetails> filteredMap = map.entrySet().stream()
                .filter(e -> !existList.contains(e.getKey()) && (player.hasPermission(e.getValue().getPermission()) || player.hasPermission(CompanionsConstants.BUY_ALL_PERMISSION)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<CompanionDetails> records = filteredMap.values().stream().skip((long) (pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        return new Page<>(filteredMap.size(), records);
    }

}