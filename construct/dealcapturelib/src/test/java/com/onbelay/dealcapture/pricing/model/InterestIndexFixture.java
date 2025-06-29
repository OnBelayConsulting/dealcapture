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

import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;
import com.onbelay.shared.enums.FrequencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InterestIndexFixture {

    public static List<Integer> generateMonthlyInterestCurves(
            InterestIndex interestIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            LocalDateTime observedDateTime) {

        return generateMonthlyInterestCurves(
                interestIndex,
                startMarketDate,
                endMarketDate,
                BigDecimal.TEN,
                observedDateTime);
    }

    public static List<Integer> generateMonthlyInterestCurves(
            InterestIndex interestIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            BigDecimal value,
            LocalDateTime observedDateTime) {

        LocalDate currentDate = startMarketDate.withDayOfMonth(1);
        ArrayList<InterestCurveSnapshot> interests = new ArrayList<>();
        while (currentDate.isAfter(endMarketDate) == false) {
            InterestCurveSnapshot curveSnapshot = new InterestCurveSnapshot();
            curveSnapshot.getDetail().setCurveDate(currentDate);
            curveSnapshot.getDetail().setCurveValue(value);
            curveSnapshot.getDetail().setObservedDateTime(observedDateTime);
            curveSnapshot.getDetail().setFrequencyCode(FrequencyCode.MONTHLY);
            interests.add(curveSnapshot);

            currentDate = currentDate.plusMonths(1);
        }
        return interestIndex.saveInterestCurves(interests);
    }

    public static List<Integer> generateDailyInterestCurves(
            InterestIndex interestIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            LocalDateTime observedDateTime) {

            return generateDailyInterestCurves(
                    interestIndex,
                    startMarketDate,
                    endMarketDate,
                    BigDecimal.ONE,
                    observedDateTime);
    }

        public static List<Integer> generateDailyInterestCurves(
            InterestIndex interestIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            BigDecimal interestValue,
            LocalDateTime observedDateTime) {

        LocalDate currentDate = startMarketDate;
        ArrayList<InterestCurveSnapshot> interests = new ArrayList<>();
        while (currentDate.isAfter(endMarketDate) == false) {
            InterestCurveSnapshot curveSnapshot = new InterestCurveSnapshot();
            curveSnapshot.getDetail().setCurveDate(currentDate);
            curveSnapshot.getDetail().setCurveValue(interestValue);
            curveSnapshot.getDetail().setObservedDateTime(observedDateTime);
            curveSnapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
            interests.add(curveSnapshot);

            currentDate = currentDate.plusDays(1);
        }
        return interestIndex.saveInterestCurves(interests);
    }

    public static InterestIndex createInterestIndex(
            String name,
            FrequencyCode frequencyCode) {

        InterestIndex interestIndex = new InterestIndex();

        InterestIndexSnapshot snapshot = new InterestIndexSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setDescription(name + "-Desc");
        snapshot.getDetail().setFrequencyCode(frequencyCode);

        interestIndex.createWith(snapshot);

        return interestIndex;
    }


    public static InterestIndex createInterestIndex(
            String name,
            boolean isRiskFreeRate,
            FrequencyCode frequencyCode) {

        InterestIndex interestIndex = new InterestIndex();

        InterestIndexSnapshot snapshot = new InterestIndexSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setIsRiskFreeRate(isRiskFreeRate);
        snapshot.getDetail().setDescription(name + "-Desc");
        snapshot.getDetail().setFrequencyCode(frequencyCode);

        interestIndex.createWith(snapshot);

        return interestIndex;
    }


    public static InterestIndexSnapshot createInterestIndexSnapshot(String name) {


        InterestIndexSnapshot snapshot = new InterestIndexSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setName(name);
        snapshot.getDetail().setDescription(name + "-Desc");
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        return snapshot;
    }

}
