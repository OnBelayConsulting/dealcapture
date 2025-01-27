package com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter;

import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.ColumnType;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.VanillaOptionPositionView;

import java.util.ArrayList;
import java.util.List;

public class VanillaOptionPositionStreamer extends BaseDealPositionStreamer {


    @Override
    public String[] getHeader() {
        List<String> list =  new ArrayList<>();
        list.addAll(DealPositionColumnType.getAsList());
        list.addAll(VanillaOptionPositionColumnType.getAsList());
        return list.toArray(new String[0]);
    }


    public List<String> asAList(DealPositionView dealPositionView) {
        List<String> list = new ArrayList<String>(super.asAList(dealPositionView));
        
        VanillaOptionPositionView optionPositionView = (VanillaOptionPositionView) dealPositionView;

        if (optionPositionView.getPriceDetail().getStrikePriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(optionPositionView.getPriceDetail().getStrikePriceValue()));
        else
            list.add(null);

        if (optionPositionView.getPriceDetail().getUnderlyingPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(optionPositionView.getPriceDetail().getUnderlyingPriceValue()));
        else
            list.add(null);

        return list;
    }

}
