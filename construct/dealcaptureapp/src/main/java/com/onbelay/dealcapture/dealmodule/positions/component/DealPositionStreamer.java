package com.onbelay.dealcapture.dealmodule.positions.component;

import com.onbelay.dealcapture.dealmodule.deal.component.ColumnType;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;

import java.util.ArrayList;
import java.util.List;

public class DealPositionStreamer {

    private DealPositionView position;

    public DealPositionStreamer(DealPositionView position) {
        this.position = position;
    }

    public List<String> asAList() {
        List<String> list = new ArrayList<String>();
        list.add(position.getDetail().getTicketNo());
        list.add(position.getDetail().getDealTypeCode().getCode());
        list.add(position.getDetail().getBuySellCodeValue());

        list.add(ColumnType.DATE.getToCSVConverter().apply(position.getDetail().getStartDate()));
        list.add(ColumnType.DATE.getToCSVConverter().apply(position.getDetail().getEndDate()));
        list.add(position.getDetail().getFrequencyCodeValue());
        list.add(position.getDetail().getCurrencyCodeValue());
        list.add(position.getDetail().getVolumeQuantityValue().toPlainString());
        list.add(position.getDetail().getVolumeUnitOfMeasureValue());
        list.add(position.getDetail().getPowerFlowCodeValue());

        list.add(position.getDetail().getCreatedDateTime().toString());

        if (position.getDetail().getValuedDateTime() != null)
            list.add(position.getDetail().getValuedDateTime().toString());
        else
            list.add(null);

        list.add(position.getDetail().getDealPriceValuationValue());

        if (position.getDetail().getDealPriceValue() != null)
            list.add(position.getDetail().getDealPriceValue().toPlainString());
        else
            list.add(null);

        if (position.getDetail().getDealIndexName() != null)
            list.add(position.getDetail().getDealIndexName());
        else
            list.add(null);

        if (position.getDetail().getDealIndexPriceValue() != null)
            list.add(position.getDetail().getDealIndexPriceValue().toPlainString());
        else
            list.add(null);

        if (position.getDetail().getTotalDealPriceValue() != null)
            list.add(position.getDetail().getTotalDealPriceValue().toPlainString());
        else
            list.add(null);

        list.add(position.getDetail().getMarketPriceValuationValue());
        list.add(position.getDetail().getMarketIndexName());

        if (position.getDetail().getMarketPriceValue() != null)
            list.add(position.getDetail().getMarketPriceValue().toPlainString());
        else
            list.add(null);


        if (position.getDetail().getMtmAmountValue() != null)
            list.add(position.getDetail().getMtmAmountValue().toPlainString());
        else
            list.add(null);

        if (position.getDetail().getSettlementCurrencyCodeValue() != null)
            list.add(position.getDetail().getSettlementCurrencyCodeValue());
        else
            list.add(null);

        if (position.getDetail().getCostSettlementAmountValue() != null)
            list.add(position.getDetail().getCostSettlementAmountValue().toPlainString());
        else
            list.add(null);

        if (position.getDetail().getSettlementAmountValue() != null)
            list.add(position.getDetail().getSettlementAmountValue().toPlainString());
        else
            list.add(null);

        if (position.getDetail().getTotalSettlementAmountValue() != null)
            list.add(position.getDetail().getTotalSettlementAmountValue().toPlainString());
        else
            list.add(null);

        list.add(position.getDetail().getErrorCode());
        list.add(position.getDetail().getErrorMessage());
        return list;
    }

}
