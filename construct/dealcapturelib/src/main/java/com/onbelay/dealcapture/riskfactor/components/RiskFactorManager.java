package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public interface RiskFactorManager {

    PriceIndexSnapshot findPriceIndex(Integer priceIndexId);

    public PriceRiskFactorSnapshot findPriceRiskFactor(
            Integer priceIndexId,
            LocalDate positionDate);

    public PriceRiskFactorSnapshot findPriceRiskFactor(
            Integer priceIndexId,
            LocalDate positionDate,
            Integer hourEnding);

    public PriceRiskFactorHolder determinePriceRiskFactor(
            Integer priceIndexId,
            LocalDate positionDate);

    public PriceRiskFactorHolder determinePriceRiskFactor(
            Integer priceIndexId,
            LocalDate positionDate,
            Integer hourEnding);


    PriceRiskFactorHolder determinePriceRiskFactor(
            PriceIndexPositionDateContainer container,
            LocalDate marketDate);

    PriceRiskFactorHolder determinePriceRiskFactor(
            PriceIndexPositionDateContainer container,
            LocalDate marketDate,
            Integer hourEnding);

    public FxRiskFactorHolder determineFxRiskFactor(
            CurrencyCode fromCurrencyCode,
            CurrencyCode toCurrencyCode,
            LocalDate positionDate);


    Queue<PriceRiskFactorHolder> getPriceRiskFactorHolderQueue();

    Queue<FxRiskFactorHolder> getFxRiskFactorHolderQueue();

    PriceIndexPositionDateContainer findPriceIndexContainer(Integer priceIndexId);

    public HashMap<Integer, List<PriceRiskFactorHolder>> getPriceRiskFactorsSearch();
}
