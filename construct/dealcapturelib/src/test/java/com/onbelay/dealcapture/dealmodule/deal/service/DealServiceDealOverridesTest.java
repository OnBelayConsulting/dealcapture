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
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonthFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.*;
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

public class DealServiceDealOverridesTest extends PhysicalDealServiceTestCase {


	private PhysicalDeal multiMonthDeal;
	private PhysicalDeal midMonthDeal;
	private PhysicalDeal partialMonthDeal;

	private PhysicalDeal dealWithCosts;

	private LocalDate threeMonthEndDate = LocalDate.of(2025, 5, 31);


	private LocalDate midMonthStartDate = LocalDate.of(2025, 1, 15);
	private LocalDate midMonthEndDate = LocalDate.of(2025, 2, 15);

	private LocalDate partialMonthStartDate = LocalDate.of(2025, 1, 15);
	private LocalDate partialMonthEndDate = LocalDate.of(2025, 1, 18);

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
				startDate,
				threeMonthEndDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"multiMonth",
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
		multiMonthDeal = PhysicalDeal.create(snapshot);



		snapshot = DealFixture.createPhysicalDealSnapshot(
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


		snapshot = DealFixture.createPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				partialMonthStartDate,
				partialMonthEndDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"partialMonth",
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
		partialMonthDeal = PhysicalDeal.create(snapshot);

	}

	@Test
	public void fetchEmptyDealOverrides() {
		DealOverrideSnapshot snapshot = dealService.fetchDealOverrides(multiMonthDeal.generateEntityId());
		assertEquals(17, snapshot.getOverrideMonths().size());
		DealOverrideMonthSnapshot monthSnapshot = snapshot.getOverrideMonths().get(0);
		assertEquals(LocalDate.of(2024,1,1), monthSnapshot.getMonthDate());
		assertEquals(31, monthSnapshot.getOverrideDays().size());
	}


	@Test
	public void fetchDealOverridesWithExistingOverrides() {
		DealDayByMonthFixture.createDayByMonthQuantity(
				multiMonthDeal,
				multiMonthDeal.getDealDetail().getStartDate(),
				2,
				BigDecimal.valueOf(30));
		DealDayByMonthFixture.createDayByMonthQuantity(
				multiMonthDeal,
				multiMonthDeal.getDealDetail().getStartDate().plusMonths(1),
				1,
				BigDecimal.valueOf(40));
		DealDayByMonthFixture.createDayByMonthPrice(
				multiMonthDeal,
				multiMonthDeal.getDealDetail().getStartDate(),
				1,
				BigDecimal.valueOf(1.44));
		flush();
		clearCache();

		DealOverrideSnapshot snapshot = dealService.fetchDealOverrides(multiMonthDeal.generateEntityId());
		assertEquals(17, snapshot.getOverrideMonths().size());
		DealOverrideMonthSnapshot monthSnapshot = snapshot.getOverrideMonths().get(0);
		assertEquals(LocalDate.of(2024,1,1), monthSnapshot.getMonthDate());
		assertEquals(31, monthSnapshot.getOverrideDays().size())
		;
		DealOverrideDaySnapshot daySnapshot = monthSnapshot.getOverrideDays().get(0);
		assertEquals(0, BigDecimal.valueOf(1.44).compareTo(daySnapshot.getValues().get(0)));

		DealOverrideDaySnapshot nextDaySnapshot = monthSnapshot.getOverrideDays().get(1);
		assertEquals(0, BigDecimal.valueOf(30).compareTo(nextDaySnapshot.getValues().get(1)));

		DealOverrideMonthSnapshot nextMonthSnapshot = snapshot.getOverrideMonths().get(1);
		DealOverrideDaySnapshot daySnapshotNextMonth = nextMonthSnapshot.getOverrideDays().get(0);
		assertEquals(0, BigDecimal.valueOf(40).compareTo(daySnapshotNextMonth.getValues().get(1)));
	}



	@Test
	public void fetchEmptyDealOverridesMidMonth() {
		DealOverrideSnapshot snapshot = dealService.fetchDealOverrides(midMonthDeal.generateEntityId());
		assertEquals(2, snapshot.getOverrideMonths().size());
		DealOverrideMonthSnapshot monthSnapshot = snapshot.getOverrideMonths().get(0);
		assertEquals(LocalDate.of(2025,1,1), monthSnapshot.getMonthDate());
		assertEquals(midMonthStartDate, monthSnapshot.getMonthStartDate());
		assertEquals(LocalDate.of(2025,1,31), monthSnapshot.getMonthEndDate());
		assertEquals(17, monthSnapshot.getOverrideDays().size());

		DealOverrideMonthSnapshot lastMonthSnapshot = snapshot.getOverrideMonths().get(1);
		assertEquals(LocalDate.of(2025,2,1), lastMonthSnapshot.getMonthDate());
		assertEquals(LocalDate.of(2025,2,1), lastMonthSnapshot.getMonthStartDate());
		assertEquals(midMonthEndDate, lastMonthSnapshot.getMonthEndDate());
		assertEquals(15, lastMonthSnapshot.getOverrideDays().size());
	}


	@Test
	public void fetchEmptyDealOverridesPartialMonth() {
		DealOverrideSnapshot snapshot = dealService.fetchDealOverrides(partialMonthDeal.generateEntityId());
		assertEquals(1, snapshot.getOverrideMonths().size());
		DealOverrideMonthSnapshot monthSnapshot = snapshot.getOverrideMonths().get(0);
		assertEquals(LocalDate.of(2025,1,1), monthSnapshot.getMonthDate());
		assertEquals(partialMonthStartDate, monthSnapshot.getMonthStartDate());
		assertEquals(partialMonthEndDate, monthSnapshot.getMonthEndDate());
		assertEquals(4, monthSnapshot.getOverrideDays().size());

	}



	@Test
	public void createDealOverridesPriceOverride() {
		EntityId entityId = fixedPriceBuyDeal.generateEntityId();
		DealOverrideSnapshot dealOverrideSnapshot = new DealOverrideSnapshot();
		dealOverrideSnapshot.setEntityId(entityId);
		dealOverrideSnapshot.setEntityState(EntityState.NEW);
		dealOverrideSnapshot.addPriceHeading();

		DealOverrideMonthSnapshot overrideMonthSnapshot = new DealOverrideMonthSnapshot();
		dealOverrideSnapshot.addOverrideMonth(overrideMonthSnapshot);
		overrideMonthSnapshot.setMonthDate(fixedPriceBuyDeal.getDealDetail().getStartDate());
		DealOverrideDaySnapshot dayOverrideSnapshot = new DealOverrideDaySnapshot();
		dayOverrideSnapshot.setOverrideDateWithDayOfMonth(
				overrideMonthSnapshot.getMonthDate(),
				1);
		dayOverrideSnapshot.initializeValuesSize(dealOverrideSnapshot.getHeadings().size());

		dayOverrideSnapshot.setDayValue(0, BigDecimal.valueOf(2.45));
		overrideMonthSnapshot.addDayOverride(dayOverrideSnapshot);

		dealService.saveDealOverrides(dealOverrideSnapshot);
		flush();
		clearCache();
		fixedPriceBuyDeal = (PhysicalDeal) dealRepository.load(fixedPriceBuyDeal.generateEntityId());
		List<DealDayByMonthSnapshot> overrides = dealService.fetchDealDayByMonths(fixedPriceBuyDeal.generateEntityId());
		assertEquals(1, overrides.size());
		DealDayByMonthSnapshot snap = overrides.get(0);
		assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), snap.getDetail().getDealMonthDate());
		assertEquals(0, BigDecimal.valueOf(2.45).compareTo(snap.getDetail().getDay1Value()));
	}



	@Test
	public void createDealOverridesWithPricesQuantitiesAndCosts() {
		EntityId entityId = dealWithCosts.generateEntityId();
		DealOverrideSnapshot dealOverrideSnapshot = new DealOverrideSnapshot();
		dealOverrideSnapshot.setEntityId(entityId);
		dealOverrideSnapshot.setEntityState(EntityState.NEW);
		dealOverrideSnapshot.addPriceHeading();
		dealOverrideSnapshot.addCostHeading(CostNameCode.BROKERAGE_DAILY_FEE.getCode());
		dealOverrideSnapshot.addCostHeading(CostNameCode.FACILITY_PER_UNIT_FEE.getCode());

		DealOverrideMonthSnapshot overrideMonthSnapshot = new DealOverrideMonthSnapshot();
		dealOverrideSnapshot.addOverrideMonth(overrideMonthSnapshot);
		overrideMonthSnapshot.setMonthDate(dealWithCosts.getDealDetail().getStartDate());
		DealOverrideDaySnapshot dayOverrideSnapshot = new DealOverrideDaySnapshot();
		dayOverrideSnapshot.setOverrideDateWithDayOfMonth(
				overrideMonthSnapshot.getMonthDate(),
				1);
		dayOverrideSnapshot.initializeValuesSize(dealOverrideSnapshot.getHeadings().size());

		dayOverrideSnapshot.setDayValue(0, BigDecimal.valueOf(2.45));
		dayOverrideSnapshot.setDayValue(1, BigDecimal.valueOf(30));
		dayOverrideSnapshot.setDayValue(2, BigDecimal.valueOf(5.22));
		overrideMonthSnapshot.addDayOverride(dayOverrideSnapshot);

		dealService.saveDealOverrides(dealOverrideSnapshot);
		flush();
		clearCache();
		dealWithCosts = (PhysicalDeal) dealRepository.load(dealWithCosts.generateEntityId());
		List<DealDayByMonthSnapshot> overrides = dealService.fetchDealDayByMonths(dealWithCosts.generateEntityId());
		assertEquals(3, overrides.size());
		DealDayByMonthSnapshot priceOver = overrides
				.stream()
				.filter( c-> c.getDetail().getDealDayTypeCode() == DayTypeCode.PRICE)
				.findFirst().get();
		assertEquals(dealWithCosts.getDealDetail().getStartDate(), priceOver.getDetail().getDealMonthDate());
		assertEquals(0, BigDecimal.valueOf(2.45).compareTo(priceOver.getDetail().getDay1Value()));

		DealDayByMonthSnapshot brokerageFeeOver = overrides
				.stream().filter( c-> c.getDetail().getDealDayTypeCode() == DayTypeCode.COST
						&& c.getDetail().getDaySubTypeCodeValue().equalsIgnoreCase(CostNameCode.BROKERAGE_DAILY_FEE.getCode()))
				.findFirst().get();
		assertEquals(dealWithCosts.getDealDetail().getStartDate(), brokerageFeeOver.getDetail().getDealMonthDate());
		assertEquals(0, BigDecimal.valueOf(30).compareTo(brokerageFeeOver.getDetail().getDay1Value()));

		DealDayByMonthSnapshot facilityFeeOver = overrides
				.stream().filter( c-> c.getDetail().getDealDayTypeCode() == DayTypeCode.COST
						&& c.getDetail().getDaySubTypeCodeValue().equalsIgnoreCase(CostNameCode.FACILITY_PER_UNIT_FEE.getCode()))
				.findFirst().get();
		assertEquals(dealWithCosts.getDealDetail().getStartDate(), facilityFeeOver.getDetail().getDealMonthDate());
		assertEquals(0, BigDecimal.valueOf(5.22).compareTo(facilityFeeOver.getDetail().getDay1Value()));
	}




	@Test
	public void createMultipleDealOverridesPriceAndQuantities() {
		EntityId entityId = fixedPriceBuyDeal.generateEntityId();
		DealOverrideSnapshot dealOverrideSnapshot = new DealOverrideSnapshot();
		dealOverrideSnapshot.setEntityId(entityId);
		dealOverrideSnapshot.setEntityState(EntityState.NEW);
		dealOverrideSnapshot.addPriceHeading();
		dealOverrideSnapshot.addQuantityHeading();

		// First month (both price and quantity overrides)
		LocalDate firstMonthDate = fixedPriceBuyDeal.getDealDetail().getStartDate();
		DealOverrideMonthSnapshot overrideMonthSnapshot = new DealOverrideMonthSnapshot();
		dealOverrideSnapshot.addOverrideMonth(overrideMonthSnapshot);
		overrideMonthSnapshot.setMonthDate(firstMonthDate);

		DealOverrideDaySnapshot dayOverrideSnapshot = new DealOverrideDaySnapshot();
		dayOverrideSnapshot.setOverrideDateWithDayOfMonth(firstMonthDate, 1);
		dayOverrideSnapshot.initializeValuesSize(dealOverrideSnapshot.getHeadings().size());
		dayOverrideSnapshot.setDayValue(0, BigDecimal.valueOf(2.45));
		dayOverrideSnapshot.setDayValue(1, BigDecimal.valueOf(100));
		overrideMonthSnapshot.addDayOverride(dayOverrideSnapshot);

		dayOverrideSnapshot = new DealOverrideDaySnapshot();
		dayOverrideSnapshot.setOverrideDateWithDayOfMonth(firstMonthDate, 2);
		dayOverrideSnapshot.initializeValuesSize(dealOverrideSnapshot.getHeadings().size());
		dayOverrideSnapshot.setDayValue(0, BigDecimal.valueOf(5.30));
		dayOverrideSnapshot.setDayValue(1, BigDecimal.valueOf(200));
		overrideMonthSnapshot.addDayOverride(dayOverrideSnapshot);

		// second Month (Only price override)
		LocalDate secondMonthDate = fixedPriceBuyDeal.getDealDetail().getStartDate().plusMonths(1);
		overrideMonthSnapshot = new DealOverrideMonthSnapshot();
		dealOverrideSnapshot.addOverrideMonth(overrideMonthSnapshot);

		overrideMonthSnapshot.setMonthDate(secondMonthDate);

		dayOverrideSnapshot = new DealOverrideDaySnapshot();
		dayOverrideSnapshot.setOverrideDateWithDayOfMonth(secondMonthDate, 5);
		dayOverrideSnapshot.initializeValuesSize(dealOverrideSnapshot.getHeadings().size());
		dayOverrideSnapshot.setDayValue(0, BigDecimal.valueOf(7.45));
		overrideMonthSnapshot.addDayOverride(dayOverrideSnapshot);

		dayOverrideSnapshot = new DealOverrideDaySnapshot();
		dayOverrideSnapshot.setOverrideDateWithDayOfMonth(secondMonthDate, 9);
		dayOverrideSnapshot.initializeValuesSize(dealOverrideSnapshot.getHeadings().size());
		dayOverrideSnapshot.setDayValue(0, BigDecimal.valueOf(8.30));
		overrideMonthSnapshot.addDayOverride(dayOverrideSnapshot);


		dealService.saveDealOverrides(dealOverrideSnapshot);
		flush();
		clearCache();
		fixedPriceBuyDeal = (PhysicalDeal) dealRepository.load(fixedPriceBuyDeal.generateEntityId());
		List<DealDayByMonthSnapshot> overrides = dealService.fetchDealDayByMonths(fixedPriceBuyDeal.generateEntityId());
		assertEquals(3, overrides.size());
		DealDayByMonthSnapshot priceOverride = overrides
				.stream()
				.filter(c -> c.getDetail().getDealDayTypeCode() == DayTypeCode.PRICE
						&& c.getDetail().getDealMonthDate().equals(firstMonthDate))
				.findFirst().get();

		assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), priceOverride.getDetail().getDealMonthDate());
		assertEquals(0, BigDecimal.valueOf(2.45).compareTo(priceOverride.getDetail().getDay1Value()));
		assertEquals(0, BigDecimal.valueOf(5.30).compareTo(priceOverride.getDetail().getDay2Value()));

		DealDayByMonthSnapshot quantityOverride = overrides
				.stream()
				.filter(c -> c.getDetail().getDealDayTypeCode() == DayTypeCode.QUANTITY
					&& c.getDetail().getDealMonthDate().equals(firstMonthDate))
				.findFirst().get();

		assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), quantityOverride.getDetail().getDealMonthDate());
		assertEquals(0, BigDecimal.valueOf(100).compareTo(quantityOverride.getDetail().getDay1Value()));
		assertEquals(0, BigDecimal.valueOf(200).compareTo(quantityOverride.getDetail().getDay2Value()));


		DealDayByMonthSnapshot secondMonthPriceOverride = overrides
				.stream()
				.filter(c -> c.getDetail().getDealDayTypeCode() == DayTypeCode.PRICE
						&& c.getDetail().getDealMonthDate().equals(secondMonthDate))
				.findFirst().get();

		assertEquals(secondMonthDate, secondMonthPriceOverride.getDetail().getDealMonthDate());
		assertEquals(0, BigDecimal.valueOf(7.45).compareTo(secondMonthPriceOverride.getDetail().getDay5Value()));
		assertEquals(0, BigDecimal.valueOf(8.30).compareTo(secondMonthPriceOverride.getDetail().getDay9Value()));
	}


	@Test
	public void saveDealOverridesWithExistingOverrides() {
		DealDayByMonthFixture.createDayByMonthQuantity(
				multiMonthDeal,
				multiMonthDeal.getDealDetail().getStartDate(),
				2,
				BigDecimal.valueOf(30));
		DealDayByMonthFixture.createDayByMonthQuantity(
				multiMonthDeal,
				multiMonthDeal.getDealDetail().getStartDate().plusMonths(1),
				1,
				BigDecimal.valueOf(40));
		DealDayByMonthFixture.createDayByMonthPrice(
				multiMonthDeal,
				multiMonthDeal.getDealDetail().getStartDate(),
				1,
				BigDecimal.valueOf(1.44));
		flush();
		clearCache();
		DealOverrideSnapshot dealOverrideSnapshot = new DealOverrideSnapshot();
		dealOverrideSnapshot.setEntityId(multiMonthDeal.generateEntityId());
		dealOverrideSnapshot.setEntityState(EntityState.NEW);
		dealOverrideSnapshot.addPriceHeading();
		dealOverrideSnapshot.addQuantityHeading();

		// First month (both price and quantity overrides)
		LocalDate firstMonthDate = multiMonthDeal.getDealDetail().getStartDate();
		DealOverrideMonthSnapshot overrideMonthSnapshot = new DealOverrideMonthSnapshot();
		dealOverrideSnapshot.addOverrideMonth(overrideMonthSnapshot);
		overrideMonthSnapshot.setMonthDate(firstMonthDate);


		DealOverrideDaySnapshot dayOverrideSnapshot = new DealOverrideDaySnapshot();
		dayOverrideSnapshot.setOverrideDateWithDayOfMonth(firstMonthDate, 1);
		dayOverrideSnapshot.initializeValuesSize(dealOverrideSnapshot.getHeadings().size());
		dayOverrideSnapshot.setDayValue(0, BigDecimal.valueOf(1.44));
		dayOverrideSnapshot.setDayValue(1, BigDecimal.valueOf(100));
		overrideMonthSnapshot.addDayOverride(dayOverrideSnapshot);



		dayOverrideSnapshot = new DealOverrideDaySnapshot();
		dayOverrideSnapshot.setOverrideDateWithDayOfMonth(firstMonthDate, 2);
		dayOverrideSnapshot.initializeValuesSize(dealOverrideSnapshot.getHeadings().size());
		dayOverrideSnapshot.setDayValue(0, BigDecimal.valueOf(2.45));
		dayOverrideSnapshot.setDayValue(1, BigDecimal.valueOf(30));
		overrideMonthSnapshot.addDayOverride(dayOverrideSnapshot);

		dealService.saveDealOverrides(dealOverrideSnapshot);
		flush();
		clearCache();

		List<DealDayByMonthSnapshot> dd = dealService.fetchDealDayByMonths(multiMonthDeal.generateEntityId());

		multiMonthDeal = (PhysicalDeal) dealRepository.load(multiMonthDeal.generateEntityId());
		DealOverrideSnapshot dealOverrideSnapshotUpdated = dealService.fetchDealOverrides(multiMonthDeal.generateEntityId());
		DealOverrideMonthSnapshot firstMonth = dealOverrideSnapshotUpdated.getOverrideMonths().get(0);
		DealOverrideDaySnapshot firstDay = firstMonth.getOverrideDays().get(0);
		assertEquals(0, BigDecimal.valueOf(1.44).compareTo(firstDay.getValues().get(0)));
		assertEquals(0, BigDecimal.valueOf(100).compareTo(firstDay.getValues().get(1)));

		DealOverrideDaySnapshot secondDay = firstMonth.getOverrideDays().get(1);
		assertEquals(0, BigDecimal.valueOf(2.45).compareTo(secondDay.getValues().get(0)));
		assertEquals(0, BigDecimal.valueOf(30).compareTo(secondDay.getValues().get(1)));
	}
}
