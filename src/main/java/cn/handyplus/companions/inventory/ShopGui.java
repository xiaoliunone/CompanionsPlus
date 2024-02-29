package cn.handyplus.companions.inventory;

import cn.handyplus.companions.constants.GuiTypeEnum;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.hook.PlaceholderApiUtil;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.db.enter.Page;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ShopGui {

    private ShopGui() {
    }

    private final static ShopGui INSTANCE = new ShopGui();

    public static ShopGui getInstance() {
        return INSTANCE;
    }

    
    public Inventory createGui(Player player) {
        String title = ConfigUtil.SHOP_CONFIG.getString("title");
        title = PlaceholderApiUtil.set(player, title);
        int size = ConfigUtil.SHOP_CONFIG.getInt("size", BaseConstants.GUI_SIZE_54);
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.SHOP.getType(), title, size);
        handyInventory.setPlayer(player);
        this.setInventoryDate(handyInventory);
        return handyInventory.getInventory();
    }

    
    public void setInventoryDate(HandyInventory handyInventory) {

        handyInventory.setGuiType(GuiTypeEnum.SHOP.getType());

        HandyInventoryUtil.refreshInventory(handyInventory.getInventory());

        this.setDate(handyInventory);

        this.setFunctionMenu(handyInventory);
    }

    
    private void setDate(HandyInventory handyInventory) {
        Inventory inventory = handyInventory.getInventory();
        Player player = handyInventory.getPlayer();
        Map<Integer, String> map = handyInventory.getStrMap();

        String indexStr = ConfigUtil.SHOP_CONFIG.getString("info.index");
        List<Integer> guiIndexList = StrUtil.strToIntList(indexStr);
        handyInventory.setPageSize(guiIndexList.size());

        List<CompanionsOwnedEnter> existList = CompanionsOwnedService.getInstance().findByPlayer(player.getUniqueId());
        List<String> existNameList = existList.stream().map(CompanionsOwnedEnter::getCompanion).collect(Collectors.toList());
        Page<CompanionDetails> page = CompanionDetails.page(player, existNameList, handyInventory.getPageNum(), handyInventory.getPageSize());
        handyInventory.setPageCount(page.getTotal());
        if (page.getTotal() < 1) {
            return;
        }
        int i = 0;
        for (CompanionDetails record : page.getRecords()) {
            Integer index = guiIndexList.get(i++);
            inventory.setItem(index, record.getGuiItem());
            map.put(index, record.getKey());
        }
    }

    
    private void setFunctionMenu(HandyInventory handyInventory) {
        Inventory inventory = handyInventory.getInventory();

        Map<String, String> replacePageMap = HandyInventoryUtil.replacePageMap(handyInventory);
        HandyInventoryUtil.setButton(ConfigUtil.SHOP_CONFIG, inventory, "nextPage", replacePageMap);
        HandyInventoryUtil.setButton(ConfigUtil.SHOP_CONFIG, inventory, "previousPage", replacePageMap);

        HandyInventoryUtil.setButton(ConfigUtil.SHOP_CONFIG, inventory, "open");

        HandyInventoryUtil.setCustomButton(ConfigUtil.OPEN_CONFIG, handyInventory, "custom");
    }

}