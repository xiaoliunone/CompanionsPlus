package cn.handyplus.companions.enter;

import cn.handyplus.lib.annotation.TableField;
import cn.handyplus.lib.annotation.TableName;
import cn.handyplus.lib.db.enums.IndexEnum;
import lombok.Data;

import java.util.List;


@Data
@TableName(value = "companions_owned", comment = "拥有宠物")
public class CompanionsOwnedEnter {

    @TableField(value = "id")
    private Integer id;

    @TableField(value = "player_uuid", indexEnum = IndexEnum.UNIQUE, notNull = true)
    private String playerUuid;

    @TableField(value = "player_name")
    private String playerName;

    @TableField(value = "companion", length = 255, comment = "宠物")
    private String companion;

    @TableField(value = "customWeapon", length = 255, comment = "定制武器")
    private String customWeapon;

    @TableField(value = "customName", length = 255, comment = "自定义名称")
    private String customName;

    @TableField(value = "nameVisible", comment = "名称可见")
    private Boolean nameVisible;

    @TableField(value = "abilityLevel", comment = "能力水平", fieldDefault = "1", notNull = true)
    private Integer abilityLevel;

    
    private List<String> abilityList;

}