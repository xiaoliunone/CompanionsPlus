package cn.handyplus.companions.listener;

import cn.handyplus.companions.core.BuffManageUtil;
import cn.handyplus.companions.event.CompanionBuffEvent;
import cn.handyplus.lib.annotation.HandyListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


@HandyListener
public class CompanionBuffEventListener implements Listener {

    
    @EventHandler
    public void onEvent(CompanionBuffEvent event) {
        BuffManageUtil.setBuff(event.getPlayer(), event.getBuffList());
    }

}