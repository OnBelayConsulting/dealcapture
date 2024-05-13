package com.onbelay.dealcapture.dealmodule.deal.component;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.shared.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PhysicalDealSnapshotMapper {

    public static void setValue(
            PhysicalDealSnapshot snapshot,
            DealColumnType columnType,
            Object value) {

        switch (columnType) {
            case COMPANY_NAME -> {
                snapshot.setCompanyRoleId(new EntityId((String)value));
            }

            case COUNTERPARTY_NAME -> {
                snapshot.setCounterpartyRoleId(new EntityId((String)value));
            }

            case COMMODITY -> {
                CommodityCode commodityCode = CommodityCode.lookUp((String) value);
                snapshot.getDealDetail().setCommodityCode(commodityCode);
            }

            case DEAL_STATUS -> {
                DealStatusCode statusCode = DealStatusCode.lookUp((String) value);
                snapshot.getDealDetail().setDealStatus(statusCode);
            }

            case BUY_SELL -> {
                BuySellCode buySellCode = BuySellCode.lookUp((String) value);
                snapshot.getDealDetail().setBuySell(buySellCode);
            }

            case TICKET_NO -> {
                snapshot.getDealDetail().setTicketNo((String) value);
            }

            case START_DATE -> {
                snapshot.getDealDetail().setStartDate((LocalDate) value);
            }

            case END_DATE -> {
                snapshot.getDealDetail().setEndDate((LocalDate) value);
            }

            case VOL_QUANTITY -> {
                snapshot.getDealDetail().setVolumeQuantity((BigDecimal) value);
            }

            case VOL_UNIT_OF_MEASURE -> {
                UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.lookUp((String) value);
                snapshot.getDealDetail().setVolumeUnitOfMeasure(unitOfMeasureCode);
            }

            case VOL_FREQUENCY -> {
                FrequencyCode frequencyCode = FrequencyCode.lookUp((String) value);
                snapshot.getDealDetail().setVolumeFrequencyCode(frequencyCode);
            }

            case REP_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                snapshot.getDealDetail().setReportingCurrencyCode(currencyCode);
            }

            case SETTLE_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                snapshot.getDealDetail().setSettlementCurrencyCode(currencyCode);
            }

            case DEAL_PRICE -> {
                snapshot.getDetail().setFixedPriceValue((BigDecimal) value);
            }

            case DEAL_PRICE_UOM -> {
                UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.lookUp((String) value);
                snapshot.getDetail().setFixedPriceUnitOfMeasure(unitOfMeasureCode);
            }

            case DEAL_PRICE_CURRENCY -> {
                CurrencyCode currencyCode = CurrencyCode.lookUp((String) value);
                snapshot.getDetail().setFixedPriceCurrencyCode(currencyCode);
            }

            case MARKET_INDEX_NAME -> {
                snapshot.setMarketPriceIndexId(new EntityId((String)value));
            }
        }
    }


}
