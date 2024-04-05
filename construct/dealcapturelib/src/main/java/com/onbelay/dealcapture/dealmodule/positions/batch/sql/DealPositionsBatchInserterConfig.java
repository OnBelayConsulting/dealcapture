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
    public CostPositionsBatchInserter getCostPositionsBatchInserter() {
        if (isSqlServer)
            return new CostPositionsSqlServerBatchInserter();
        else
            return new CostPositionsDefaultBatchInserter();
    }


}
