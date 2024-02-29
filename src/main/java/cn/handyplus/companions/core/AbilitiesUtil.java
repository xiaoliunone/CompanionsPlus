package cn.handyplus.companions.core;

import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.HandyRunnable;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Random;


public class AbilitiesUtil {

    
    public static void setFly(Player player) {
        Optional<CompanionsOwnedEnter> companionsOwnedOpt = CacheUtil.getCache(player.getUniqueId());
        if (!companionsOwnedOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = companionsOwnedOpt.get();
        if (activeCompanion.getAbilityList().contains("FLY")) {
            player.setAllowFlight(true);
        } else {
            if (!player.getGameMode().equals(GameMode.CREATIVE) && !player.isFlying()) {
                HandySchedulerUtil.runTask(() -> player.setAllowFlight(true));
            }
        }
    }

    
    public static void executeCommand(Player player) {
        Optional<CompanionsOwnedEnter> activeCompanionOpt = CacheUtil.getCache(player.getUniqueId());
        if (!activeCompanionOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = activeCompanionOpt.get();
        int increaseChanceByLevel = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.command.increaseChanceByLevel", 5);
        for (String ability : activeCompanion.getAbilityList()) {
            if (ability.startsWith("COMMAND")) {


                Random random = new Random();
                ability = ability.replace("COMMAND/", "");
                List<String> commandParameters = StrUtil.strToStrList(ability, "/");
                long interval = NumberUtil.isNumericToLong(commandParameters.get(0), 60L) * 1200L;
                int commandChance = NumberUtil.isNumericToInt(commandParameters.get(1));
                String command = commandParameters.get(2).replace("%player%", player.getName().replace("%companion%", activeCompanion.getCompanion()));

                HandyRunnable handyRunnable = new HandyRunnable() {
                    @Override
                    public void run() {
                        int chance = random.nextInt(100);
                        if (chance <= commandChance + activeCompanion.getAbilityLevel() * increaseChanceByLevel) {
                            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                            if (commandParameters.size() > 3) {
                                MessageUtil.sendMessage(player, commandParameters.get(3));
                            }
                            if (commandParameters.size() > 4) {
                                MessageUtil.sendAllMessage(commandParameters.get(4).replace("%player%", player.getName()));
                            }
                        }
                    }
                };
                PlayerData.instanceOf(player).getCommandTask().add(handyRunnable);
                HandySchedulerUtil.runTaskTimer(handyRunnable, interval, interval);
            }
        }
    }

    public static void stopCommandAbility(Player player) {
        if (!PlayerData.instanceOf(player).getCommandTask().isEmpty()) {
            for (HandyRunnable tasks : PlayerData.instanceOf(player).getCommandTask()) {
                tasks.cancel();
            }
            PlayerData.instanceOf(player).getCommandTask().clear();
        }
    }

}
