package com.onbelay.dealcapture.dealmodule.deal.component;

import java.util.ArrayList;
import java.util.List;

public class DealFileFormat implements SourceFileFormat {

    private List<DealColumnType> columns = new ArrayList<>();

    public DealFileFormat() {
        addColumn(DealColumnType.COMPANY_NAME);
        addColumn(DealColumnType.COUNTERPARTY_NAME);
        addColumn(DealColumnType.COMMODITY);
        addColumn(DealColumnType.DEAL_STATUS);
        addColumn(DealColumnType.BUY_SELL);
        addColumn(DealColumnType.TICKET_NO);
        addColumn(DealColumnType.START_DATE);
        addColumn(DealColumnType.END_DATE);
        addColumn(DealColumnType.VOL_QUANTITY);
        addColumn(DealColumnType.VOL_UNIT_OF_MEASURE);
        addColumn(DealColumnType.VOL_FREQUENCY);
        addColumn(DealColumnType.REP_CURRENCY);
        addColumn(DealColumnType.SETTLE_CURRENCY);
        addColumn(DealColumnType.DEAL_PRICE);
        addColumn(DealColumnType.DEAL_PRICE_UOM);
        addColumn(DealColumnType.DEAL_PRICE_CURRENCY);
        addColumn(DealColumnType.MARKET_INDEX_NAME);

    }

    private void addColumn(DealColumnType columnType) {
        columns.add(columnType);
    }

    public DealColumnType get(int index) {
        return columns.get(index);
    }

    @Override
    public String[] getAsArray() {

        String[] array = new String[columns.size()];
        for (int i=0 ; i< columns.size(); i++) {
            DealColumnType type = columns.get(i);
            array[i] = type.getCode();
        }
        return array;
    }

    @Override
    public int length() {
        return columns.size();
    }
}
