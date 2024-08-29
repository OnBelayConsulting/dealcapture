package com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter;

import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.ColumnType;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.FinancialSwapPositionView;

import java.util.ArrayList;
import java.util.List;

public class FinancialSwapPositionStreamer extends BaseDealPositionStreamer {


    @Override
    public String[] getHeader() {
        List<String> list =  new ArrayList<>();
        list.addAll(DealPositionColumnType.getAsList());
        list.addAll(FinancialSwapPositionColumnType.getAsList());
        return list.toArray(new String[0]);
    }


    public List<String> asAList(DealPositionView dealPositionView) {
        List<String> list = new ArrayList<String>(super.asAList(dealPositionView));
        
        FinancialSwapPositionView swapPositionView = (FinancialSwapPositionView) dealPositionView;
        list.add(swapPositionView.getDetail().getPaysValuationValue());

        if (swapPositionView.getPriceDetail().getPaysPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(swapPositionView.getPriceDetail().getPaysPriceValue()));
        else
            list.add(null);

        if (swapPositionView.getPriceDetail().getPaysIndexPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(swapPositionView.getPriceDetail().getPaysIndexPriceValue()));
        else
            list.add(null);

        if (swapPositionView.getPriceDetail().getTotalPaysPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(swapPositionView.getPriceDetail().getTotalPaysPriceValue()));
        else
            list.add(null);

        list.add(swapPositionView.getDetail().getReceivesValuationValue());

        if (swapPositionView.getPriceDetail().getReceivesPriceValue() != null)
            list.add(ColumnType.PRICE.getToCSVConverter().apply(swapPositionView.getPriceDetail().getReceivesPriceValue()));
        else
            list.add(null);

        return list;
    }

}
