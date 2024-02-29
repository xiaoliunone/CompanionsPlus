package cn.handyplus.companions.inventory;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.GuiTypeEnum;
import cn.handyplus.companions.core.CompanionEquipment;
import cn.handyplus.companions.hook.PlaceholderApiUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.stream.Collectors;


public class WeaponGui {

    private WeaponGui() {
    }

    private final static WeaponGui INSTANCE = new WeaponGui();

    public static WeaponGui getInstance() {
        return INSTANCE;
    }

    public Inventory createGui(Player player, Integer id, Integer pageNum) {
        String title = ConfigUtil.WEAPON_CONFIG.getString("title");
        title = PlaceholderApiUtil.set(player, title);
        int size = ConfigUtil.WEAPON_CONFIG.getInt("size", BaseConstants.GUI_SIZE_54);
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.WEAPON.getType(), title, size);
        handyInventory.setPlayer(player);
        handyInventory.setId(id);
        handyInventory.setPageNum(pageNum);
        this.setInventoryDate(handyInventory);
        return handyInventory.getInventory();
    }

    
    public void setInventoryDate(HandyInventory handyInventory) {

        handyInventory.setGuiType(GuiTypeEnum.WEAPON.getType());

        HandyInventoryUtil.refreshInventory(handyInventory.getInventory());

        this.setDate(handyInventory);

        this.setFunctionMenu(handyInventory);
    }

    
    private void setDate(HandyInventory handyInventory) {
        Player player = handyInventory.getPlayer();
        Inventory inventory = handyInventory.getInventory();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] contents = playerInventory.getContents();

        ItemStack[] item = contents.clone();
        boolean rst = false;

        List<ItemStack> itemStackList = CompanionsConstants.COMPANION_EQUIPMENT_MAP.values().stream().map(CompanionEquipment::getItem).collect(Collectors.toList());
        for (int i = 0; i < item.length; i++) {
            if (item[i] == null || Material.AIR.equals(item[i].getType())) {
                continue;
            }
            item[i].setAmount(1);
            if (!itemStackList.contains(item[i])) {
                item[i] = null;
            } else {
                rst = true;
            }
        }
        if (!rst) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("noPlayerEquipmentKey"));
            return;
        }
        inventory.setContents(item);
    }

    
    private void setFunctionMenu(HandyInventory handyInventory) {
        Inventory inventory = handyInventory.getInventory();

        HandyInventoryUtil.setButton(ConfigUtil.WEAPON_CONFIG, inventory, "back");

        HandyInventoryUtil.setCustomButton(ConfigUtil.WEAPON_CONFIG, handyInventory, "custom");
    }

}