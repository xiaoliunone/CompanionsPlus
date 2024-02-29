package cn.handyplus.companions.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum GuiTypeEnum {
    
    SHOP("shop", "商店"),
    OPEN("open", "仓库"),
    UPGRADE("upgrade", "升级"),
    WEAPON("weapon", "选择武器"),
    ;

    private final String type;
    private final String title;

}