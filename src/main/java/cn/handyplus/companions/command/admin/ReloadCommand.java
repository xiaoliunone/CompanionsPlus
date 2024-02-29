package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "reload";
    }

    @Override
    public String permission() {
        return "companionsPlus.reload";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ConfigUtil.init();
        MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("reloadMsg"));
    }

}