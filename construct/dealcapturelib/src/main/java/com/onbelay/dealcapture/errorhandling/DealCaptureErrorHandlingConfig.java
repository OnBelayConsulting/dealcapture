package com.onbelay.dealcapture.errorhandling;

import com.onbelay.core.errorhandling.ErrorHandlingManager;
import com.onbelay.core.errorhandlingimpl.CoreErrorHandlingManager;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorErrorCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.EnumSet;

@Configuration
public class DealCaptureErrorHandlingConfig {

    @Bean
    @Primary
    public ErrorHandlingManager errorHandlingManager() {
        CoreErrorHandlingManager manager = new  CoreErrorHandlingManager();
        for (DealErrorCode c : EnumSet.allOf(DealErrorCode.class))
            manager.addErrorMessage(c.getCode(), c.getDescription());

        for (PricingErrorCode c : EnumSet.allOf(PricingErrorCode.class))
            manager.addErrorMessage(c.getCode(), c.getDescription());

        for (RiskFactorErrorCode c : EnumSet.allOf(RiskFactorErrorCode.class))
            manager.addErrorMessage(c.getCode(), c.getDescription());


        return manager;
    }

}
