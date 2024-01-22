/*
 Copyright 2019, OnBelay Consulting Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  
 */
package com.onbelay.dealcapture.pricing.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PriceIndexFixture {


    public static PriceIndex createPriceIndex(
            String name,
            PricingLocation pricingLocation) {

        PriceIndex priceIndex = new PriceIndex();

        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setDaysOffsetForExpiry(4);
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setDescription(name + "-Desc");
        snapshot.getDetail().setIndexType(IndexType.HUB);
        snapshot.getDetail().setCurrencyCode(CurrencyCode.USD);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

        snapshot.setPricingLocationId(pricingLocation.generateEntityId());

        priceIndex.createWith(snapshot);

        return priceIndex;
    }


    public static List<EntityId> generateMonthlyPriceCurves(
            PriceIndex priceIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            LocalDateTime observedDateTime) {

        return generateMonthlyPriceCurves(
                priceIndex,
                startMarketDate,
                endMarketDate,
                BigDecimal.TEN,
                observedDateTime);
    }

    public static List<EntityId> generateMonthlyPriceCurves(
            PriceIndex priceIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            BigDecimal value,
            LocalDateTime observedDateTime) {

        LocalDate currentDate = startMarketDate.withDayOfMonth(1);
        ArrayList<PriceCurveSnapshot> prices = new ArrayList<>();
        while (currentDate.isAfter(endMarketDate) == false) {
            PriceCurveSnapshot curveSnapshot = new PriceCurveSnapshot();
            curveSnapshot.getDetail().setCurveDate(currentDate);
            curveSnapshot.getDetail().setCurveValue(value);
            curveSnapshot.getDetail().setObservedDateTime(observedDateTime);
            curveSnapshot.getDetail().setFrequencyCode(FrequencyCode.MONTHLY);
            prices.add(curveSnapshot);

            currentDate = currentDate.plusMonths(1);
        }
        return priceIndex.savePriceCurves(prices);
    }

    public static List<EntityId> generateDailyPriceCurves(
            PriceIndex priceIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            LocalDateTime observedDateTime) {

            return generateDailyPriceCurves(
                    priceIndex,
                    startMarketDate,
                    endMarketDate,
                    BigDecimal.ONE,
                    observedDateTime);
    }

        public static List<EntityId> generateDailyPriceCurves(
            PriceIndex priceIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            BigDecimal priceValue,
            LocalDateTime observedDateTime) {

        LocalDate currentDate = startMarketDate;
        ArrayList<PriceCurveSnapshot> prices = new ArrayList<>();
        while (currentDate.isAfter(endMarketDate) == false) {
            PriceCurveSnapshot curveSnapshot = new PriceCurveSnapshot();
            curveSnapshot.getDetail().setCurveDate(currentDate);
            curveSnapshot.getDetail().setCurveValue(priceValue);
            curveSnapshot.getDetail().setObservedDateTime(observedDateTime);
            curveSnapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
            prices.add(curveSnapshot);

            currentDate = currentDate.plusDays(1);
        }
        return priceIndex.savePriceCurves(prices);
    }

    public static PriceIndex createBasisPriceIndex(
            PriceIndex hubIndex,
            String name,
            FrequencyCode frequencyCode,
            CurrencyCode currencyCode,
            UnitOfMeasureCode unitOfMeasureCode,
            PricingLocation pricingLocation) {

        PriceIndex priceIndex = new PriceIndex();
        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.setBaseIndexId(hubIndex.generateEntityId());

        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setDaysOffsetForExpiry(4);
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setDescription(name + "-Desc Basis");
        snapshot.getDetail().setIndexType(IndexType.BASIS);
        snapshot.getDetail().setCurrencyCode(currencyCode);
        snapshot.getDetail().setUnitOfMeasureCode(unitOfMeasureCode);
        snapshot.getDetail().setFrequencyCode(frequencyCode);

        snapshot.setPricingLocationId(pricingLocation.generateEntityId());

        priceIndex.createWith(snapshot);

        return priceIndex;

    }

    public static PriceIndex createPriceIndex(
            String name,
            FrequencyCode frequencyCode,
            CurrencyCode currencyCode,
            UnitOfMeasureCode unitOfMeasureCode,
            PricingLocation pricingLocation) {

        PriceIndex priceIndex = new PriceIndex();

        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setDaysOffsetForExpiry(4);
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setDescription(name + "-Desc");
        snapshot.getDetail().setIndexType(IndexType.HUB);
        snapshot.getDetail().setCurrencyCode(currencyCode);
        snapshot.getDetail().setUnitOfMeasureCode(unitOfMeasureCode);
        snapshot.getDetail().setFrequencyCode(frequencyCode);

        snapshot.setPricingLocationId(pricingLocation.generateEntityId());

        priceIndex.createWith(snapshot);

        return priceIndex;
    }


    public static PriceIndex createPriceIndex(
            String name,
            FrequencyCode frequencyCode,
            CurrencyCode currencyCode,
            PricingLocation pricingLocation) {

        PriceIndex priceIndex = new PriceIndex();

        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setDaysOffsetForExpiry(4);
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setDescription(name + "-Desc");
        snapshot.getDetail().setIndexType(IndexType.HUB);
        snapshot.getDetail().setCurrencyCode(currencyCode);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setFrequencyCode(frequencyCode);

        snapshot.setPricingLocationId(pricingLocation.generateEntityId());

        priceIndex.createWith(snapshot);

        return priceIndex;
    }

    public static PriceIndexSnapshot createPriceIndexSnapshot(
            String name,
            PricingLocation pricingLocation) {


        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setDaysOffsetForExpiry(4);
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setDescription(name + "-Desc");
        snapshot.getDetail().setCurrencyCode(CurrencyCode.USD);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setIndexType(IndexType.HUB);

        snapshot.setPricingLocationId(pricingLocation.generateEntityId());
        return snapshot;
    }

}
