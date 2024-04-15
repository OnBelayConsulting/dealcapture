package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.assembler.PowerProfilePositionAssembler;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePosition;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionView;
import com.onbelay.dealcapture.dealmodule.positions.repository.PowerProfilePositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.service.PowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PowerProfilePositionServiceBean implements PowerProfilePositionsService {

    @Autowired
    private PowerProfilePositionRepository powerProfilePositionRepository;

    @Override
    public PowerProfilePositionSnapshot load(EntityId entityId) {
        PowerProfilePosition position =  powerProfilePositionRepository.load(entityId);
        PowerProfilePositionAssembler assembler = new PowerProfilePositionAssembler();
        return assembler.assemble(position);
    }

    @Override
    public TransactionResult savePowerProfilePositions(
            String positionGeneratorIdentifier,
            List<PowerProfilePositionSnapshot> positions) {

        for (PowerProfilePositionSnapshot snapshot : positions) {
            if (snapshot.getEntityState() == EntityState.NEW) {
                PowerProfilePosition position = new PowerProfilePosition();
                position.createWith(snapshot);
            }
        }
        return new TransactionResult();
    }

    @Override
    public List<PowerProfilePositionView> fetchPowerProfilePositionViews(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {

        List<PowerProfilePositionView> views = powerProfilePositionRepository.findPowerProfilePositionViews(
                    dealIds,
                    currencyCode,
                    createdDateTime);


        return views;
    }

    @Override
    public List<PowerProfilePositionSnapshot> findByPowerProfile(EntityId entityId) {
        List<PowerProfilePosition> positions =  powerProfilePositionRepository.findByPowerProfile(entityId);
        PowerProfilePositionAssembler assembler = new PowerProfilePositionAssembler();
        return assembler.assemble(positions);
    }


    @Override
    public QuerySelectedPage findPositionIds(DefinedQuery definedQuery) {
        return new QuerySelectedPage(
                powerProfilePositionRepository.findPowerProfilePositionIds(definedQuery),
                definedQuery.getOrderByClause());
    }

    @Override
    public List<PowerProfilePositionSnapshot> findByIds(QuerySelectedPage selectedPage) {
        List<PowerProfilePosition> positions = powerProfilePositionRepository.fetchByIds(selectedPage);
        PowerProfilePositionAssembler assembler = new PowerProfilePositionAssembler();
        return assembler.assemble(positions);
    }

}
