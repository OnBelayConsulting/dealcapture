package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

public class VanillaOptionDealFileFormat extends DealFileFormat {

    public VanillaOptionDealFileFormat() {
        addColumn(DealColumnType.UNDERLYING_INDEX_NAME);
        addColumn(DealColumnType.OPTION_EXPIRY_DATE_RULE);
        addColumn(DealColumnType.TRADE_TYPE);
        addColumn(DealColumnType.OPTION_TYPE);
        addColumn(DealColumnType.OPTION_STYLE);
        addColumn(DealColumnType.STRIKE_PRICE);
        addColumn(DealColumnType.STRIKE_PRICE_UOM);
        addColumn(DealColumnType.STRIKE_PRICE_CURRENCY);
        addColumn(DealColumnType.PREMIUM_PRICE);
        addColumn(DealColumnType.PREMIUM_PRICE_UOM);
        addColumn(DealColumnType.PREMIUM_PRICE_CURRENCY);
    }

}
