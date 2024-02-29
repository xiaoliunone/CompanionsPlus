package cn.handyplus.companions.constants;


import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.core.CompanionEquipment;
import cn.handyplus.companions.packets.HandyPacket;

import java.util.HashMap;
import java.util.Map;


public class CompanionsConstants {

    
    public final static String PLUGIN_VERSION_URL = "https:

    
    public static final Map<String, CompanionDetails> COMPANION_DETAILS_MAP = new HashMap<>();

    
    public static final Map<String, CompanionEquipment> COMPANION_EQUIPMENT_MAP = new HashMap<>();

    
    public final static String ALL = "all";

    
    public final static String NONE = "NONE";

    
    public static HandyPacket COMPANION_PACKET;

    
    public final static String BUY_ALL_PERMISSION = "companions.buy.all";
    
    public final static String UPGRADE_PERMISSION = "companions.upgrade.ability";
    
    public final static String CHANGE_WEAPON_PERMISSION = "companions.upgrade.changeWeapon";
    
    public final static String RENAME_PERMISSION = "companions.upgrade.rename";
    
    public final static String HIDE_NAME_PERMISSION = "companions.upgrade.hideName";
    
    public final static String BLACK_LIST_PERMISSION = "companions.admin.blacklist";
    
    public final static String COOL_DOWN_PERMISSION = "companions.bypass.coolDown";

}
