package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import java.util.ArrayList;
import java.util.List;

public abstract class DealFileFormat implements SourceFileFormat {

    private List<DealColumnType> columns = new ArrayList<>();

    public DealFileFormat() {
        addColumn(DealColumnType.DEAL_TYPE);
        addColumn(DealColumnType.COMPANY_TRADER);
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
    }

    protected void addColumn(DealColumnType columnType) {
        columns.add(columnType);
    }

    @Override
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
