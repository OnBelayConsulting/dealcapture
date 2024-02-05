package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.positions.assembler.PositionAssembler;
import com.onbelay.dealcapture.dealmodule.positions.assembler.DealPositionAssemblerFactory;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.valuator.DealPositionValuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DealPositionServiceBean implements DealPositionService {

    @Autowired
    private DealPositionRepository dealPositionRepository;

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private DealPositionValuator dealPositionValuator;

    @Override
    public DealPositionSnapshot load(EntityId entityId) {
        DealPosition position =  dealPositionRepository.load(entityId);
        DealPositionAssemblerFactory factory = new DealPositionAssemblerFactory();
        PositionAssembler assembler = factory.newAssembler(position.getDealPositionDetail().getDealTypeCode());
        return assembler.assemble(position);
    }

    @Override
    public TransactionResult saveDealPositions(
            String positionGeneratorIdentifier,
            EntityId dealId,
            List<DealPositionSnapshot> positions) {

        BaseDeal deal = dealRepository.load(dealId);
        deal.savePositions(
                positionGeneratorIdentifier,
                positions);
        return new TransactionResult();
    }

    @Override
    public TransactionResult saveAllDealPositions(
            String positionGenerationIdentifier,
            List<DealPositionSnapshot> positions) {

        HashMap<Integer, List<DealPositionSnapshot>> dealPositionMap = new HashMap<>();
        for (DealPositionSnapshot snapshot : positions) {
            List<DealPositionSnapshot> list = dealPositionMap.get(snapshot.getDealId().getId());
            if (list == null) {
                list = new ArrayList<>();
                dealPositionMap.put(snapshot.getDealId().getId(), list);
            }
            list.add(snapshot);
        }
        for (Integer dealId : dealPositionMap.keySet()) {
            BaseDeal deal = dealRepository.load(new EntityId(dealId));
            deal.savePositions(
                positionGenerationIdentifier,
                dealPositionMap.get(dealId));
        }

        return new TransactionResult();
    }

    @Override
    public List<DealPositionSnapshot> findByDeal(EntityId entityId) {
        List<DealPosition> positions =  dealPositionRepository.findByDeal(entityId);
        DealPositionAssemblerFactory factory = new DealPositionAssemblerFactory();
        PositionAssembler assembler = factory.newAssembler(DealTypeCode.PHYSICAL_DEAL);
        return assembler.assemble(positions);
    }

    @Override
    public QuerySelectedPage findPositionIds(DefinedQuery definedQuery) {
        return new QuerySelectedPage(
                dealPositionRepository.findPositionIds(definedQuery),
                definedQuery.getOrderByClause());
    }

    @Override
    public List<DealPositionSnapshot> findByIds(QuerySelectedPage selectedPage) {
        List<DealPosition> positions = dealPositionRepository.fetchByIds(selectedPage);
        DealPositionAssemblerFactory factory = new DealPositionAssemblerFactory();
        return factory.assemble(positions);
    }

    @Override
    public TransactionResult valuePositions(
            EntityId dealId,
            LocalDateTime currentDateTime) {
        dealPositionValuator.valuePositions(
                dealId,
                currentDateTime);
        return new TransactionResult();
    }

    @Override
    public TransactionResult valuePositions(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime) {

        List<Integer> ids = dealPositionRepository.findPositionIds(definedQuery);
        dealPositionValuator.valuePositions(
                new QuerySelectedPage(ids),
                currentDateTime);
        return new TransactionResult();
    }
}
