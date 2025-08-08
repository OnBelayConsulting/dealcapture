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
package com.onbelay.dealcapture.dealmodule.deal.service;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealCostFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideHourSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideHoursForDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DealServiceDealHourlyOverridesTest extends PhysicalDealServiceTestCase {


	private PhysicalDeal midMonthDeal;

	private PhysicalDeal dealWithCosts;

	private LocalDate midMonthStartDate = LocalDate.of(2025, 1, 15);
	private LocalDate midMonthEndDate = LocalDate.of(2025, 2, 15);


	@Override
	public void setUp() {
		super.setUp();
		dealWithCosts = DealFixture.createSamplePhysicalDeal(
				myBusinessContact,
				CommodityCode.CRUDE,
				"myDeal",
				companyRole,
				counterpartyRole,
				marketIndex);
		flush();
		DealCostFixture.createPerUnitCost(
				dealWithCosts,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				CostNameCode.FACILITY_PER_UNIT_FEE,
				BigDecimal.TEN);
		DealCostFixture.createPerUnitCost(
				dealWithCosts,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				CostNameCode.BROKERAGE_DAILY_FEE,
				BigDecimal.TEN);
		flush();





		PhysicalDealSnapshot snapshot = DealFixture.createPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				midMonthStartDate,
				midMonthEndDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"midMonth",
				companyRole,
				counterpartyRole,
				marketIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));

		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		midMonthDeal = PhysicalDeal.create(snapshot);


	}

	@Test
	public void fetchEmptyHourlyDealOverrides() {
		DealOverrideHoursForDaySnapshot snapshot = dealService.fetchHourlyDealOverrides(
				midMonthDeal.generateEntityId(),
				midMonthStartDate);

		assertNotNull(snapshot.getDayDate());
		assertEquals(2, snapshot.getHeadings().size());
		assertEquals(24, snapshot.getOverrideHours().size());
	}


	@Test
	public void createDealOverridesPriceOverride() {
		EntityId entityId = fixedPriceBuyDeal.generateEntityId();
		DealOverrideHoursForDaySnapshot dealOverrideSnapshot = new DealOverrideHoursForDaySnapshot();
		dealOverrideSnapshot.setDayDate(fixedPriceBuyDeal.getDealDetail().getStartDate());
		dealOverrideSnapshot.setEntityId(entityId);
		dealOverrideSnapshot.setEntityState(EntityState.NEW);
		dealOverrideSnapshot.addPriceHeading();

		DealOverrideHourSnapshot hourOverrideSnapshot = new DealOverrideHourSnapshot(
				dealOverrideSnapshot.getDayDate(),
				2,
				1);

		hourOverrideSnapshot.setValueAt(0, BigDecimal.valueOf(2.45));

		dealOverrideSnapshot.getOverrideHours().add(hourOverrideSnapshot);

		dealService.saveHourlyDealOverrides(
				fixedPriceBuyDeal.generateEntityId(),
				dealOverrideSnapshot);
		flush();
		clearCache();
		fixedPriceBuyDeal = (PhysicalDeal) dealRepository.load(fixedPriceBuyDeal.generateEntityId());
		List<DealHourByDaySnapshot> overrides = dealService.fetchDealHourByDays(fixedPriceBuyDeal.generateEntityId());
		assertEquals(1, overrides.size());
		DealHourByDaySnapshot snap = overrides.get(0);
		assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), snap.getDetail().getDealDayDate());
		assertEquals(0, BigDecimal.valueOf(2.45).compareTo(snap.getDetail().getHour2Value()));

		DealOverrideHoursForDaySnapshot dealOverrideHoursForDaySnapshot = dealService.fetchHourlyDealOverrides(
				fixedPriceBuyDeal.generateEntityId(),
				fixedPriceBuyDeal.getDealDetail().getStartDate());
		assertEquals(24, dealOverrideHoursForDaySnapshot.getOverrideHours().size());
		DealOverrideHourSnapshot secondHour = dealOverrideHoursForDaySnapshot.getOverrideHours().get(1);
		assertEquals(0, BigDecimal.valueOf(2.45).compareTo(secondHour.getValues().get(0)));
	}



	@Test
	public void createDealOverridesMultipleOverrides() {
		EntityId entityId = dealWithCosts.generateEntityId();
		DealOverrideHoursForDaySnapshot dealOverrideSnapshot = new DealOverrideHoursForDaySnapshot();
		dealOverrideSnapshot.setDayDate(dealWithCosts.getDealDetail().getStartDate());
		dealOverrideSnapshot.setEntityId(entityId);
		dealOverrideSnapshot.setEntityState(EntityState.NEW);
		dealOverrideSnapshot.addPriceHeading();
		dealOverrideSnapshot.addQuantityHeading();
		dealOverrideSnapshot.addCostHeadings(dealWithCosts.fetchCostNames());
		dealOverrideSnapshot.createHourOverrides();


		// Second Hour
		DealOverrideHourSnapshot hourOverrideSnapshot = dealOverrideSnapshot.getOverrideHours().get(1);

		hourOverrideSnapshot.setValueAt(0, BigDecimal.valueOf(2.45));
		hourOverrideSnapshot.setValueAt(1, BigDecimal.valueOf(100));
		hourOverrideSnapshot.setValueAt(2, BigDecimal.valueOf(6.22));
		hourOverrideSnapshot.setValueAt(3, BigDecimal.valueOf(45.00));

		dealService.saveHourlyDealOverrides(
				dealWithCosts.generateEntityId(),
				dealOverrideSnapshot);
		flush();
		clearCache();
		dealWithCosts = (PhysicalDeal) dealRepository.load(dealWithCosts.generateEntityId());
		List<DealHourByDaySnapshot> overrides = dealService.fetchDealHourByDays(dealWithCosts.generateEntityId());
		assertEquals(4, overrides.size());

		DealHourByDaySnapshot priceOverride = overrides
				.stream()
				.filter(c-> c.getDetail().getDealDayTypeCode() == DayTypeCode.PRICE).findFirst().get();

		assertEquals(dealWithCosts.getDealDetail().getStartDate(), priceOverride.getDetail().getDealDayDate());
		assertEquals(0, BigDecimal.valueOf(2.45).compareTo(priceOverride.getDetail().getHour2Value()));


		DealHourByDaySnapshot quantityOverride = overrides
				.stream()
				.filter(c-> c.getDetail().getDealDayTypeCode() == DayTypeCode.QUANTITY).findFirst().get();

		assertEquals(dealWithCosts.getDealDetail().getStartDate(), quantityOverride.getDetail().getDealDayDate());
		assertEquals(0, BigDecimal.valueOf(100).compareTo(quantityOverride.getDetail().getHour2Value()));


		DealHourByDaySnapshot costBrokerageFeeOverride = overrides
				.stream()
				.filter(c-> c.getDetail().getDealDayTypeCode() == DayTypeCode.COST &&
						c.getDetail().getDaySubTypeCodeValue().equals(CostNameCode.BROKERAGE_DAILY_FEE.getCode()))
				.findFirst().get();

		assertEquals(dealWithCosts.getDealDetail().getStartDate(), costBrokerageFeeOverride.getDetail().getDealDayDate());
		assertEquals(0, BigDecimal.valueOf(6.22).compareTo(costBrokerageFeeOverride.getDetail().getHour2Value()));


		DealHourByDaySnapshot costFacilityFeeOverride = overrides
				.stream()
				.filter(c-> c.getDetail().getDealDayTypeCode() == DayTypeCode.COST &&
						c.getDetail().getDaySubTypeCodeValue().equals(CostNameCode.FACILITY_PER_UNIT_FEE.getCode()))
				.findFirst().get();

		assertEquals(dealWithCosts.getDealDetail().getStartDate(), costFacilityFeeOverride.getDetail().getDealDayDate());
		assertEquals(0, BigDecimal.valueOf(45).compareTo(costFacilityFeeOverride.getDetail().getHour2Value()));


		DealOverrideHoursForDaySnapshot dealOverrideHoursForDaySnapshot = dealService.fetchHourlyDealOverrides(
				dealWithCosts.generateEntityId(),
				dealWithCosts.getDealDetail().getStartDate());
		assertEquals(24, dealOverrideHoursForDaySnapshot.getOverrideHours().size());
		DealOverrideHourSnapshot secondHour = dealOverrideHoursForDaySnapshot.getOverrideHours().get(1);
		assertEquals(0, BigDecimal.valueOf(2.45).compareTo(secondHour.getValues().get(0)));
		assertEquals(0, BigDecimal.valueOf(100).compareTo(secondHour.getValues().get(1)));
		assertEquals(0, BigDecimal.valueOf(6.22).compareTo(secondHour.getValues().get(2)));
		assertEquals(0, BigDecimal.valueOf(45).compareTo(secondHour.getValues().get(3)));

	}


}
