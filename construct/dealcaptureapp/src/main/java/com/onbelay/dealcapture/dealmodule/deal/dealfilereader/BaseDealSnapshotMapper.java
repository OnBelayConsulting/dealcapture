package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.shared.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract  class BaseDealSnapshotMapper {

    protected BaseDealSnapshot snapshot;

    protected BaseDealSnapshotMapper(BaseDealSnapshot dealSnapshot) {
        this.snapshot = dealSnapshot;
    }

    public abstract SourceFileFormat<DealColumnType> getSourceFileFormat();

    public BaseDealSnapshot getSnapshot() {return snapshot;}

    public boolean setPropertyValue(
            DealColumnType columnType,
            Object value) {

        switch (columnType) {
            case COMPANY_TRADER -> {
                snapshot.setCompanyTraderId(new EntityId((String)value));
                return true;
            }
            case COMPANY_NAME -> {
                snapshot.setCompanyRoleId(new EntityId((String)value));
                return true;
            }

            case COUNTERPARTY_NAME -> {
                snapshot.setCounterpartyRoleId(new EntityId((String)value));
                return true;
            }

            case COMMODITY -> {
                CommodityCode commodityCode = CommodityCode.lookUp((String) value);
                snapshot.getDealDetail().setCommodityCode(commodityCode);
                return true;
            }

            case DEAL_STATUS -> {
                DealStatusCode statusCode = DealStatusCode.lookUp((String) value);
                snapshot.getDealDetail().setDealStatus(statusCode);
                return true;
            }

            case BUY_SELL -> {
                BuySellCode buySellCode = BuySellCode.lookUp((String) value);
                snapshot.getDealDetail().setBuySell(buySellCode);
                return true;
            }

            case TICKET_NO -> {
                snapshot.getDealDetail().setTicketNo((String) value);
                return true;
            }

            case START_DATE -> {
                snapshot.getDealDetail().setStartDate((LocalDate) value);
                return true;
            }

            case END_DATE -> {
                snapshot.getDealDetail().setEndDate((LocalDate) value);
                return true;
            }

            case VOL_QUANTITY -> {
                snapshot.getDealDetail().setVolumeQuantity((BigDecimal) value);
                return true;
            }

            case VOL_UNIT_OF_MEASURE -> {
                UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.lookUp((String) value);
                snapshot.getDealDetail().setVolumeUnitOfMeasureCode(unitOfMeasureCode);
                return true;
            }

            case VOL_FREQUENCY -> {
                FrequencyCode frequencyCode = FrequencyCode.lookUp((String) value);
                snapshot.getDealDetail().setVolumeFrequencyCode(frequencyCode);
                return true;
            }

            case REP_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                snapshot.getDealDetail().setReportingCurrencyCode(currencyCode);
                return true;
            }

            case SETTLE_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                snapshot.getDealDetail().setSettlementCurrencyCode(currencyCode);
                return true;
            }

            default ->  {
                return false;
            }
        }
    }


}
