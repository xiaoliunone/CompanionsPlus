package cn.handyplus.companions.inventory;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.GuiTypeEnum;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.hook.PlaceholderApiUtil;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.db.enter.Page;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;


public class OpenGui {

    private OpenGui() {
    }

    private final static OpenGui INSTANCE = new OpenGui();

    public static OpenGui getInstance() {
        return INSTANCE;
    }

    public Inventory createGui(Player player, Integer pageNum) {
        String title = ConfigUtil.OPEN_CONFIG.getString("title");
        title = PlaceholderApiUtil.set(player, title);
        int size = ConfigUtil.OPEN_CONFIG.getInt("size", BaseConstants.GUI_SIZE_54);
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.OPEN.getType(), title, size);
        handyInventory.setPlayer(player);
        handyInventory.setPageNum(pageNum);
        this.setInventoryDate(handyInventory);
        return handyInventory.getInventory();
    }

    
    public Inventory createGui(Player player) {
        return createGui(player, 1);
    }

    
    public void setInventoryDate(HandyInventory handyInventory) {

        handyInventory.setGuiType(GuiTypeEnum.OPEN.getType());

        HandyInventoryUtil.refreshInventory(handyInventory.getInventory());

        this.setDate(handyInventory);

        this.setFunctionMenu(handyInventory);
    }

    
    private void setDate(HandyInventory handyInventory) {
        Inventory inventory = handyInventory.getInventory();
        Map<Integer, Integer> map = handyInventory.getIntMap();
        Player player = handyInventory.getPlayer();

        String indexStr = ConfigUtil.OPEN_CONFIG.getString("info.index");
        List<Integer> guiIndexList = StrUtil.strToIntList(indexStr);
        handyInventory.setPageSize(guiIndexList.size());

        Page<CompanionsOwnedEnter> page = CompanionsOwnedService.getInstance().page(player.getUniqueId(), handyInventory.getPageNum(), handyInventory.getPageSize());
        handyInventory.setPageCount(page.getTotal());
        if (page.getTotal() < 1) {
            return;
        }
        Map<Integer, ItemStack> itemStackMap = MapUtil.newHashMapWithExpectedSize(page.getRecords().size());
        for (CompanionsOwnedEnter companions : page.getRecords()) {
            CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companions.getCompanion());
            if (companionDetails == null) {
                CompanionsOwnedService.getInstance().remove(player.getUniqueId(), companions.getCompanion());
                MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("companionRemovedFromPlayer", MapUtil.of("${companion}", companions.getCustomName())));
                continue;
            }
            itemStackMap.put(companions.getId(), companionDetails.getGuiItem());
        }
        int i = 0;
        for (Integer id : itemStackMap.keySet()) {
            Integer index = guiIndexList.get(i++);
            inventory.setItem(index, itemStackMap.get(id));
            map.put(index, id);
        }
    }

    
    private void setFunctionMenu(HandyInventory handyInventory) {
        Inventory inventory = handyInventory.getInventory();

        Map<String, String> replacePageMap = HandyInventoryUtil.replacePageMap(handyInventory);
        HandyInventoryUtil.setButton(ConfigUtil.OPEN_CONFIG, inventory, "nextPage", replacePageMap);
        HandyInventoryUtil.setButton(ConfigUtil.OPEN_CONFIG, inventory, "previousPage", replacePageMap);

        HandyInventoryUtil.setButton(ConfigUtil.OPEN_CONFIG, inventory, "shop");

        HandyInventoryUtil.setCustomButton(ConfigUtil.OPEN_CONFIG, handyInventory, "custom");
    }

}