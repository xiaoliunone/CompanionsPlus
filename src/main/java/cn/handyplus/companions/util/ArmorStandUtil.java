package cn.handyplus.companions.util;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.core.AbilitiesUtil;
import cn.handyplus.companions.core.BuffManageUtil;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.core.PotionEffectUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemMetaUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.EulerAngle;

import java.util.List;


public class ArmorStandUtil {

    
    public static void loadBuff(Player player) {

        PotionEffectUtil.give(player);

        AbilitiesUtil.setFly(player);

        AbilitiesUtil.executeCommand(player);

        BuffManageUtil.callBuffEvent(player);
    }

    
    @SuppressWarnings("deprecation")
    public static ArmorStand createArmorStand(Player player, Location location, String companionName) {
        ArmorStand armorStand = player.getWorld().spawn(location, ArmorStand.class);

        armorStand.setBasePlate(false);

        armorStand.setVisible(false);

        armorStand.setCanPickupItems(false);

        armorStand.setSmall(true);

        armorStand.setGravity(false);

        armorStand.setHelmet(getPlayerHead(companionName));

        armorStand.setChestplate(getChestPlate(companionName));


        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionName);

        armorStand.setItemInHand(getWeapon(companionDetails.getWeapon()));

        armorStand.setCustomNameVisible(companionDetails.isNameVisible());

        armorStand.setCustomName(BaseUtil.replaceChatColor(companionDetails.getName()));

        setArmorPose(companionName, armorStand);
        return armorStand;
    }

    
    public static ItemStack getPlayerHead(String companionName) {

        CompanionDetails details = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionName);

        String skullMaterial = "PLAYER_HEAD";
        int customModelData = 0;
        if (StrUtil.isNotEmpty(details.getCustomModelData()) && !CompanionsConstants.NONE.equals(details.getCustomModelData())) {
            List<String> list = StrUtil.strToStrList(details.getCustomModelData(), ":");
            skullMaterial = list.get(0);
            customModelData = list.size() > 1 ? NumberUtil.isNumericToInt(list.get(1), 0) : 0;
        }

        ItemStack companionSkull = ItemStackUtil.getItemStack(skullMaterial, companionName, null, false, customModelData);
        ItemMeta itemMeta = companionSkull.getItemMeta();

        if (itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            setSkull(skullMeta, details.getPlayerSkull(), details.getCustomModelData());
            companionSkull.setItemMeta(skullMeta);
        }
        return companionSkull;
    }

    
    public static void setSkull(SkullMeta skullMeta, String texTureUrl, String customModelData) {
        if (StrUtil.isNotEmpty(customModelData) && !CompanionsConstants.NONE.equals(customModelData)) {
            List<String> list = StrUtil.strToStrList(customModelData, ":");
            Integer data = NumberUtil.isNumericToInt(list.size() > 1 ? list.get(1) : list.get(0));
            ItemMetaUtil.setCustomModelData(skullMeta, data);
            return;
        }
        ItemMetaUtil.setSkull(skullMeta, texTureUrl);
    }

    
    public static ItemStack getChestPlate(String companionName) {
        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionName);

        if ("LEATHER_CHESTPLATE".equalsIgnoreCase(companionDetails.getChestPlate())) {
            ItemStack companionChest = ItemStackUtil.getItemStack("LEATHER_CHESTPLATE");
            ItemMeta itemMeta = companionChest.getItemMeta();

            if (itemMeta instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
                leatherArmorMeta.setColor(Color.fromRGB(companionDetails.getLeatherColorRed(),
                        companionDetails.getLeatherColorGreen(), companionDetails.getLeatherColorBlue()));
                companionChest.setItemMeta(leatherArmorMeta);
            }
            return companionChest;
        }

        if (StrUtil.isEmpty(companionDetails.getChestPlate()) || CompanionsConstants.NONE.equalsIgnoreCase(companionDetails.getChestPlate())) {
            return new ItemStack(Material.AIR);
        }

        return ItemStackUtil.getItemStack(companionDetails.getChestPlate());
    }

    
    public static ItemStack getWeapon(String customWeapon) {
        if (StrUtil.isEmpty(customWeapon) || CompanionsConstants.NONE.equalsIgnoreCase(customWeapon)) {
            return new ItemStack(Material.AIR);
        }
        if (CompanionsConstants.COMPANION_EQUIPMENT_MAP.containsKey(customWeapon)) {
            return CompanionsConstants.COMPANION_EQUIPMENT_MAP.get(customWeapon).getItem();
        }
        return ItemStackUtil.getItemStack(customWeapon);
    }

    
    private static void setArmorPose(String companionName, ArmorStand armorStand) {
        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionName);

        armorStand.setHeadPose(new EulerAngle(
                Math.toRadians(companionDetails.getHeadPose1()),
                Math.toRadians(companionDetails.getHeadPose2()),
                Math.toRadians(companionDetails.getHeadPose3())));

        armorStand.setBodyPose(new EulerAngle(
                Math.toRadians(companionDetails.getBodyPose1()),
                Math.toRadians(companionDetails.getBodyPose2()),
                Math.toRadians(companionDetails.getBodyPose3())));

        armorStand.setLeftArmPose(new EulerAngle(
                Math.toRadians(companionDetails.getLeftArmPose1()),
                Math.toRadians(companionDetails.getLeftArmPose2()),
                Math.toRadians(companionDetails.getLeftArmPose3())));

        armorStand.setRightArmPose(new EulerAngle(
                Math.toRadians(companionDetails.getRightArmPose1()),
                Math.toRadians(companionDetails.getRightArmPose2()),
                Math.toRadians(companionDetails.getRightArmPose3())));

        armorStand.setLeftLegPose(new EulerAngle(
                Math.toRadians(178), Math.toRadians(0), Math.toRadians(0)));

        armorStand.setRightLegPose(new EulerAngle(
                Math.toRadians(178), Math.toRadians(0), Math.toRadians(0)));
    }

}
