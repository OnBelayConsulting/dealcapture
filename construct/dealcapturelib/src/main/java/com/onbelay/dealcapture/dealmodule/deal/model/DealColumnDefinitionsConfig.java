package com.onbelay.dealcapture.dealmodule.deal.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DealColumnDefinitionsConfig {

    @Bean
    public DealColumnDefinitions dealColumnDefinitions() {
        return new DealColumnDefinitions();
    }

    @Bean
    public PhysicalDealColumnDefinitions physicalDealColumnDefinitions() {
        return new PhysicalDealColumnDefinitions();
    }

    @Bean
    public FinancialSwapDealColumnDefinitions financialSwapDealColumnDefinitions() {
        return new FinancialSwapDealColumnDefinitions();
    }

    @Bean
    public VanillaOptionDealColumnDefinitions vanillaOptionDealColumnDefinitions() {
        return new VanillaOptionDealColumnDefinitions();
    }

    @Bean
    public DealColumnDefinitionsMap dealColumnDefinitionsMap() {
        DealColumnDefinitionsMap map = new DealColumnDefinitionsMap();
        map.putColumnDefinitions("BaseDeal", dealColumnDefinitions());
        map.putColumnDefinitions("PhysicalDeal", physicalDealColumnDefinitions());
        map.putColumnDefinitions("FinancialSwapDeal", financialSwapDealColumnDefinitions());
        map.putColumnDefinitions("VanillaOptionDeal", vanillaOptionDealColumnDefinitions());
        return map;
    }
}
