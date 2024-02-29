package cn.handyplus.companions.listener.gui;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.GuiTypeEnum;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.inventory.OpenGui;
import cn.handyplus.companions.inventory.ShopGui;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;


public class ShopClickEvent implements IHandyClickEvent {

    @Override
    public String guiType() {
        return GuiTypeEnum.SHOP.getType();
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void rawSlotClick(HandyInventory handyInventory, InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        Integer pageNum = handyInventory.getPageNum();
        Integer pageCount = handyInventory.getPageCount();
        Player player = handyInventory.getPlayer();
        Map<Integer, String> map = handyInventory.getStrMap();


        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.SHOP_CONFIG, "previousPage")) {
            if (pageNum > 1) {
                handyInventory.setPageNum(handyInventory.getPageNum() - 1);
                ShopGui.getInstance().setInventoryDate(handyInventory);
            }
            return;
        }

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.SHOP_CONFIG, "nextPage")) {
            if (pageNum + 1 <= pageCount) {
                handyInventory.setPageNum(handyInventory.getPageNum() + 1);
                ShopGui.getInstance().setInventoryDate(handyInventory);
            }
            return;
        }

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.SHOP_CONFIG, "open")) {
            handyInventory.syncOpen(OpenGui.getInstance().createGui(player));
            return;
        }

        Map<Integer, String> custom = HandyInventoryUtil.getCustomButton(ConfigUtil.SHOP_CONFIG, "custom");
        String command = custom.get(rawSlot);
        if (StrUtil.isNotEmpty(command)) {
            PlayerSchedulerUtil.syncPerformReplaceCommand(player, command);
            return;
        }

        String name = map.get(rawSlot);
        if (StrUtil.isEmpty(name)) {
            return;
        }

        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(name);
        long itemPrice = companionDetails.getItemPrice();
        if (itemPrice < 0) {
            return;
        }

        if (CompanionUtil.takeMoney(player, companionDetails.getRawPrice())) {
            return;
        }

        CompanionUtil.addOwned(player, name);
        ShopGui.getInstance().setInventoryDate(handyInventory);
        MessageUtil.sendMessage(player, StrUtil.replace(BaseUtil.getMsgNotColor("companionReceived"), "companion", companionDetails.getName()));
    }

}