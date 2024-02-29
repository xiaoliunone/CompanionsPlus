package cn.handyplus.companions.command;

import cn.handyplus.companions.constants.TabListEnum;
import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.command.HandyCommandFactory;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@HandyCommand(name = "companionsPlus", aliases = "cp")
public class CompanionsPlusCommand implements TabExecutor {
    private final static String PERMISSION = "companionsPlus.reload";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (args.length < 1) {
            return sendHelp(sender);
        }
        boolean rst = HandyCommandFactory.getInstance().onCommand(sender, cmd, label, args, BaseUtil.getMsgNotColor("noPermission"));
        if (!rst) {
            return sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands;
        if (!sender.hasPermission(PERMISSION)) {
            commands = new ArrayList<>();
        } else {
            commands = TabListEnum.returnList(args, args.length);
        }
        if (commands == null) {
            return null;
        }
        StringUtil.copyPartialMatches(args[args.length - 1], commands, completions);
        Collections.sort(completions);
        return completions;
    }

    
    private boolean sendHelp(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION)) {
            return true;
        }
        List<String> helps = BaseConstants.LANG_CONFIG.getStringList("helps");
        for (String help : helps) {
            MessageUtil.sendMessage(sender, help);
        }
        return true;
    }

}
