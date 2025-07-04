package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

public class FinancialSwapDealFileFormat extends DealFileFormat {

    public FinancialSwapDealFileFormat() {
        addColumn(DealColumnType.PAYS_INDEX_NAME);
        addColumn(DealColumnType.PAYS_PRICE);
        addColumn(DealColumnType.PAYS_PRICE_UOM);
        addColumn(DealColumnType.PAYS_PRICE_CURRENCY);
        addColumn(DealColumnType.RECEIVES_INDEX_NAME);
    }

}
