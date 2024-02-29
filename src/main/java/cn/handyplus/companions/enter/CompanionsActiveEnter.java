package cn.handyplus.companions.enter;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.annotation.TableName;
import cn.handyplus.lib.db.enums.IndexEnum;
import lombok.Data;


@Data
@TableName(value = "companions_active", comment = "活动宠物")
public class CompanionsActiveEnter {

    @TableField(value = "id")
    private Integer id;

    @TableField(value = "player_uuid", indexEnum = IndexEnum.UNIQUE, notNull = true)
    private String playerUuid;

    @TableField(value = "player_name")
    private String playerName;

    @TableField(value = "companion", length = 255, comment = "活动的宠物")
    private String companion;

}