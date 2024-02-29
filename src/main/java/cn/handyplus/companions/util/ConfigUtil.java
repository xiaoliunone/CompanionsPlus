package cn.handyplus.companions.util;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.PriceTypeEnum;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.core.CompanionEquipment;
import cn.handyplus.companions.enter.CompanionsActiveEnter;
import cn.handyplus.companions.enter.CompanionsCoinEnter;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.service.CompanionsActiveService;
import cn.handyplus.companions.service.CompanionsCoinService;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConfigUtil {
    public static FileConfiguration CONFIG;
    public static FileConfiguration CUSTOM_ABILITY_CONFIG, LANG_CONFIG;
    public static FileConfiguration COMPANIONS_DATA_CONFIG;
    public static FileConfiguration OPEN_CONFIG, SHOP_CONFIG, UPGRADE_CONFIG, WEAPON_CONFIG;

    public static List<String> PACKET_RANGE;
    public static List<String> DISABLED_WORLDS;
    public static String PREFIX;

    public static Map<String, FileConfiguration> COMPANIONS_CONFIG_MAP, EQUIPMENT_CONFIG_MAP;

    
    public static void init() {
        CONFIG = HandyConfigUtil.loadConfig();

        LANG_CONFIG = HandyConfigUtil.loadLangConfig(CONFIG.getString("language", "zh_CN"), true);
        CUSTOM_ABILITY_CONFIG = HandyConfigUtil.load("customability.yml");


        OPEN_CONFIG = HandyConfigUtil.load("gui/open.yml");
        WEAPON_CONFIG = HandyConfigUtil.load("gui/weapon.yml");


        String packetSearchRange = ConfigUtil.CONFIG.getString("settings.packetSearchRange", "");
        PACKET_RANGE = StrUtil.strToStrList(packetSearchRange, ",");

        DISABLED_WORLDS = ConfigUtil.CONFIG.getStringList("settings.disabledWorlds");
        PREFIX = ConfigUtil.LANG_CONFIG.getString("messages.prefix");

        cacheCompanions();

        cacheCompanionsEquipment();


        if (HandyConfigUtil.exists("companionsdata.yml")) {
            COMPANIONS_DATA_CONFIG = HandyConfigUtil.load("companionsdata.yml");
        }
    }

    
    private static void cacheCompanions() {
        CompanionsConstants.COMPANION_DETAILS_MAP.clear();
        for (String key : COMPANIONS_CONFIG_MAP.keySet()) {
            FileConfiguration fileConfiguration = COMPANIONS_CONFIG_MAP.get(key);
            for (String companionName : HandyConfigUtil.getKey(fileConfiguration, "companions")) {
                CompanionDetails details = new CompanionDetails();
                details.setKey(companionName);
                details.setPermission("companions.buy." + companionName);

                String mainPath = "companions.%name%.";

                details.setAbility(fileConfiguration.getString(mainCompanionName + "ability", CompanionsConstants.NONE));
                details.setAttributeList(fileConfiguration.getStringList(mainCompanionName + "attribute"));

                int maxAbilityLevel = ConfigUtil.CONFIG.getInt("settings.maxAbilityLevel", 3);
                    details.setGuiItem(companionSkull);
                }
                if (CompanionsConstants.COMPANION_DETAILS_MAP.containsKey(companionName)) {
                    MessageUtil.sendConsoleMessage("&8[&a✘&8] 加载宠物跳过重复key:&a" + companionName);
                }
                CompanionsConstants.COMPANION_DETAILS_MAP.putIfAbsent(companionName, details);
            }
        }
    }

    
    private static void cacheCompanionsEquipment() {
        CompanionsConstants.COMPANION_EQUIPMENT_MAP.clear();
        for (String equipmentKey : EQUIPMENT_CONFIG_MAP.keySet()) {
            FileConfiguration fileConfiguration = EQUIPMENT_CONFIG_MAP.get(equipmentKey);
            for (String key : HandyConfigUtil.getKey(fileConfiguration, "equipment")) {
                String name = fileConfiguration.getString("equipment." + key + ".name");
                companionEquipment.setKey(key);
                companionEquipment.setItem(itemStack);
                companionEquipment.setAttributeList(attributeList);
                CompanionsConstants.COMPANION_EQUIPMENT_MAP.putIfAbsent(key, companionEquipment);
            }
        }
    }


    public static void yml2Db() {
        if (COMPANIONS_DATA_CONFIG == null) {
            return;
        }
    }

}