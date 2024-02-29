package cn.handyplus.companions.listener;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.constants.PotionEffectEnum;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.constants.VersionCheckEnum;
import cn.handyplus.lib.core.MapUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.expand.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.ItemStackUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@HandyListener
public class CustomAbilitiesListener implements Listener {

    @EventHandler
    public void giveMiningVision(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Optional<CompanionsOwnedEnter> activeCompanionOpt = CacheUtil.getCache(player.getUniqueId());
        if (!activeCompanionOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = activeCompanionOpt.get();
        if (activeCompanion.getAbilityList().contains("MINING_VISION")) {
            int yLevel = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.mining_vision.yLevel");
            if (player.getLocation().getY() >= yLevel && PlayerData.instanceOf(player).isMiningVision()) {
                PlayerData.instanceOf(player).setMiningVision(false);
                PlayerSchedulerUtil.removePotionEffect(player, PotionEffectType.NIGHT_VISION);
                return;
            }
            if (player.getLocation().getY() < yLevel && !PlayerData.instanceOf(player).isMiningVision()) {
                PlayerData.instanceOf(player).setMiningVision(true);
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, 1728000, activeCompanion.getAbilityLevel() - 1);
                PlayerSchedulerUtil.addPotionEffects(player, Collections.singletonList(potionEffect));
            }
        }
    }

    @EventHandler
    public void giveFireBall(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Action.LEFT_CLICK_AIR.equals(event.getAction())) {
            return;
        }
        Optional<CompanionsOwnedEnter> activeCompanionOpt = CacheUtil.getCache(player.getUniqueId());
        if (!activeCompanionOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = activeCompanionOpt.get();
        if (activeCompanion.getAbilityList().contains("FIREBALL")) {
            int chance = new Random().nextInt(100);
            int fireballChance = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.fireball.chance");
            if (chance <= fireballChance) {
                player.launchProjectile(Fireball.class);
            }
        }
    }

    @EventHandler
    public void giveLightning(EntityDamageByEntityEvent event) {
        Entity hitGiver = event.getDamager();
        Entity target = event.getEntity();
        Player player = null;
        if (hitGiver instanceof Player) {
            player = (Player) hitGiver;
        }
        if (hitGiver instanceof Projectile) {
            Projectile proj = (Projectile) hitGiver;
            if (proj.getShooter() instanceof Player) {
                player = (Player) proj.getShooter();
            }
        }
        if (player == null) {
            return;
        }
        Optional<CompanionsOwnedEnter> activeCompanionOpt = CacheUtil.getCache(player.getUniqueId());
        if (!activeCompanionOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = activeCompanionOpt.get();
        if (activeCompanion.getAbilityList().contains("LIGHTNING")) {
            int chance = new Random().nextInt(100);
            int lightningChance = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.lightning.chance");
            if (chance <= lightningChance) {
                player.getWorld().strikeLightning(target.getLocation());
            }
        }
    }

    @EventHandler
    public void checkDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Optional<CompanionsOwnedEnter> activeCompanionOpt = CacheUtil.getCache(player.getUniqueId());
        if (!activeCompanionOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = activeCompanionOpt.get();
        if (activeCompanion.getAbilityList().contains("LEAP")) {
            PlayerData.instanceOf(player).setDropped(true);
            HandySchedulerUtil.runTaskLater(() -> PlayerData.instanceOf(player).setDropped(false), 10L);
        }
    }

    @EventHandler
    public void giveVectorJump(PlayerInteractEvent event) {
        if (!Action.LEFT_CLICK_AIR.equals(event.getAction())) {
            return;
        }
        Player player = event.getPlayer();
        Optional<CompanionsOwnedEnter> activeCompanionOpt = CacheUtil.getCache(player.getUniqueId());
        if (!activeCompanionOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = activeCompanionOpt.get();
        if (!activeCompanion.getAbilityList().contains("LEAP")) {
            return;
        }
        if (PlayerData.instanceOf(player).isDropped() || !Material.AIR.equals(ItemStackUtil.getItemInMainHand(player.getInventory()).getType())) {
            return;
        }

        long playerCoolDown = PlayerData.instanceOf(player).getCoolDown().getOrDefault("LEAP", 0L);
        int vectorJumpCoolDown = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.vectorjump.cooldown");
        if (System.currentTimeMillis() - playerCoolDown <= (vectorJumpCoolDown * 1000L)) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("abilityCoolDown"));
            return;
        }

        if (!player.hasPermission(CompanionsConstants.COOL_DOWN_PERMISSION)) {
            PlayerData.instanceOf(player).getCoolDown().put("LEAP", System.currentTimeMillis());
        }

        int vectorMultiplier = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.vectorjump.distanceMultiplier");
        player.setVelocity(player.getLocation().getDirection().normalize().multiply(vectorMultiplier * activeCompanion.getAbilityLevel()));

        if (BaseConstants.VERSION_ID > VersionCheckEnum.V_1_8.getVersionId()) {
            for (int i = 0; i < 5; i += 1) {
                player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(),
                        30, 0, 0, 0, 1);
            }
        } else {
            for (int i = 0; i < 30; i += 1) {
                player.getWorld().playEffect(player.getLocation(), Effect.valueOf("FIREWORKS_SPARK"), 51);
            }
        }


        String sound = ConfigUtil.CUSTOM_ABILITY_CONFIG.getString("ability.vectorjump.sound");
        Optional<Sound> soundOpt = BaseUtil.getSound(sound);
        if (soundOpt.isPresent()) {
            PlayerSchedulerUtil.playSound(player, soundOpt.get(), 1.0F, 1.0F);
        } else {
            MessageUtil.sendConsoleMessage(BaseUtil.getMsgNotColor("notSoundMsg", MapUtil.of("${sound}", sound)));
        }
    }

    @EventHandler
    public void givePotionEffect(EntityDamageByEntityEvent event) {
        Entity hitTaker = event.getEntity();
        if (!(hitTaker instanceof Player)) {
            return;
        }
        Player player = (Player) hitTaker;
        Entity hitDamager = event.getDamager();
        LivingEntity entity = this.getEntity(hitDamager);

        Optional<CompanionsOwnedEnter> activeCompanionOpt = CacheUtil.getCache(player.getUniqueId());
        if (!activeCompanionOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = activeCompanionOpt.get();

        int chance = new Random().nextInt(100);


        List<PotionEffect> playerPotionEffect = new ArrayList<>();
        List<PotionEffect> entityPotionEffect = new ArrayList<>();
        for (String potionEffect : PotionEffectEnum.getAll()) {
            if (chance > PotionEffectEnum.getChance(potionEffect)) {
                continue;
            }
            if (activeCompanion.getAbilityList().contains(potionEffect + "_DEFENSE_CHANCE")) {
                PotionEffectType potionEffectType = PotionEffectType.getByName(potionEffect);
                if (potionEffectType == null) {
                    MessageUtil.sendConsoleMessage("没有药水效果:" + potionEffect);
                    continue;
                }
                int duration = 20 * PotionEffectEnum.getDuration(potionEffect);
                PotionEffect potion = new PotionEffect(potionEffectType, duration, activeCompanion.getAbilityLevel() - 1);
                playerPotionEffect.add(potion);

            } else if (activeCompanion.getAbilityList().contains(potionEffect + "_ATTACK_CHANCE")) {
                if (entity == null) {
                    continue;
                }
                PotionEffectType potionEffectType = PotionEffectType.getByName(potionEffect);
                if (potionEffectType == null) {
                    MessageUtil.sendConsoleMessage("没有药水效果:" + potionEffect);
                    continue;
                }
                int duration = 20 * PotionEffectEnum.getDuration(potionEffect);
                PotionEffect potion = new PotionEffect(potionEffectType, duration, activeCompanion.getAbilityLevel() - 1);
                entityPotionEffect.add(potion);
            }
        }
        PlayerSchedulerUtil.addPotionEffects(player, playerPotionEffect);
        PlayerSchedulerUtil.addPotionEffects(entity, entityPotionEffect);


        int speedBurstChance = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.speed_burst.chance");
        int speedBurstDuration = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.speed_burst.duration") * 20;
        if (chance <= speedBurstChance) {
            if (activeCompanion.getAbilityList().contains("SPEED_BURST")) {
                if (!PlayerData.instanceOf(player).isSpeedBoosted()) {
                    player.setWalkSpeed(0.5F);
                    PlayerData.instanceOf(player).setSpeedBoosted(true);
                    HandySchedulerUtil.runTaskLater(() -> {

                        player.setWalkSpeed(0.19996406F);
                        PlayerData.instanceOf(player).setSpeedBoosted(false);
                    }, (long) activeCompanion.getAbilityLevel() * speedBurstDuration);
                }
            }
        }


        int flingChance = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.fling.chance");
        int flingUpgrade = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.fling.increaseChanceByLevel");
        if (chance <= flingChance + (activeCompanion.getAbilityLevel() - 1) * flingUpgrade) {
            if (activeCompanion.getAbilityList().contains("FLING")) {
                if (entity != null) {
                    double flingDistance = ConfigUtil.CUSTOM_ABILITY_CONFIG.getDouble("ability.fling.distance");
                    entity.setVelocity(new Vector(0, flingDistance, 0));
                }
            }
        }


        int enderManChance = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.enderman.chance");
        int enderManUpgrade = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.enderman.increaseChanceByLevel");
        if (chance <= enderManChance +
                (activeCompanion.getAbilityLevel() - 1) * enderManUpgrade) {
            if (activeCompanion.getAbilityList().contains("ENDERMAN")) {
                int randomX = new Random().nextInt(5);
                player.launchProjectile(EnderPearl.class, new Vector(0, 1, randomX));
            }
        }
    }

    @EventHandler
    public void giveDodge(EntityDamageByEntityEvent event) {
        Entity hitTaker = event.getEntity();
        if (!(hitTaker instanceof Player)) {
            return;
        }
        Player player = (Player) hitTaker;

        Optional<CompanionsOwnedEnter> activeCompanionOpt = CacheUtil.getCache(player.getUniqueId());
        if (!activeCompanionOpt.isPresent()) {
            return;
        }
        CompanionsOwnedEnter activeCompanion = activeCompanionOpt.get();

        int chance = new Random().nextInt(100);

        if (activeCompanion.getAbilityList().contains("DODGE")) {
            int dodgeChance = ConfigUtil.CUSTOM_ABILITY_CONFIG.getInt("ability.dodge.chance");
            if (chance <= dodgeChance) {
                event.setCancelled(true);
            }
        }
    }

    private LivingEntity getEntity(Entity entity) {
        LivingEntity livingEntity = null;
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity) entity;
        }
        if (entity instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof LivingEntity) {
                livingEntity = (LivingEntity) shooter;
            }
        }
        return livingEntity;
    }

}
