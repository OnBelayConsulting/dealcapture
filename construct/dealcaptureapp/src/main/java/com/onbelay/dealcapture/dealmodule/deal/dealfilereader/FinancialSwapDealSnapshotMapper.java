package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;

public class FinancialSwapDealSnapshotMapper extends BaseDealSnapshotMapper{


    public FinancialSwapDealSnapshotMapper() {
        super(new FinancialSwapDealSnapshot());
    }

    @Override
    public SourceFileFormat getSourceFileFormat() {
        return new FinancialSwapDealFileFormat();
    }

    @Override
    public BaseDealSnapshot getSnapshot() {
        FinancialSwapDealSnapshot financialSwapDealSnapshot = (FinancialSwapDealSnapshot) super.getSnapshot();

        if (financialSwapDealSnapshot.getPaysPriceIndexId() != null)
            financialSwapDealSnapshot.getDetail().setPaysValuationCode(ValuationCode.INDEX);

        if (financialSwapDealSnapshot.getDealDetail().getFixedPrice() != null) {
            if (financialSwapDealSnapshot.getPaysPriceIndexId() == null)
                financialSwapDealSnapshot.getDetail().setPaysValuationCode(ValuationCode.FIXED);
            else
                financialSwapDealSnapshot.getDetail().setPaysValuationCode(ValuationCode.INDEX_PLUS);
        }
        financialSwapDealSnapshot.getDetail().setReceivesValuationCode(ValuationCode.INDEX);

        return super.getSnapshot();
    }

    public boolean setPropertyValue(
            DealColumnType columnType,
            Object value) {

        boolean isSet = super.setPropertyValue(columnType, value);

        if (isSet)
            return true;

        FinancialSwapDealSnapshot financialSwapDealSnapshot = (FinancialSwapDealSnapshot) snapshot;


        switch (columnType) {

            case PAYS_INDEX_NAME -> {
                financialSwapDealSnapshot.setPaysPriceIndexId(new EntityId((String)value));
                return true;
            }

            case PAYS_PRICE -> {
                financialSwapDealSnapshot.getDealDetail().setFixedPriceValue((BigDecimal) value);
                return true;
            }

            case PAYS_PRICE_UOM -> {
                UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.lookUp((String) value);
                financialSwapDealSnapshot.getDealDetail().setFixedPriceUnitOfMeasureCode(unitOfMeasureCode);
                return true;
            }

            case PAYS_PRICE_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                financialSwapDealSnapshot.getDealDetail().setFixedPriceCurrencyCode(currencyCode);
                return true;
            }

            case RECEIVES_INDEX_NAME -> {
                financialSwapDealSnapshot.setReceivesPriceIndexId(new EntityId((String)value));
                return true;
            }

            default ->  {
                return false;
            }

        }
    }


}
