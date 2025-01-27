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
import com.onbelay.dealcapture.pricing.model.InterestCurve;
import com.onbelay.dealcapture.pricing.model.InterestIndex;
import com.onbelay.dealcapture.pricing.model.InterestIndexFixture;
import com.onbelay.dealcapture.pricing.repository.InterestCurveRepository;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.FrequencyCode;
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
public class InterestIndexRestAdapterTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(InterestIndexRestAdapterTest.class);
	
	@Autowired
	private InterestIndexRestAdapter interestIndexRestAdapter;

	@Autowired
	private InterestCurveRepository interestCurveRepository;

	private InterestIndex interestIndex;
	private InterestIndex dailyInterestIndex;

	private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
	private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

	@Override
	public void setUp() {
		super.setUp();

		interestIndex = InterestIndexFixture.createInterestIndex(
				"INTM",
				FrequencyCode.MONTHLY);


		dailyInterestIndex = InterestIndexFixture.createInterestIndex(
				"INTD",
				FrequencyCode.DAILY);


		InterestIndexFixture.generateMonthlyInterestCurves(
				interestIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(2023, 10, 1, 0, 0));


		flush();
	}

	@Test
	public void createInterestCurves() {
		InterestCurveSnapshot snapshot = new InterestCurveSnapshot();
		snapshot.getDetail().setCurveValue(BigDecimal.ONE);
		snapshot.getDetail().setCurveDate(LocalDate.of(2022, 1, 1));
		snapshot.getDetail().setObservedDateTime(LocalDateTime.now());
		snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

		TransactionResult result = interestIndexRestAdapter.saveInterestCurves(
				dailyInterestIndex.getId(),
				List.of(snapshot));
		flush();
		DefinedQuery definedQuery = new DefinedQuery("InterestCurve");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression("interestIndexId", ExpressionOperator.EQUALS, dailyInterestIndex.getId()));

		List<Integer> ids  = interestCurveRepository.findInterestCurveIds(definedQuery);
		assertEquals(1, ids.size());
		InterestCurve curve = interestCurveRepository.load(new EntityId(ids.get(0)));
		assertEquals(LocalDate.of(2022, 1, 1), curve.getDetail().getCurveDate());
	}
	

}
