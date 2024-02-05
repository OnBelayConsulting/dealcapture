package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public interface RiskFactorManager {

    public PriceRiskFactorHolder determinePriceRiskFactor(
            Integer priceIndexId,
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

    PriceIndexPositionDateContainer findPriceIndexContainer(Integer priceIndexId);

    public HashMap<Integer, List<PriceRiskFactorHolder>> getPriceRiskFactorsSearch();
}
