package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.shared.enums.*;

import java.math.BigDecimal;

public class PhysicalDealSnapshotMapper extends BaseDealSnapshotMapper{


    public PhysicalDealSnapshotMapper() {
        super(new PhysicalDealSnapshot());
    }

    @Override
    public SourceFileFormat getSourceFileFormat() {
        return new PhysicalDealFileFormat();
    }

    @Override
    public BaseDealSnapshot getSnapshot() {
        PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) super.getSnapshot();

        if (physicalDealSnapshot.getDealPriceIndexId() != null)
            physicalDealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX);

        if (physicalDealSnapshot.getDetail().getFixedPrice() != null) {
            if (physicalDealSnapshot.getDealPriceIndexId() == null)
                physicalDealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.FIXED);
            else
                physicalDealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX_PLUS);
        }
        physicalDealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);

        return super.getSnapshot();
    }



    public boolean setPropertyValue(
            DealColumnType columnType,
            Object value) {

        boolean isSet = super.setPropertyValue(columnType, value);

        if (isSet)
            return true;

        PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) snapshot;


        switch (columnType) {

            case DEAL_INDEX_NAME -> {
                physicalDealSnapshot.setDealPriceIndexId(new EntityId(String.valueOf(value)));
                return true;
            }

            case DEAL_PRICE -> {
                physicalDealSnapshot.getDetail().setFixedPriceValue((BigDecimal) value);
                return true;
            }

            case DEAL_PRICE_UOM -> {
                UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.lookUp((String) value);
                physicalDealSnapshot.getDetail().setFixedPriceUnitOfMeasure(unitOfMeasureCode);
                return true;
            }

            case DEAL_PRICE_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                physicalDealSnapshot.getDetail().setFixedPriceCurrencyCode(currencyCode);
                return true;
            }

            case MARKET_INDEX_NAME -> {
                physicalDealSnapshot.setMarketPriceIndexId(new EntityId((String)value));
                return true;
            }

            default ->  {
                return false;
            }

        }
    }


}
