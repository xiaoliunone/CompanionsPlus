package cn.handyplus.companions.listener.gui;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.GuiTypeEnum;
import cn.handyplus.companions.constants.PriceTypeEnum;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.inventory.OpenGui;
import cn.handyplus.companions.inventory.UpgradeGui;
import cn.handyplus.companions.inventory.WeaponGui;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class UpgradeClickEvent implements IHandyClickEvent {

    @Override
    public String guiType() {
        return GuiTypeEnum.UPGRADE.getType();
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void rawSlotClick(HandyInventory handyInventory, InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        Player player = handyInventory.getPlayer();

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.UPGRADE_CONFIG, "back")) {
            handyInventory.syncOpen(OpenGui.getInstance().createGui(player, handyInventory.getPageNum()));
            return;
        }

        Map<Integer, String> custom = HandyInventoryUtil.getCustomButton(ConfigUtil.UPGRADE_CONFIG, "custom");
        String command = custom.get(rawSlot);
        if (StrUtil.isNotEmpty(command)) {
            PlayerSchedulerUtil.syncPerformReplaceCommand(player, command);
            return;
        }

        Optional<CompanionsOwnedEnter> companionsOwnedOptional = CompanionsOwnedService.getInstance().findById(handyInventory.getId());
        if (!companionsOwnedOptional.isPresent()) {
            return;
        }
        CompanionsOwnedEnter companionsOwned = companionsOwnedOptional.get();

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.UPGRADE_CONFIG, "up")) {
            upgradeAbility(companionsOwned, player, true, true);
            UpgradeGui.getInstance().setInventoryDate(handyInventory);
            return;
        }

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.UPGRADE_CONFIG, "status")) {
            this.changStatus(handyInventory);
            UpgradeGui.getInstance().setInventoryDate(handyInventory);
            return;
        }

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.UPGRADE_CONFIG, "weapon")) {
            handyInventory.syncOpen(WeaponGui.getInstance().createGui(player, handyInventory.getId(), handyInventory.getPageNum()));
            return;
        }

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.UPGRADE_CONFIG, "name")) {
            PlayerData.instanceOf(player).setChatNameById(handyInventory.getId());
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("inRenaming"));
            handyInventory.syncClose();
            return;
        }

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.UPGRADE_CONFIG, "hideName")) {
            String rawPrice = ConfigUtil.UPGRADE_CONFIG.getString("hideName.price");

            if (CompanionUtil.takeMoney(player, rawPrice)) {
                return;
            }

            CompanionUtil.changeNameVisible(player, companionsOwned);

            HashMap<String, String> map = MapUtil.of("${price}", PriceTypeEnum.getPrice(rawPrice).getFormatPrice());
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("hideCompanion", map));

            UpgradeGui.getInstance().setInventoryDate(handyInventory);
        }
    }

    
    private void changStatus(HandyInventory handyInventory) {
        Player player = handyInventory.getPlayer();
        Optional<CompanionsOwnedEnter> companionsOwnedOptional = CompanionsOwnedService.getInstance().findById(handyInventory.getId());
        if (!companionsOwnedOptional.isPresent()) {
            return;
        }
        CompanionsOwnedEnter companionsOwned = companionsOwnedOptional.get();

        CompanionUtil.removeCompanion(player);

        if (companionsOwned.getCompanion().equalsIgnoreCase(CacheUtil.getActiveCompanionName(player.getUniqueId()))) {

            CompanionUtil.delActiveCompanion(player);
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("toggledBack"));
        } else {

            CompanionUtil.addActiveCompanion(player, companionsOwned.getCompanion());

            CompanionsConstants.COMPANION_PACKET.loadHandyCompanion(player);
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("toggledAway"));
        }
    }

    
    public static void upgradeAbility(CompanionsOwnedEnter companionsOwned, Player player, boolean check, boolean upgrade) {

        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionsOwned.getCompanion());
        if (upgrade && companionsOwned.getAbilityLevel() >= companionDetails.getMaxAbilityLevel()) {
            return;
        }
        String rawPrice = ConfigUtil.UPGRADE_CONFIG.getString("up.price");

        if (check && CompanionUtil.takeMoney(player, rawPrice)) {
            return;
        }

        int abilityIncrement = upgrade ? 1 : -1;

        CompanionsOwnedService.getInstance().updateAbilityLevel(companionsOwned.getId(), companionsOwned.getAbilityLevel() + abilityIncrement);

        if (companionsOwned.getCompanion().equalsIgnoreCase(CacheUtil.getActiveCompanionName(player.getUniqueId()))) {

            CacheUtil.db2ActiveCache(player.getUniqueId());
            CompanionsConstants.COMPANION_PACKET.toggleHandyCompanion(player);
        }

        HashMap<String, String> map = MapUtil.of("${price}", PriceTypeEnum.getPrice(rawPrice).getFormatPrice()
                , "${companion}", companionsOwned.getCustomName());
        MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("abilityBought", map));
    }

}