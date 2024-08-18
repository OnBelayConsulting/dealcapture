package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import java.util.ArrayList;
import java.util.List;

public class PhysicalDealFileFormat extends DealFileFormat {

    public PhysicalDealFileFormat() {
        addColumn(DealColumnType.DEAL_INDEX_NAME);
        addColumn(DealColumnType.DEAL_PRICE);
        addColumn(DealColumnType.DEAL_PRICE_UOM);
        addColumn(DealColumnType.DEAL_PRICE_CURRENCY);
        addColumn(DealColumnType.MARKET_INDEX_NAME);
    }

}
