package cn.handyplus.companions.enter;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.annotation.TableName;
import cn.handyplus.lib.db.enums.IndexEnum;
import lombok.Data;


@Data
@TableName(value = "companions_coin", comment = "玩家宠物货币")
public class CompanionsCoinEnter {

    @TableField(value = "id")
    private Integer id;

    @TableField(value = "player_uuid", indexEnum = IndexEnum.UNIQUE, notNull = true)
    private String playerUuid;

    @TableField(value = "player_name")
    private String playerName;

    @TableField(value = "coins", comment = "玩家宠物货币")
    private Long coins;

}

