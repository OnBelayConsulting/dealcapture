package com.onbelay.dealcapture.riskfactor.batch.sql;

import com.onbelay.dealcapture.dealmodule.positions.batch.sql.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RiskFactorBatchInserterConfig {

    @Value("${batchInserterIsSqlServer:false}")
    private boolean isSqlServer;

    @Bean
    public PriceRiskFactorBatchInserter priceRiskFactorBatchInserter() {
        if (isSqlServer)
            return new PriceRiskFactorSqlServerBatchInserter();
        else
            return new PriceRiskFactorDefaultBatchInserter();
    }

    @Bean
    public FxRiskFactorBatchInserter fxRiskFactorBatchInserter() {
        if (isSqlServer)
            return new FxRiskFactorSqlServerBatchInserter();
        else
            return new FxRiskFactorDefaultBatchInserter();
    }

}
