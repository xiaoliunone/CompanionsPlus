package cn.handyplus.companions.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@Getter
public class CompanionBuffEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final List<String> buffList;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public CompanionBuffEvent(Player player, List<String> buffList) {
        this.player = player;
        this.buffList = buffList;
    }

}