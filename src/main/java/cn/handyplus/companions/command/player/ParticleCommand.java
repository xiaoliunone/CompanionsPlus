package cn.handyplus.companions.command.player;

import cn.handyplus.companions.core.ParticleUtil;
import cn.handyplus.companions.core.PlayerData;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ParticleCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "particle";
    }

    @Override
    public String permission() {
        return "companionsPlus.particle";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = AssertUtil.notPlayer(sender, BaseUtil.getMsgNotColor("noPlayerFailureMsg"));
        if (PlayerData.instanceOf(player).isParticle()) {
            ParticleUtil.removeParticles(player);
            PlayerData.instanceOf(player).setParticle(false);
        } else {
            PlayerData.instanceOf(player).setParticle(true);
        }
        MessageUtil.sendMessage(player, BaseUtil.getMsgNotColor("succeedMsg"));
    }

}