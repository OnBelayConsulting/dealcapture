package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.dealcapture.job.enums.JobTypeCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DealJobFactoryConfig {

    @Bean
    public DealJobRunner dealPositionGenerationJobRunner() {
        DealJobRunner runner = new DealPositionGenerationJobRunnerBean();
        return runner;
    }
    @Bean
    public DealJobRunner dealPositionValuationJobRunner() {
        DealJobRunner runner = new DealPositionValuationJobRunnerBean();
        return runner;
    }
    @Bean
    public DealJobRunner powerProfilePositionGenerationJobRunner() {
        DealJobRunner runner = new PowerProfilePositionGenerationJobRunnerBean();
        return runner;
    }

    @Bean
    public DealJobRunner powerProfilePositionValuationJobRunner() {
        DealJobRunner runner = new PowerProfilePositionValuationJobRunnerBean();
        return runner;
    }

    @Bean
    public DealJobRunner priceRiskFactorValuationJobRunner() {
        return new PriceRiskFactorValuationJobRunnerBean();
    }

    @Bean
    public DealJobRunnerFactory dealJobRunnerFactory() {
        DealJobRunnerFactoryBean factory = new DealJobRunnerFactoryBean();
        factory.addRunner(JobTypeCode.DEAL_POS_GENERATION, dealPositionGenerationJobRunner());
        factory.addRunner(JobTypeCode.DEAL_POS_VALUATION, dealPositionValuationJobRunner());
        factory.addRunner(JobTypeCode.PWR_PROFILE_POS_GENERATION, powerProfilePositionGenerationJobRunner());
        factory.addRunner(JobTypeCode.PWR_PROFILE_POS_VALUATION, powerProfilePositionValuationJobRunner());
        factory.addRunner(JobTypeCode.PRICE_RF_VALUATION, priceRiskFactorValuationJobRunner());
        return factory;
    }

}
