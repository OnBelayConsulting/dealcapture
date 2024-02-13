package com.onbelay.dealcapture.dealmodule.positions.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.model.PositionRiskFactorMapping;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;

import java.util.List;

public interface PositionRiskFactorMappingRepository {
    public static final String BEAN_NAME = "positionRiskFactorMappingRepository";

    PositionRiskFactorMapping load(EntityId entityId);

    List<PositionRiskFactorMapping> findByDealPosition(EntityId dealEntityId);

    public List<PositionRiskFactorMappingSummary> findMappingSummaries(
            EntityId positionEntityId,
            PriceTypeCode priceTypeCode);

    List<PositionRiskFactorMappingSummary> findAllMappingSummaries(List<Integer> positionIds);
}
