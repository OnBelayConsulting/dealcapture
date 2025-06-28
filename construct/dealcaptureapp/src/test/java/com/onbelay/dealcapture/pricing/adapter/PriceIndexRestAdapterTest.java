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
package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.pricing.repository.PriceCurveRepository;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@WithMockUser(username="test")
public class PriceIndexRestAdapterTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(PriceIndexRestAdapterTest.class);
	
	@Autowired
	private PriceIndexRestAdapter priceIndexRestAdapter;

	@Autowired
	private PriceCurveRepository priceCurveRepository;

	private PricingLocation location;
	private PriceIndex priceIndex;
	private PriceIndex dailyPriceIndex;

	private FxIndex fxIndex;

	private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
	private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

	@Override
	public void setUp() {
		super.setUp();
		location = PricingLocationFixture.createPricingLocation("West");

		fxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.MONTHLY,
				CurrencyCode.USD,
				CurrencyCode.CAD);

		FxIndexFixture.generateDailyFxCurves(
				fxIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(10, 1, 1, 1, 1));

		priceIndex = PriceIndexFixture.createPriceIndex(
				"ACEE_M",
				FrequencyCode.MONTHLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				location);


		dailyPriceIndex = PriceIndexFixture.createPriceIndex(
				"ACEE_D",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				location);


		PriceIndexFixture.generateMonthlyPriceCurves(
				priceIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(2023, 10, 1, 0, 0));


		flush();
	}

	@Test
	public void createPrices() {
		PriceCurveSnapshot snapshot = new PriceCurveSnapshot();
		snapshot.getDetail().setCurveValue(BigDecimal.ONE);
		snapshot.getDetail().setCurveDate(LocalDate.of(2022, 1, 1));
		snapshot.getDetail().setObservedDateTime(LocalDateTime.now());
		snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

		TransactionResult result = priceIndexRestAdapter.savePriceCurves(
				dailyPriceIndex.getId(),
				List.of(snapshot));
		flush();
		DefinedQuery definedQuery = new DefinedQuery("PriceCurve");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression("priceIndexId", ExpressionOperator.EQUALS, dailyPriceIndex.getId()));

		List<Integer> ids  = priceCurveRepository.findPriceCurveIds(definedQuery);
		assertEquals(1, ids.size());
		PriceCurve curve = priceCurveRepository.load(new EntityId(ids.get(0)));
		assertEquals(LocalDate.of(2022, 1, 1), curve.getDetail().getCurveDate());
	}
	

}
