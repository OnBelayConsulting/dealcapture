package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DealPositionsBatchInserterConfig {

    @Value("${batchInserterIsSqlServer:false}")
    private boolean isSqlServer;

    @Bean
    public DealPositionsBatchInserter getDealPositionsBatchInserter() {
        if (isSqlServer)
            return new DealPositionsSqlServerBatchInserter();
        else
            return new DealPositionsDefaultBatchInserter();
    }


    @Bean
    public DealHourlyPositionsBatchInserter getDealHourlyPositionsBatchInserter() {
        if (isSqlServer)
            return new DealHourlyPositionsSqlServerBatchInserter();
        else
            return new DealHourlyPositionsDefaultBatchInserter();
    }


    @Bean
    public CostPositionsBatchInserter getCostPositionsBatchInserter() {
        if (isSqlServer)
            return new CostPositionsSqlServerBatchInserter();
        else
            return new CostPositionsDefaultBatchInserter();
    }

    @Bean
    public PowerProfilePositionsBatchInserter getPowerProfilePositionsBatchInserter() {
        if (isSqlServer)
            return new PowerProfilePositionsSqlServerBatchInserter();
        else
            return new PowerProfilePositionsDefaultBatchInserter();
    }

    @Bean
    public PositionRiskFactorMappingBatchInserter positionRiskFactorMappingBatchInserter() {
        if (isSqlServer)
            return new PositionRiskFactorMappingSqlServerBatchInserter();
        else
            return new PositionRiskFactorMappingDefaultBatchInserter();
    }


}
