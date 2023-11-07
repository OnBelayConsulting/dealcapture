package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.formulas.model.FxRiskFactorHolder;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorErrorCode;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentRiskFactorManager implements RiskFactorManager {
    private static final Logger logger = LogManager.getLogger();

    private ConcurrentHashMap<String, FxIndexSnapshot> fromToCurrencyfxIndexMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, PriceIndexPositionDateContainer> priceIndexContainerMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, FxIndexPositionDateContainer> fxIndexContainerMap = new ConcurrentHashMap<>();

    private ConcurrentLinkedQueue<PriceRiskFactorHolder> priceRiskFactorHolderQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<FxRiskFactorHolder> fxRiskFactorHolderQueue = new ConcurrentLinkedQueue<>();


    public ConcurrentRiskFactorManager(
            List<PriceIndexSnapshot> priceIndexSnapshots,
            List<FxIndexSnapshot> fxIndexSnapshots,
            List<PriceRiskFactorSnapshot> priceRiskFactorSnapshots,
            List<FxRiskFactorSnapshot> fxRiskFactorSnapshots) {

        initializePriceIndices(priceIndexSnapshots);
        initializePriceRiskFactors(priceRiskFactorSnapshots);
        initializeFxIndices(fxIndexSnapshots);
        initializeFxRiskFactors(fxRiskFactorSnapshots);
    }

    private void initializePriceIndices(List<PriceIndexSnapshot> priceIndexSnapshots) {
        for (PriceIndexSnapshot snapshot : priceIndexSnapshots) {
            priceIndexContainerMap.put(
                    snapshot.getDetail().getName(),
                    new PriceIndexPositionDateContainer(snapshot));
        }
    }

    private void initializePriceRiskFactors(List<PriceRiskFactorSnapshot> priceRiskFactorSnapshots) {
        for (PriceRiskFactorSnapshot snapshot : priceRiskFactorSnapshots) {
            PriceIndexPositionDateContainer container = priceIndexContainerMap.get(snapshot.getPriceIndexId().getCode());
            container.putRiskFactor(snapshot);
        }
    }


    private void initializeFxIndices(List<FxIndexSnapshot> fxIndexSnapshots) {
        for (FxIndexSnapshot snapshot : fxIndexSnapshots) {
            fxIndexContainerMap.put(snapshot.getDetail().getName(), new FxIndexPositionDateContainer(snapshot));

            CurrencyCode firstCode;
            CurrencyCode secondCode;

            if (snapshot.getDetail().getFromCurrencyCode().compareTo(snapshot.getDetail().getToCurrencyCode()) > 0) {
                firstCode = snapshot.getDetail().getFromCurrencyCode();
                secondCode = snapshot.getDetail().getToCurrencyCode();
            } else {
                secondCode = snapshot.getDetail().getFromCurrencyCode();
                firstCode = snapshot.getDetail().getToCurrencyCode();
            }

            if (fromToCurrencyfxIndexMap.containsKey(
                    createFxKey(
                        firstCode,
                        secondCode))) {

                if (snapshot.getDetail().getFrequencyCode() == FrequencyCode.DAILY)
                    fromToCurrencyfxIndexMap.put(
                            createFxKey(
                                    firstCode,
                                    secondCode),
                            snapshot);
            } else {

                fromToCurrencyfxIndexMap.put(
                        createFxKey(firstCode, secondCode),
                        snapshot);
            }
        }

    }


    private void initializeFxRiskFactors(List<FxRiskFactorSnapshot> fxRiskFactorSnapshots) {
        for (FxRiskFactorSnapshot snapshot : fxRiskFactorSnapshots) {
            FxIndexPositionDateContainer container = fxIndexContainerMap.get(snapshot.getFxIndexId().getCode());
            container.putRiskFactor(snapshot);
        }
    }

    @Override
    public PriceRiskFactorHolder determinePriceRiskFactor(String indexName, LocalDate marketDate) {

        PriceIndexPositionDateContainer container = priceIndexContainerMap.get(indexName);

        PriceRiskFactorSnapshot riskFactor = container.findRiskFactor(marketDate);
        if (riskFactor != null) {
            return new PriceRiskFactorHolder(riskFactor);
        } else {
            PriceRiskFactorHolder holder =  new PriceRiskFactorHolder(
                    container.getPriceIndex(),
                    marketDate);
            priceRiskFactorHolderQueue.add(holder);
            return holder;
        }
    }

    @Override
    public FxRiskFactorHolder determineFxRiskFactor(
            CurrencyCode fromCurrencyCode,
            CurrencyCode toCurrencyCode,
            LocalDate marketDate) {

        CurrencyCode firstCode;
        CurrencyCode secondCode;
        if (fromCurrencyCode.compareTo(toCurrencyCode) > 0) {
            firstCode = fromCurrencyCode;
            secondCode = toCurrencyCode;
        } else {
            firstCode = toCurrencyCode;
            secondCode = fromCurrencyCode;
        }

        FxIndexSnapshot fxIndex = fromToCurrencyfxIndexMap.get(
                createFxKey(
                    firstCode,
                    secondCode));

        FxIndexPositionDateContainer container = fxIndexContainerMap.get(fxIndex.getDetail().getName());

        FxRiskFactorSnapshot riskFactor = container.findRiskFactor(marketDate);
        if (riskFactor != null) {
            return new FxRiskFactorHolder(riskFactor);
        } else {
            FxRiskFactorHolder holder =  new FxRiskFactorHolder(
                    fxIndex,
                    marketDate);
            fxRiskFactorHolderQueue.add(holder);
            return holder;
        }
    }

    public static String createFxKey(
            CurrencyCode fromCurrencyCode,
            CurrencyCode toCurrencyCode) {
        return fromCurrencyCode + "_" + toCurrencyCode;
    }

    @Override
    public Queue<PriceRiskFactorHolder> getPriceRiskFactorHolderQueue() {
        return priceRiskFactorHolderQueue;
    }

    @Override
    public Queue<FxRiskFactorHolder> getFxRiskFactorHolderQueue() {
        return fxRiskFactorHolderQueue;
    }

}
