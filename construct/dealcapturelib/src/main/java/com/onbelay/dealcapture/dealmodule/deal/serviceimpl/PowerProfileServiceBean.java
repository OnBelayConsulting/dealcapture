package com.onbelay.dealcapture.dealmodule.deal.serviceimpl;

import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.assembler.PowerProfileAssembler;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PowerProfileServiceBean extends BaseDomainService implements PowerProfileService {

    @Autowired
    private PowerProfileRepository powerProfileRepository;

    @Override
    public QuerySelectedPage findPowerProfileIds(DefinedQuery definedQuery) {
        return new QuerySelectedPage(
                powerProfileRepository.findProfileIds(definedQuery),
                definedQuery.getOrderByClause());
    }

    @Override
    public List<PowerProfileSnapshot> findByIds(QuerySelectedPage selectedPage) {
        List<PowerProfile> profiles = powerProfileRepository.fetchByIds(selectedPage);
        PowerProfileAssembler assembler = new PowerProfileAssembler();
        return assembler.assemble(profiles);
    }

    @Override
    public void assignPositionIdentifierToPowerProfiles(
            String positionGenerationIdentifier,
            List<Integer> entityIds) {

        powerProfileRepository.executeUpdateAssignForPositionGeneration(
                entityIds,
                positionGenerationIdentifier);
    }

    @Override
    public List<PowerProfileSnapshot> getAssignedPowerProfiles(String positionGenerationIdentifier) {
        List<PowerProfile> profiles = powerProfileRepository.getAssignedPowerProfiles(positionGenerationIdentifier);
        PowerProfileAssembler assembler = new PowerProfileAssembler();
        return assembler.assemble(profiles);
    }

    @Override
    public void updatePositionStatusToComplete(
            List<Integer> powerProfileIds,
            LocalDateTime createdDateTime) {
        powerProfileRepository.executeUpdatePositionGenerationToComplete(
                powerProfileIds,
                createdDateTime);
    }

    @Override
    public void updatePositionGenerationStatusToPending(List<Integer> powerProfileIds) {
        powerProfileRepository.executeUpdateSetPositionGenerationToPending(powerProfileIds);
    }

    @Override
    public TransactionResult save(PowerProfileSnapshot snapshot) {
        switch (snapshot.getEntityState()) {
            case NEW -> {
                PowerProfile profile = PowerProfile.create(snapshot);
                return new TransactionResult(profile.getId());
            }
            case MODIFIED -> {
                PowerProfile profile = powerProfileRepository.load(snapshot.getEntityId());
                profile.updateWith(snapshot);
                return new TransactionResult(profile.getId());
            }
            case DELETE -> {
                PowerProfile profile = powerProfileRepository.load(snapshot.getEntityId());
                profile.delete();
            }
        }
        return new TransactionResult();
    }

    @Override
    public TransactionResult save(List<PowerProfileSnapshot> snapshots) {
        List<Integer> ids = new ArrayList<>();
        for (PowerProfileSnapshot snapshot : snapshots) {
            TransactionResult child = save(snapshot);
            if (child.getId() != null)
                ids.add(child.getId());

        }
        return new TransactionResult(ids);
    }

    @Override
    public PowerProfileSnapshot load(EntityId entityId) {
        PowerProfile powerProfile = powerProfileRepository.load(entityId);
        PowerProfileAssembler assembler = new PowerProfileAssembler();
        return assembler.assemble(powerProfile);
    }
}
