package com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter;

import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.ColumnType;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDealPositionStreamer implements DealPositionStreamer {


    public List<String> asAList(DealPositionView dealPositionView) {
        List<String> list = new ArrayList<String>();
        list.add(dealPositionView.getViewDetail().getTicketNo());
        list.add(dealPositionView.getViewDetail().getDealTypeCode().getCode());
        list.add(dealPositionView.getViewDetail().getBuySellCodeValue());

        list.add(ColumnType.DATE.getToCSVConverter().apply(dealPositionView.getViewDetail().getStartDate()));
        list.add(ColumnType.DATE.getToCSVConverter().apply(dealPositionView.getViewDetail().getEndDate()));
        list.add(dealPositionView.getViewDetail().getFrequencyCodeValue());
        list.add(dealPositionView.getViewDetail().getCurrencyCodeValue());
        list.add(ColumnType.BIG_DECIMAL.getToCSVConverter().apply(dealPositionView.getViewDetail().getVolumeQuantityValue()));
        list.add(dealPositionView.getViewDetail().getVolumeUnitOfMeasureValue());
        list.add(dealPositionView.getViewDetail().getPowerFlowCodeValue());

        list.add(dealPositionView.getViewDetail().getCreatedDateTime().toString());

        if (dealPositionView.getViewDetail().getValuedDateTime() != null)
            list.add(dealPositionView.getViewDetail().getValuedDateTime().toString());
        else
            list.add(null);

        if (dealPositionView.getViewDetail().getMtmAmountValue() != null)
            list.add(ColumnType.BIG_DECIMAL.getToCSVConverter().apply(dealPositionView.getViewDetail().getMtmAmountValue()));
        else
            list.add(null);

        if (dealPositionView.getViewDetail().getSettlementCurrencyCodeValue() != null)
            list.add(dealPositionView.getViewDetail().getSettlementCurrencyCodeValue());
        else
            list.add(null);

        if (dealPositionView.getViewDetail().getCostSettlementAmountValue() != null)
            list.add(ColumnType.BIG_DECIMAL.getToCSVConverter().apply(dealPositionView.getViewDetail().getCostSettlementAmountValue()));
        else
            list.add(null);

        if (dealPositionView.getViewDetail().getSettlementAmountValue() != null)
            list.add(ColumnType.BIG_DECIMAL.getToCSVConverter().apply(dealPositionView.getViewDetail().getSettlementAmountValue()));
        else
            list.add(null);

        if (dealPositionView.getViewDetail().getTotalSettlementAmountValue() != null)
            list.add(ColumnType.BIG_DECIMAL.getToCSVConverter().apply(dealPositionView.getViewDetail().getTotalSettlementAmountValue()));
        else
            list.add(null);

        list.add(dealPositionView.getViewDetail().getErrorCode());
        list.add(dealPositionView.getViewDetail().getErrorMessage());
        return list;
    }

}
