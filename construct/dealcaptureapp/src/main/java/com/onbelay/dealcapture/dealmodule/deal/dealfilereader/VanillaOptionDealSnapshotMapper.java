package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionStyleCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.TradeTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.VanillaOptionDealSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;

public class VanillaOptionDealSnapshotMapper extends BaseDealSnapshotMapper{


    public VanillaOptionDealSnapshotMapper() {
        super(new VanillaOptionDealSnapshot());
    }

    @Override
    public SourceFileFormat getSourceFileFormat() {
        return new VanillaOptionDealFileFormat();
    }

    public boolean setPropertyValue(
            DealColumnType columnType,
            Object value) {

        boolean isSet = super.setPropertyValue(columnType, value);

        if (isSet)
            return true;

        VanillaOptionDealSnapshot optionDealSnapshot = (VanillaOptionDealSnapshot) snapshot;


        switch (columnType) {

            case UNDERLYING_INDEX_NAME -> {
                optionDealSnapshot.setUnderlyingPriceIndexId(new EntityId((String)value));
                return true;
            }

            case OPTION_EXPIRY_DATE_RULE -> {
                OptionExpiryDateRuleToken token = OptionExpiryDateRuleToken.lookUp((String) value);
                optionDealSnapshot.getDetail().setOptionExpiryDateRuleToken(token);
                return true;
            }

            case TRADE_TYPE -> {
                TradeTypeCode code = TradeTypeCode.lookUp((String) value);
                optionDealSnapshot.getDetail().setTradeTypeCode(code);
                return true;
            }

            case OPTION_TYPE -> {
                OptionTypeCode code = OptionTypeCode.lookUp((String) value);
                optionDealSnapshot.getDetail().setOptionTypeCode(code);
                return true;
            }

            case OPTION_STYLE -> {
                OptionStyleCode code = OptionStyleCode.lookUp((String) value);
                optionDealSnapshot.getDetail().setOptionStyleCode(code);
                return true;
            }

            case STRIKE_PRICE -> {
                optionDealSnapshot.getDetail().setStrikePriceValue((BigDecimal) value);
                return true;
            }

            case STRIKE_PRICE_UOM -> {
                UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.lookUp((String) value);
                optionDealSnapshot.getDetail().setStrikePriceUnitOfMeasureCode(unitOfMeasureCode);
                return true;
            }

            case STRIKE_PRICE_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                optionDealSnapshot.getDetail().setStrikePriceCurrencyCode(currencyCode);
                return true;
            }

            case PREMIUM_PRICE -> {
                optionDealSnapshot.getDetail().setPremiumPriceValue((BigDecimal) value);
                return true;
            }

            case PREMIUM_PRICE_UOM -> {
                UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.lookUp((String) value);
                optionDealSnapshot.getDetail().setPremiumPriceUnitOfMeasureCode(unitOfMeasureCode);
                return true;
            }

            case PREMIUM_PRICE_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                optionDealSnapshot.getDetail().setPremiumPriceCurrencyCode(currencyCode);
                return true;
            }

            default ->  {
                return false;
            }

        }
    }


}
