package com.onbelay.dealcapture.dealmodule.positions.adapter;

import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@WithMockUser
public class DealPositionRestTestCase extends DealCaptureAppSpringTestCase {

    @Autowired
    protected FxRiskFactorService fxRiskFactorService;

    @Autowired
    protected PriceRiskFactorService priceRiskFactorService;

    @Autowired
    protected DealService dealService;

    @Autowired
    protected DealPositionService dealPositionService;

    @Autowired
    protected GeneratePositionsService generatePositionsService;

    protected PricingLocation pricingLocation;
    protected PriceIndex priceIndex;
    protected PriceRiskFactor priceRiskFactor;

    protected InterestIndex interestIndex;

    protected CompanyRole companyRole;
    protected CounterpartyRole counterpartyRole;

    protected FxIndex fxIndex;

    protected FxRiskFactor fxRiskFactor;

    protected LocalDateTime createdDateTime = LocalDateTime.of(2023, 1, 1, 1, 0);
    protected LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    protected LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

    protected LocalDate startDate = LocalDate.of(2023, 1, 1);
    protected LocalDate endDate = LocalDate.of(2023, 1, 31);


    @Override
    public void setUp() {
        super.setUp();
        companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
        counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);
        pricingLocation = PricingLocationFixture.createPricingLocation("West");
        interestIndex = InterestIndexFixture.createInterestIndex("RATE", true, FrequencyCode.DAILY);
        flush();
        LocalDateTime observedDateTime = LocalDateTime.of(2023, 12, 1, 1, 43);

        InterestIndexFixture.generateDailyInterestCurves(
                interestIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(0.12),
                observedDateTime);


        fxIndex = FxIndexFixture.createFxIndex(
                FrequencyCode.DAILY,
                CurrencyCode.USD,
                CurrencyCode.CAD);

        FxIndexFixture.generateDailyFxCurves(
                fxIndex,
                fromMarketDate,
                toMarketDate,
                LocalDateTime.of(2023, 11, 1, 1, 1));

        fxRiskFactor = FxRiskFactorFixture.createFxRiskFactor(fxIndex, fromMarketDate);

        priceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                pricingLocation);

        PriceIndexFixture.generateMonthlyPriceCurves(
                priceIndex,
                fromMarketDate,
                toMarketDate,
                LocalDateTime.of(2023, 10, 1, 0, 0));

        priceRiskFactor = PriceRiskFactorFixture.createPriceRiskFactor(
                priceIndex,
                fromMarketDate);

        flush();
    }

}
