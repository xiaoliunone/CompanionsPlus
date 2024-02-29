package cn.handyplus.companions.listener.gui;

import cn.handyplus.companions.constants.GuiTypeEnum;
import cn.handyplus.companions.inventory.OpenGui;
import cn.handyplus.companions.inventory.ShopGui;
import cn.handyplus.companions.inventory.UpgradeGui;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.inventory.IHandyClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;


public class OpenClickEvent implements IHandyClickEvent {

    @Override
    public String guiType() {
        return GuiTypeEnum.OPEN.getType();
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
        Map<Integer, Integer> map = handyInventory.getIntMap();

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.OPEN_CONFIG, "previousPage")) {
            if (pageNum > 1) {
                handyInventory.setPageNum(handyInventory.getPageNum() - 1);
                OpenGui.getInstance().setInventoryDate(handyInventory);
            }
            return;
        }

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.OPEN_CONFIG, "nextPage")) {
            if (pageNum + 1 <= pageCount) {
                handyInventory.setPageNum(handyInventory.getPageNum() + 1);
                OpenGui.getInstance().setInventoryDate(handyInventory);
            }
            return;
        }

        if (HandyInventoryUtil.isIndex(rawSlot, ConfigUtil.OPEN_CONFIG, "shop")) {
            handyInventory.syncOpen(ShopGui.getInstance().createGui(player));
            return;
        }

        Map<Integer, String> custom = HandyInventoryUtil.getCustomButton(ConfigUtil.OPEN_CONFIG, "custom");
        String command = custom.get(rawSlot);
        if (StrUtil.isNotEmpty(command)) {
            PlayerSchedulerUtil.syncPerformReplaceCommand(player, command);
            return;
        }

        Integer id = map.get(rawSlot);
        if (id == null) {
            return;
        }
        handyInventory.syncOpen(UpgradeGui.getInstance().createGui(player, id, handyInventory.getPageNum()));
    }

}