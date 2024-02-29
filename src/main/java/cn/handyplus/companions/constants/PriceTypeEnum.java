package cn.handyplus.companions.constants;

import cn.handyplus.lib.core.NumberUtil;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum PriceTypeEnum {
    
    VAULT,
    
    COMPANIONS,
    
    PLAYER_POINTS;

    public static PriceType getPrice(String rawPrice) {
        if (StrUtil.isEmpty(rawPrice)) {
            return PriceType.builder().build();
        }
        PriceTypeEnum priceTypeEnum = PriceTypeEnum.VAULT;
        if (rawPrice.contains("C")) {
            priceTypeEnum = PriceTypeEnum.COMPANIONS;
            rawPrice = rawPrice.replace("C", "");
        }
        if (rawPrice.contains("P")) {
            priceTypeEnum = PriceTypeEnum.PLAYER_POINTS;
            rawPrice = rawPrice.replace("P", "");
        }
        String formatPrice = rawPrice;
        switch (priceTypeEnum) {
            case VAULT:
                formatPrice = rawPrice + BaseUtil.getLangMsg("vaultPrice");
                break;
            case COMPANIONS:
                formatPrice = rawPrice + BaseUtil.getLangMsg("companionsPrice");
                break;
            case PLAYER_POINTS:
                formatPrice = rawPrice + BaseUtil.getLangMsg("pointPrice");
                break;
            default:
                break;
        }
        return PriceType.builder().price(NumberUtil.isNumericToLong(rawPrice)).priceTypeEnum(priceTypeEnum).formatPrice(formatPrice).build();
    }

    @Data
    @Builder
    public static class PriceType {
        private PriceTypeEnum priceTypeEnum;
        private Long price;
        private String formatPrice;
    }

}
