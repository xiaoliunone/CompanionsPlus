package cn.handyplus.companions.command.admin;

import cn.handyplus.companions.enter.CompanionsActiveEnter;
import cn.handyplus.companions.enter.CompanionsCoinEnter;
import cn.handyplus.companions.enter.CompanionsOwnedEnter;
import cn.handyplus.companions.service.CompanionsActiveService;
import cn.handyplus.companions.service.CompanionsCoinService;
import cn.handyplus.companions.service.CompanionsOwnedService;
import cn.handyplus.lib.command.IHandyCommandEvent;
import cn.handyplus.lib.constants.BaseConstants;
import cn.handyplus.lib.db.Db;
import cn.handyplus.lib.db.SqlManagerUtil;
import cn.handyplus.lib.db.enums.DbTypeEnum;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.HandyConfigUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ConvertCommand implements IHandyCommandEvent {

    @Override
    public String command() {
        return "convert";
    }

    @Override
    public String permission() {
        return "companionsPlus.convert";
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        AssertUtil.notTrue(args.length < 2, sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
        String storageMethod = args[1];
        if (!DbTypeEnum.MySQL.getType().equalsIgnoreCase(storageMethod) && !DbTypeEnum.SQLite.getType().equalsIgnoreCase(storageMethod)) {
            MessageUtil.sendMessage(sender, BaseUtil.getMsgNotColor("paramFailureMsg"));
            return;
        }
        if (storageMethod.equalsIgnoreCase(BaseConstants.STORAGE_CONFIG.getString(BaseConstants.STORAGE_METHOD))) {
            MessageUtil.sendMessage(sender, "&4禁止转换！原因，您当前使用的存储方式已经为：" + storageMethod);
            return;
        }

        List<CompanionsCoinEnter> all = CompanionsCoinService.getInstance().findAll();
        List<CompanionsActiveEnter> all1 = CompanionsActiveService.getInstance().findAll();
        List<CompanionsOwnedEnter> all2 = CompanionsOwnedService.getInstance().findAll();

        HandyConfigUtil.setPath(BaseConstants.STORAGE_CONFIG, "storage-method", storageMethod, Collections.singletonList("存储方法(MySQL,SQLite)请复制括号内的类型,不要自己写"), "storage.yml");

        SqlManagerUtil.enableSql();

        Db.use(CompanionsCoinEnter.class).execution().create();
        Db.use(CompanionsActiveEnter.class).execution().create();
        Db.use(CompanionsOwnedEnter.class).execution().create();

        Db.use(CompanionsCoinEnter.class).execution().insertBatch(all);
        Db.use(CompanionsActiveEnter.class).execution().insertBatch(all1);
        Db.use(CompanionsOwnedEnter.class).execution().insertBatch(all2);
        MessageUtil.sendMessage(sender, "&4转换数据完成，请务必重启服务器，不然有可能会出现未知bug");
    }

}