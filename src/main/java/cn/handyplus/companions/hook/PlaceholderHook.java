package cn.handyplus.companions.hook;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.enter.CompanionsCoinEnter;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.service.CompanionsCoinService;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.lib.InitApi;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.Optional;


public class PlaceholderHook extends PlaceholderExpansion {

    
    @Override
    public String getIdentifier() {
        return "companions";
    }

    
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        Optional<CompanionsOwnedEnter> companionsOwnedOpt = CacheUtil.getCache(player.getUniqueId());
        switch (identifier) {
            case "activecompanion":
                return companionsOwnedOpt.map(CompanionsOwnedEnter::getCompanion).orElse(CompanionsConstants.NONE);
            case "companionlevel":
                return companionsOwnedOpt.map(s -> s.getAbilityLevel().toString()).orElse("NOT EQUIPPED");
            case "companionname":
                return companionsOwnedOpt.map(CompanionsOwnedEnter::getCustomName).orElse("NOT EQUIPPED");
            case "companioncoin":
                Optional<CompanionsCoinEnter> companionsCoinOpt = CompanionsCoinService.getInstance().findByUid(player.getUniqueId());
                return companionsCoinOpt.map(s -> s.getCoins().toString()).orElse("0");
            case "companionsize":
                return String.valueOf(CompanionsOwnedService.getInstance().count(player.getUniqueId()));
            default:
                return "";
        }
    }

    
    @Override
    public boolean persist() {
        return true;
    }

    
    @Override
    public boolean canRegister() {
        return true;
    }

    
    @Override
    public String getAuthor() {
        return InitApi.PLUGIN.getDescription().getAuthors().toString();
    }

    
    @Override
    public String getVersion() {
        return InitApi.PLUGIN.getDescription().getVersion();
    }

}