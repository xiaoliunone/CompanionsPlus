package cn.handyplus.companions.inventory;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.GuiTypeEnum;
import cn.handyplus.companions.constants.PriceTypeEnum;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.core.CompanionEquipment;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.hook.PlaceholderApiUtil;
import cn.handyplus.companions.hook.VaultUtil;
import cn.handyplus.companions.service.CompanionsCoinService;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.inventory.HandyInventory;
import cn.handyplus.lib.inventory.HandyInventoryUtil;
import cn.handyplus.lib.util.BaseUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.Optional;


public class UpgradeGui {

    private UpgradeGui() {
    }

    private final static UpgradeGui INSTANCE = new UpgradeGui();

    public static UpgradeGui getInstance() {
        return INSTANCE;
    }

    
    public Inventory createGui(Player player, Integer id, Integer pageNum) {
        String title = ConfigUtil.UPGRADE_CONFIG.getString("title");
        title = PlaceholderApiUtil.set(player, title);
        int size = ConfigUtil.UPGRADE_CONFIG.getInt("size", BaseConstants.GUI_SIZE_54);
        HandyInventory handyInventory = new HandyInventory(GuiTypeEnum.UPGRADE.getType(), title, size);
        handyInventory.setPlayer(player);
        handyInventory.setPageNum(pageNum);
        handyInventory.setId(id);
        this.setInventoryDate(handyInventory);
        return handyInventory.getInventory();
    }

    
    public void setInventoryDate(HandyInventory handyInventory) {

        handyInventory.setGuiType(GuiTypeEnum.UPGRADE.getType());

        HandyInventoryUtil.refreshInventory(handyInventory.getInventory());

        this.setFunctionMenu(handyInventory);
    }

    
    private void setFunctionMenu(HandyInventory handyInventory) {
        Inventory inventory = handyInventory.getInventory();
        Player player = handyInventory.getPlayer();
        Optional<CompanionsOwnedEnter> companionsOwnedOptional = CompanionsOwnedService.getInstance().findById(handyInventory.getId());
        if (!companionsOwnedOptional.isPresent()) {
            return;
        }
        CompanionsOwnedEnter companionsOwnedEnter = companionsOwnedOptional.get();
        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionsOwnedEnter.getCompanion());
        if (player.hasPermission(CompanionsConstants.UPGRADE_PERMISSION)) {
            if (companionsOwnedEnter.getAbilityLevel() >= companionDetails.getMaxAbilityLevel()) {

                HandyInventoryUtil.setButton(ConfigUtil.UPGRADE_CONFIG, inventory, "max");
            } else {

                HandyInventoryUtil.setButton(ConfigUtil.UPGRADE_CONFIG, inventory, "up", this.getReplaceMap(companionsOwnedEnter, player, "up"));
            }
        }

        HandyInventoryUtil.setButton(ConfigUtil.UPGRADE_CONFIG, inventory, "status", this.getReplaceMap(companionsOwnedEnter, player, "status"));

        if (player.hasPermission(CompanionsConstants.CHANGE_WEAPON_PERMISSION)) {
            HandyInventoryUtil.setButton(ConfigUtil.UPGRADE_CONFIG, inventory, "weapon", this.getReplaceMap(companionsOwnedEnter, player, "weapon"));
        }

        if (player.hasPermission(CompanionsConstants.RENAME_PERMISSION)) {
            HandyInventoryUtil.setButton(ConfigUtil.UPGRADE_CONFIG, inventory, "name", this.getReplaceMap(companionsOwnedEnter, player, "name"));
        }

        if (player.hasPermission(CompanionsConstants.HIDE_NAME_PERMISSION)) {
            HandyInventoryUtil.setButton(ConfigUtil.UPGRADE_CONFIG, inventory, "hideName", this.getReplaceMap(companionsOwnedEnter, player, "hideName"));
        }

        HandyInventoryUtil.setButton(ConfigUtil.UPGRADE_CONFIG, inventory, "back");

        HandyInventoryUtil.setCustomButton(ConfigUtil.UPGRADE_CONFIG, handyInventory, "custom");
    }

    
    private Map<String, String> getReplaceMap(CompanionsOwnedEnter companionsOwned, Player player, String type) {
        Map<String, String> map = MapUtil.newHashMapWithExpectedSize(7);
        String activeCompanionName = CacheUtil.getActiveCompanionName(player.getUniqueId());
        map.put("level", String.valueOf(companionsOwned.getAbilityLevel()));
        map.put("name", companionsOwned.getCustomName());
        map.put("status", BaseUtil.getMsgNotColor(companionsOwned.getCompanion().equalsIgnoreCase(activeCompanionName) ? "show" : "hide"));

        if (StrUtil.isEmpty(companionsOwned.getCustomWeapon()) || CompanionsConstants.NONE.equalsIgnoreCase(companionsOwned.getCustomWeapon())) {
            map.put("weapon", BaseUtil.getMsgNotColor("none"));
        } else {
            CompanionEquipment companionEquipment = CompanionsConstants.COMPANION_EQUIPMENT_MAP.get(companionsOwned.getCustomWeapon());
            map.put("weapon", companionEquipment != null ? BaseUtil.getDisplayName(companionEquipment.getItem()) : BaseUtil.getMsgNotColor("none"));
        }
        map.put("hideStatus", BaseUtil.getMsgNotColor(companionsOwned.getNameVisible() ? "show" : "hide"));

        String price = ConfigUtil.UPGRADE_CONFIG.getString(type + ".price");
        if (StrUtil.isNotEmpty(price)) {
            PriceTypeEnum.PriceType priceType = PriceTypeEnum.getPrice(price);
            map.put("price", priceType.getFormatPrice());
            String mePrice = "";
            switch (priceType.getPriceTypeEnum()) {
                case VAULT:
                    mePrice = VaultUtil.getPlayerVault(player) + BaseUtil.getMsgNotColor("vaultPrice");
                    break;
                case COMPANIONS:
                    mePrice = CompanionsCoinService.getInstance().findCoinByUid(player.getUniqueId()) + BaseUtil.getMsgNotColor("companionsPrice");
                    break;
                default:
                    break;
            }
            map.put("mePrice", mePrice);
        }
        return map;
    }

}