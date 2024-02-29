package cn.handyplus.companions.core;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;


@Data
public class CompanionEquipment {

    
    private String key;

    
    private ItemStack item;
    
    private List<String> attributeList;

}