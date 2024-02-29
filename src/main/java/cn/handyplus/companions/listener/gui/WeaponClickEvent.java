package cn.handyplus.companions.listener.gui;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.GuiTypeEnum;
import cn.handyplus.companions.constants.PriceTypeEnum;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.inventory.UpgradeGui;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;


public class WeaponClickEvent implements IHandyClickEvent {

    @Override
    public String guiType() {
        return GuiTypeEnum.WEAPON.getType();
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void rawSlotClick(HandyInventory handyInventory, InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        Integer pageNum = handyInventory.getPageNum();
        Integer id = handyInventory.getId();
        Player player = handyInventory.getPlayer();


        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.WEAPON_CONFIG, "back")) {
            handyInventory.syncOpen(UpgradeGui.getInstance().createGui(player, id, pageNum));
            return;
        }

        Map<Integer, String> custom = HandyInventoryUtil.getCustomButton(ConfigUtil.WEAPON_CONFIG, "custom");
        String command = custom.get(rawSlot);
        if (StrUtil.isNotEmpty(command)) {
            PlayerSchedulerUtil.syncPerformReplaceCommand(player, command);
            return;
        }

        if (rawSlot > 44 || rawSlot < 0) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        String newWeapon = currentItem.getType().name();
        for (String key : CompanionsConstants.COMPANION_EQUIPMENT_MAP.keySet()) {
            if (CompanionsConstants.COMPANION_EQUIPMENT_MAP.get(key).getItem().isSimilar(currentItem)) {
                newWeapon = key;
                break;
            }
        }
        String rawPrice = ConfigUtil.UPGRADE_CONFIG.getString("weapon.price");

        if (CompanionUtil.takeMoney(player, rawPrice)) {
            return;
        }

        Optional<CompanionsOwnedEnter> companionsOwnedOpt = CompanionsOwnedService.getInstance().findById(id);
        if (!companionsOwnedOpt.isPresent()) {
            handyInventory.syncOpen(UpgradeGui.getInstance().createGui(player, id, pageNum));
            return;
        }

        ItemStackUtil.removeItem(player.getInventory(), currentItem, 1);
        if (CompanionsConstants.COMPANION_EQUIPMENT_MAP.containsKey(companionsOwnedOpt.get().getCustomWeapon())) {
            ItemStackUtil.addItem(player, CompanionsConstants.COMPANION_EQUIPMENT_MAP.get(companionsOwnedOpt.get().getCustomWeapon()).getItem(), 1);
        }


        CompanionUtil.changeWeapon(player, newWeapon, companionsOwnedOpt.get());

        String changedCompanionWeapon = BaseUtil.getMsgNotColor("changedCompanionWeapon");
        PriceTypeEnum.PriceType priceType = PriceTypeEnum.getPrice(rawPrice);
        changedCompanionWeapon = StrUtil.replace(changedCompanionWeapon, "price", priceType.getFormatPrice());
        changedCompanionWeapon = StrUtil.replace(changedCompanionWeapon, "name", BaseUtil.getDisplayName(currentItem));
        MessageUtil.sendMessage(player, changedCompanionWeapon);

        handyInventory.syncOpen(UpgradeGui.getInstance().createGui(player, id, pageNum));
    }

}
