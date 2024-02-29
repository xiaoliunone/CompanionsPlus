package cn.handyplus.companions;

import cn.handyplus.companions.hook.HookUtil;
import cn.handyplus.companions.util.ConfigUtil;
import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public class CompanionsPlus extends JavaPlugin {
    public static CompanionsPlus INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        InitApi initApi = InitApi.getInstance(this);
        ConfigUtil.init();

        HookUtil.init();

        getLogger().log(Level.SEVERE, "错误! 插件不支持当前版本 - 请联系米饭以解决此问题.");
        Bukkit.getPluginManager().disablePlugin(this);

        ConfigUtil.yml2Db();
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "已成功载入服务器！");
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "Author:handy 使用文档: https:
    }

    @Override
    public void onDisable() {
        InitApi.disable();
    }

}