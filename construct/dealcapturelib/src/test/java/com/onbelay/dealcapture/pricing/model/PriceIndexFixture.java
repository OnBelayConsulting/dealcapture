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

import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.shared.enums.CurrencyCode;

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
        snapshot.getDetail().setCurrencyCode(CurrencyCode.US);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

        snapshot.setPricingLocationId(pricingLocation.generateEntityId());

        priceIndex.createWith(snapshot);

        return priceIndex;
    }


    public static PriceIndex createPriceIndex(
            String name,
            FrequencyCode frequencyCode,
            PricingLocation pricingLocation) {

        PriceIndex priceIndex = new PriceIndex();

        PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setDaysOffsetForExpiry(4);
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setDescription(name + "-Desc");
        snapshot.getDetail().setIndexType(IndexType.HUB);
        snapshot.getDetail().setCurrencyCode(CurrencyCode.US);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
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
        snapshot.getDetail().setCurrencyCode(CurrencyCode.US);
        snapshot.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setIndexType(IndexType.HUB);

        snapshot.setPricingLocationId(pricingLocation.generateEntityId());
        return snapshot;
    }

}
