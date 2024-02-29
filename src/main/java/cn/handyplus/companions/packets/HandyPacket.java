package cn.handyplus.companions.packets;

import cn.handyplus.companions.constants.CompanionsConstants;
import cn.handyplus.companions.core.CompanionDetails;
import cn.handyplus.companions.core.ParticleUtil;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.util.ArmorStandUtil;
import cn.handyplus.companions.util.CacheUtil;
import cn.handyplus.companions.util.CompanionUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.expand.adapter.HandySchedulerUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Vector3f;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class HandyPacket {

    private final Map<UUID, PacketData> packetData = new ConcurrentHashMap<>();

    public void loadHandyCompanion(Player player) {
        if (ConfigUtil.DISABLED_WORLDS.contains(player.getWorld().getName())) {
            MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("playerInDisabledWorld"));
            return;
        }
        Optional<CompanionsOwnedEnter> companionsOwnedOptional = CacheUtil.getCache(player.getUniqueId());
        if (!companionsOwnedOptional.isPresent()) {
            return;
        }
        CompanionsOwnedEnter companionsOwned = companionsOwnedOptional.get();
        String activeCompanion = companionsOwned.getCompanion();

        ArmorStandUtil.loadBuff(player);

        HandySchedulerUtil.runTaskAsynchronously(() -> {

            ParticleUtil.giveParticle(player);

            CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(activeCompanion);
            double x = Math.cos(Math.toRadians(player.getLocation().getYaw() - 180.0F) + companionDetails.getX());
            double z = Math.sin(Math.toRadians(player.getLocation().getYaw() - 180.0F) + companionDetails.getZ());
            Location loc = player.getLocation().add(x, companionDetails.getY(), z);
            CraftPlayer craftPlayer = (CraftPlayer) player;
            CraftWorld craftWorld = (CraftWorld) player.getLocation().getWorld();
            EntityArmorStand armorStand = new EntityArmorStand(craftWorld.getHandle(), loc.getX(), loc.getY(), loc.getZ());
            PacketPlayOutSpawnEntity entity = new PacketPlayOutSpawnEntity(armorStand, 78);
            craftPlayer.getHandle().h.m(entity, null);
            armorStand.ta(companionsOwned.getNameVisible() != null ? companionsOwned.getNameVisible() : companionDetails.isNameVisible());
            armorStand.a(true);
            armorStand.b(true);
            armorStand.c(true);
            armorStand.d(true);
            armorStand.e(true);
            this.setArmorPose(player, armorStand);
            IChatBaseComponent iChatBaseComponent = IChatBaseComponent.a(BaseUtil.replaceChatColor(companionsOwned.getCustomName()));
            armorStand.b(iChatBaseComponent);

            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> headList = new ArrayList<>();
            headList.add(Pair.of(EnumItemSlot.f, CraftItemStack.asNMSCopy(ArmorStandUtil.getPlayerHead(activeCompanion))));
            PacketPlayOutEntityEquipment head = new PacketPlayOutEntityEquipment(armorStand.tt(), headList);

            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> weaponList = new ArrayList<>();
            weaponList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(ArmorStandUtil.getWeapon(companionsOwned.getCustomWeapon()))));
            PacketPlayOutEntityEquipment weapon = new PacketPlayOutEntityEquipment(armorStand.ad(), weaponList);

            craftPlayer.getHandle().c.a(head, null);
            craftPlayer.getHandle().c.a(weapon, null);
            PacketPlayOutEntityMetadata packetTeleport = new PacketPlayOutEntityMetadata(armorStand.fs(), armorStand.h().e());
            craftPlayer.getHandle().c.a(packetTeleport, null);
            PacketData data = new PacketData();

            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> chestList = new ArrayList<>();
            chestList.add(EnumItemSlot.e, CraftItemStack.asNMSCopy(ArmorStandUtil.getChestPlate(activeCompanion)));
            PacketPlayOutEntityEquipment chest = new PacketPlayOutEntityEquipment(armorStand.ax());
            craftPlayer.getHandle().a.e(chest, null);
            data.setChest(chest);
            data.setCompanionPacket(armorStand);
            data.setCompanionMetaData(packetTeleport);
            data.setSkull(head);
            data.setWeapon(weapon);
            this.packetData.put(player.getUniqueId(), data);
        });
    }

    public void followHandyCompanion(Player player) {
        PacketData data = this.packetData.get(player.getUniqueId());
        if (data == null || data.getCompanionPacket() == null) {
            return;
        }
        String activeCompanionName = CacheUtil.getActiveCompanionName(player.getUniqueId());
        if (StrUtil.isEmpty(activeCompanionName)) {
            return;
        }

        CompanionDetails companionDetails = CompanionsConstants.COMPANION_DETAILS_MAP.get(activeCompanionName);
        double x = Math.cos(Math.toRadians(player.getLocation().getYaw() - 180.0F) + companionDetails.getX());
        double z = Math.sin(Math.toRadians(player.getLocation().getYaw() - 180.0F) + companionDetails.getZ());
        Location loc = player.getLocation().add(x, companionDetails.getY(), z);
        final CraftPlayer cp = (CraftPlayer) player;
        data.getCompanionPacket().a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0.0F);

        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(data.getCompanionPacket());
        cp.getHandle().c.a(packet, null);
        this.packetData.get(player.getUniqueId()).setTeleportPacket(packet);

        List<Entity> entityList = player.getNearbyEntities(Double.parseDouble(ConfigUtil.PACKET_RANGE.get(0)), Double.parseDouble(ConfigUtil.PACKET_RANGE.get(1)), Double.parseDouble(ConfigUtil.PACKET_RANGE.get(2)));

        for (Entity ent : entityList) {
            if (ent.getType() != EntityType.PLAYER) {
                continue;
            }
            Player ePlayer = (Player) ent;
            CraftPlayer craftPlayer = (CraftPlayer) ePlayer;
            if (PlayerData.instanceOf(player).getPlayerPacketList().get(ePlayer) == null) {
                PacketPlayOutSpawnEntity entity = new PacketPlayOutSpawnEntity(data.getCompanionPacket(), 78);
                craftPlayer.getHandle().c.a(entity, null);
                craftPlayer.getHandle().c.a(data.getCompanionMetaData(), null);
                craftPlayer.getHandle().c.a(data.getSkull(), null);
                if (data.getChest() != null) {
                    craftPlayer.getHandle().v.e(data.getChest(), null);
                }

                craftPlayer.getHandle().c.a(data.getWeapon(), null);
                PlayerData.instanceOf(player).getPlayerPacketList().put(ePlayer, true);
            }

            craftPlayer.getHandle().a.c(data.getTeleportPacket(), null);
        }
        if (PlayerData.instanceOf(player).getPlayerPacketList().size() != entityList.size()) {
            for (Player packetPlayer : PlayerData.instanceOf(player).getPlayerPacketList().keySet()) {
                if (!entityList.contains(packetPlayer)) {
                    this.deSpawnCompanion(player, packetPlayer);
                    PlayerData.instanceOf(player).setClear(true);
                    PlayerData.instanceOf(player).getPlayerPacketList().put(packetPlayer, false);
                }
            }
            if (PlayerData.instanceOf(player).isClear()) {
                PlayerData.instanceOf(player).getPlayerPacketList().clear();
                PlayerData.instanceOf(player).setClear(false);
            }
        }
    }

    public void deSpawnHandyCompanion(Player player, Player packetPlayer) {
        PacketData data = this.packetData.get(player.getUniqueId());
        if (data != null && data.getCompanionPacket() != null) {
            PacketPlayOutEntityDestroy pa = new PacketPlayOutEntityDestroy(data.getCompanionPacket().af());
            ((CraftPlayer) packetPlayer).getHandle().c.a(pa, null);
        }
    }

    public void deSpawnHandyCompanion(Player player) {
        PacketData data = this.packetData.get(player.getUniqueId());
        if (data != null && data.getCompanionPacket() != null) {
            PacketPlayOutEntityDestroy pa = new PacketPlayOutEntityDestroy(data.getCompanionPacket().af());
            ((CraftPlayer) player).getHandle().c.a(pa, null);
            for (Player packetPlayer : PlayerData.instanceOf(player).getPlayerPacketList().keySet()) {
                ((CraftPlayer) packetPlayer).getHandle().c.a(pa, null);
            }
            data.setCompanionPacket(null);
            PlayerData.instanceOf(player).getPlayerPacketList().clear();
        }
    }

    public void toggleHandyCompanion(Player player) {
        PacketData data = this.packetData.get(player.getUniqueId());
        if (data != null && data.getCompanionPacket() != null) {
            CompanionUtil.removeCompanion(player);
            this.loadCompanion(player);
        }
    }

    public void setHandyCompanionCustomName(Player player, String newName) {
        PacketData packet = this.packetData.get(player.getUniqueId());
        packet.getCompanionPacket().b(IChatBaseComponent.a(newName));
        this.updateCompanion(player);
    }

    public void setHandyCompanionVisible(Player player, boolean visible) {
        PacketData packet = this.packetData.get(player.getUniqueId());
        packet.getCompanionPacket().n(visible);
        this.updateCompanion(player);
    }

    public void setHandyCompanionWeapon(Player player, ItemStack itemStack) {
        PacketData packet = this.packetData.get(player.getUniqueId());
        packet.getCompanionPacket().a(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack));
        this.updateCompanion(player);
    }

    private void updateCompanion(Player player) {
        deSpawnCompanion(player);
        loadCompanion(player);
    }

    private void setArmorPose(Player player, EntityArmorStand armorStand) {
        String activeCompanionName = CacheUtil.getActiveCompanionName(player.getUniqueId());
        CompanionDetails cd = CompanionsConstants.COMPANION_DETAILS_MAP.get(activeCompanionName);
        armorStand.b(new Vector3f(cd.getBodyPose1(), cd.getBodyPose2(), cd.getBodyPose3()));
        armorStand.a(new Vector3f(cd.getHeadPose1(), cd.getHeadPose2(), cd.getHeadPose3()));
        armorStand.c(new Vector3f(cd.getLeftArmPose1(), cd.getLeftArmPose2(), cd.getLeftArmPose3()));
        armorStand.d(new Vector3f(cd.getRightArmPose1(), cd.getRightArmPose2(), cd.getRightArmPose3()));
    }

    @Getter
    @Setter
    private static class PacketData {
        
        private EntityArmorStand companionPacket;

        private PacketPlayOutEntityMetadata companionMetaData;

        private PacketPlayOutEntityEquipment skull, chest, weapon;

        private PacketPlayOutEntityTeleport teleportPacket;
    }

}
