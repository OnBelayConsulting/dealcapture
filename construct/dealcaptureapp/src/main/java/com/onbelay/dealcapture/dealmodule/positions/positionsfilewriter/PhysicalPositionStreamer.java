package com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter;

import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.ColumnType;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionView;

import java.util.ArrayList;
import java.util.List;

public class PhysicalPositionStreamer extends BaseDealPositionStreamer {

    @Override
    public String[] getHeader() {
        ArrayList<String> list = new ArrayList<>(DealPositionColumnType.getAsList());
        list.addAll(PhysicalPositionColumnType.getAsList());
        return list.toArray(new String[0]);
    }

    public List<String> asAList(DealPositionView dealPositionView) {
        List<String> list = new ArrayList<String>(super.asAList(dealPositionView));

        PhysicalPositionView physicalPositionView = (PhysicalPositionView) dealPositionView; 
        list.add(physicalPositionView.getDetail().getDealPriceValuationValue());

        if (physicalPositionView.getPriceDetail().getDealPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(physicalPositionView.getPriceDetail().getDealPriceValue()));
        else
            list.add(null);

        if (physicalPositionView.getPriceDetail().getDealIndexPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(physicalPositionView.getPriceDetail().getDealIndexPriceValue()));
        else
            list.add(null);

        if (physicalPositionView.getPriceDetail().getTotalDealPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(physicalPositionView.getPriceDetail().getTotalDealPriceValue()));
        else
            list.add(null);

        list.add(physicalPositionView.getDetail().getMarketPriceValuationValue());

        if (physicalPositionView.getPriceDetail().getMarketPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(physicalPositionView.getPriceDetail().getMarketPriceValue()));
        else
            list.add(null);

        return list;
    }

}
