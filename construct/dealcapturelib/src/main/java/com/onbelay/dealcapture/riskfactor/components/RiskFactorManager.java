package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.formulas.model.FxRiskFactorHolder;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.util.Queue;

public interface RiskFactorManager {

    public PriceRiskFactorHolder determinePriceRiskFactor(
            String indexName,
            LocalDate positionDate);

    PriceRiskFactorHolder determinePriceRiskFactor(
            PriceIndexPositionDateContainer container,
            LocalDate marketDate);

    public FxRiskFactorHolder determineFxRiskFactor(
            CurrencyCode fromCurrencyCode,
            CurrencyCode toCurrencyCode,
            LocalDate positionDate);


    Queue<PriceRiskFactorHolder> getPriceRiskFactorHolderQueue();

    Queue<FxRiskFactorHolder> getFxRiskFactorHolderQueue();

    PriceIndexPositionDateContainer findPriceIndexContainer(String priceIndexCode);
}
