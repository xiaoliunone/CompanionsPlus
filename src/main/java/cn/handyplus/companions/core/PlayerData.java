package cn.handyplus.companions.core;

import cn.handyplus.lib.expand.adapter.HandyRunnable;
import lombok.Data;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Data
public class PlayerData {

    public PlayerData(Player player) {
        this.player = player;
    }

    private final static Map<UUID, PlayerData> PLAYER_DATA_MAP = new HashMap<>();

    
    private final HashMap<Player, Boolean> playerPacketList = new HashMap<>();

    
    private Player player;

    
    private final List<HandyRunnable> commandTask = new ArrayList<>();

    
    private HandyRunnable particleTask;

    
    private boolean miningVision, dropped, speedBoosted, clear, particle, respawned;

    
    private final Map<String, Long> coolDown = new HashMap<>();

    
    private ArmorStand mysteryCompanion;

    
    private Integer chatNameById;

    
    private int bodyPose = 0, headPose = 0;

    public static PlayerData instanceOf(Player player) {
        PLAYER_DATA_MAP.putIfAbsent(player.getUniqueId(), new PlayerData(player));
        return PLAYER_DATA_MAP.get(player.getUniqueId());
    }

    public void remove() {
        PLAYER_DATA_MAP.remove(getPlayer().getUniqueId());
    }

}
