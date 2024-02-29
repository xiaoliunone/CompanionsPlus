package cn.handyplus.companions.util;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.PriceTypeEnum;
import cn.handyplus.companions.core.AbilitiesUtil;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.core.ParticleUtil;
import cn.handyplus.companions.core.PotionEffectUtil;
import cn.handyplus.companions.enter.CompanionsActiveEnter;
import cn.handyplus.companions.enter.CompanionsCoinEnter;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.hook.PlayerPointsUtil;
import cn.handyplus.companions.hook.VaultUtil;
import cn.handyplus.companions.service.CompanionsActiveService;
import cn.handyplus.companions.service.CompanionsCoinService;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;


public class CompanionUtil {

    public static void delayCompanionSpawn(Player player) {

        HandySchedulerUtil.runTaskLater(() -> CompanionsConstants.COMPANION_PACKET.loadHandyCompanion(player), 30L);
    }

    public static void delayToggleCompanion(Player player) {

        HandySchedulerUtil.runTaskLater(() -> CompanionsConstants.COMPANION_PACKET.toggleHandyCompanion(player), 30L);
    }

    public static void delActiveCompanion(Player player) {

        CompanionsActiveService.getInstance().remove(player.getUniqueId());

        CacheUtil.db2ActiveCache(player.getUniqueId());
    }

    public static void addActiveCompanion(Player player, String companionName) {

        CompanionsActiveService.getInstance().remove(player.getUniqueId());

        CompanionsActiveEnter companionsActiveEnter = new CompanionsActiveEnter();
        companionsActiveEnter.setPlayerUuid(player.getUniqueId().toString());
        companionsActiveEnter.setPlayerName(player.getName());
        companionsActiveEnter.setCompanion(companionName);
        CompanionsActiveService.getInstance().add(companionsActiveEnter);

        CacheUtil.db2ActiveCache(player.getUniqueId());
    }

    
    public static void addOwned(Player player, String companionName) {

        CompanionsOwnedService.getInstance().remove(player.getUniqueId(), companionName);

        CompanionsOwnedEnter companionsOwnedEnter = new CompanionsOwnedEnter();
        companionsOwnedEnter.setPlayerUuid(player.getUniqueId().toString());
        companionsOwnedEnter.setPlayerName(player.getName());
        companionsOwnedEnter.setCompanion(companionName);
        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(companionName);
        companionsOwnedEnter.setCustomWeapon(companionDetails.getWeapon());
        companionsOwnedEnter.setCustomName(companionDetails.getName());
        companionsOwnedEnter.setNameVisible(companionDetails.isNameVisible());
        companionsOwnedEnter.setAbilityLevel(1);
        CompanionsOwnedService.getInstance().add(companionsOwnedEnter);
    }

    public static void changeName(Player player, String customName, Integer id) {

        CompanionsOwnedService.getInstance().updateCustomName(id, customName);

        if (id.equals(CacheUtil.getActiveId(player.getUniqueId()))) {
            CacheUtil.db2ActiveCache(player.getUniqueId());
            CompanionsConstants.COMPANION_PACKET.setHandyCompanionCustomName(player, customName);
        }
    }

    public static void changeName(Player player, String customName, CompanionsOwnedEnter companionsOwned) {

        CompanionsOwnedService.getInstance().updateCustomName(companionsOwned.getId(), customName);

        if (companionsOwned.getCompanion().equalsIgnoreCase(CacheUtil.getActiveCompanionName(player.getUniqueId()))) {
            CacheUtil.db2ActiveCache(player.getUniqueId());
            CompanionsConstants.COMPANION_PACKET.setHandyCompanionCustomName(player, customName);
        }
    }

    public static void changeNameVisible(Player player, CompanionsOwnedEnter companionsOwned) {

        CompanionsOwnedService.getInstance().updateNameVisible(companionsOwned.getId(), !companionsOwned.getNameVisible());

        if (companionsOwned.getCompanion().equalsIgnoreCase(CacheUtil.getActiveCompanionName(player.getUniqueId()))) {
            CacheUtil.db2ActiveCache(player.getUniqueId());
            CompanionsConstants.COMPANION_PACKET.setHandyCompanionVisible(player, !companionsOwned.getNameVisible());
        }
    }

    public static void changeWeapon(Player player, String newWeapon, CompanionsOwnedEnter companionsOwned) {

        CompanionsOwnedService.getInstance().updateCustomWeapon(companionsOwned.getId(), newWeapon);

        if (companionsOwned.getCompanion().equalsIgnoreCase(CacheUtil.getActiveCompanionName(player.getUniqueId()))) {
            CacheUtil.db2ActiveCache(player.getUniqueId());
            CompanionsConstants.COMPANION_PACKET.setHandyCompanionWeapon(player, ItemStackUtil.getItemStack(newWeapon));
        }
    }

    
    public static ItemStack getToken() {
        String tokenType = ConfigUtil.CONFIG.getString("items.companionToken.type");
        String tokenName = ConfigUtil.CONFIG.getString("items.companionToken.name");
        List<String> tokenDescList = ConfigUtil.CONFIG.getStringList("items.companionToken.description");
        return ItemStackUtil.getItemStack(tokenType, tokenName, tokenDescList);
    }

    
    public static void removeCompanion(Player player) {

        PotionEffectUtil.remove(player);

        AbilitiesUtil.stopCommandAbility(player);

        ParticleUtil.removeParticles(player);

        CompanionsConstants.COMPANION_PACKET.deSpawnHandyCompanion(player);

        if (!player.getGameMode().equals(GameMode.CREATIVE) && player.isFlying()) {
            HandySchedulerUtil.runTask(() -> player.setAllowFlight(false));
        }
    }

    
    public static boolean takeMoney(Player player, String rawPrice) {
        PriceTypeEnum.PriceType priceType = PriceTypeEnum.getPrice(rawPrice);
        switch (priceType.getPriceTypeEnum()) {
            case COMPANIONS:
                Optional<CompanionsCoinEnter> companionsCurrencyEnterOptional = CompanionsCoinService.getInstance().findByUid(player.getUniqueId());
                if (companionsCurrencyEnterOptional.filter(v -> v.getCoins() >= priceType.getPrice()).isPresent()) {
                    CompanionsCoinService.getInstance().take(player.getUniqueId(), priceType.getPrice());
                } else {
                    String notEnoughMoney = StrUtil.replace(BaseUtil.getMsgNotColor("notEnoughMoney"), "price", priceType.getFormatPrice());
                    MessageUtil.sendMessage(player, notEnoughMoney);
                    return true;
                }
                break;
            case VAULT:
                boolean vaultRst = VaultUtil.buy(player, priceType.getPrice());
                if (!vaultRst) {
                    String notEnoughMoney = StrUtil.replace(BaseUtil.getMsgNotColor("notEnoughMoney"), "price", priceType.getFormatPrice());
                    MessageUtil.sendMessage(player, notEnoughMoney);
                    return true;
                }
                break;
            case PLAYER_POINTS:
                boolean pointsRst = PlayerPointsUtil.buy(player, priceType.getPrice().intValue());
                if (!pointsRst) {
                    String notEnoughMoney = StrUtil.replace(BaseUtil.getMsgNotColor("notEnoughMoney"), "price", priceType.getFormatPrice());
                    MessageUtil.sendMessage(player, notEnoughMoney);
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

}