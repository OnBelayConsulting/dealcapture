package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.enums.AssemblerDirectiveCopyType;
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.assembler.CostPositionAssembler;
import com.onbelay.dealcapture.dealmodule.positions.assembler.DealHourlyPositionAssembler;
import com.onbelay.dealcapture.dealmodule.positions.assembler.DealPositionAssemblerFactory;
import com.onbelay.dealcapture.dealmodule.positions.assembler.PositionAssembler;
import com.onbelay.dealcapture.dealmodule.positions.model.*;
import com.onbelay.dealcapture.dealmodule.positions.repository.CostPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealHourlyPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.repository.PositionRiskFactorMappingRepository;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.*;
import com.onbelay.shared.enums.CurrencyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class DealPositionServiceBean implements DealPositionService {

    @Autowired
    private DealPositionRepository dealPositionRepository;

    @Autowired
    private DealHourlyPositionRepository dealHourlyPositionRepository;

    @Autowired
    private CostPositionRepository costPositionRepository;

    @Autowired
    private PositionRiskFactorMappingRepository positionRiskFactorMappingRepository;

    @Override
    public DealPositionSnapshot load(EntityId entityId) {
        DealPosition position =  dealPositionRepository.load(entityId);
        DealPositionAssemblerFactory factory = new DealPositionAssemblerFactory();
        PositionAssembler assembler = factory.newAssembler(position.getDealTypeCode());
        return assembler.assemble(position);
    }

    @Override
    public TransactionResult saveDealPositions(
            String positionGeneratorIdentifier,
            List<DealPositionSnapshot> positions) {

        for (DealPositionSnapshot snapshot : positions) {
            if (snapshot.getEntityState() == EntityState.NEW) {
                PhysicalPosition position = new PhysicalPosition();
                position.createWith(snapshot);
            }
        }
        return new TransactionResult();
    }

    @Override
    public List<DealPositionView> fetchDealPositionViews(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {

        List<DealPositionView> views = dealPositionRepository.findDealPositionViews(
                    dealIds,
                    currencyCode,
                    createdDateTime);

        HashMap<Integer, DealPositionView> viewMap = new HashMap<>();
        views.forEach(c-> viewMap.put(c.getId(), c));

        List<Integer> positionIds = viewMap.keySet().stream().toList();
        for (PositionRiskFactorMappingSummary summary : positionRiskFactorMappingRepository.findAllMappingSummaries(positionIds)) {
            DealPositionView view = viewMap.get(summary.getDealPositionId());
            view.addMappingSummary(summary);
        }

        return views;
    }

    @Override
    public List<DealPositionView> fetchDealPositionViews(
            Integer dealId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {

        List<DealPositionView> views = dealPositionRepository.findDealPositionViews(
                dealId,
                currencyCode,
                createdDateTime);
        HashMap<Integer, DealPositionView> viewMap = new HashMap<>();
        views.forEach(c-> viewMap.put(c.getId(), c));

        List<Integer> positionIds = viewMap.keySet().stream().toList();
        for (PositionRiskFactorMappingSummary summary : positionRiskFactorMappingRepository.findAllMappingSummaries(positionIds)) {
            DealPositionView view = viewMap.get(summary.getDealPositionId());
            view.addMappingSummary(summary);
        }

        return views;

    }

    @Override
    public List<DealHourlyPositionView> fetchDealHourlyPositionViews(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {

        return dealHourlyPositionRepository.findDealHourlyPositionViews(
                dealIds,
                currencyCode,
                createdDateTime);
    }

    @Override
    public List<DealHourlyPositionView> fetchDealHourlyPositionViews(
            Integer dealId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {

        return dealHourlyPositionRepository.findDealHourlyPositionViews(
                dealId,
                currencyCode,
                createdDateTime);
    }

    @Override
    public List<DealPositionSnapshot> findPositionsByDeal(EntityId entityId) {
        List<DealPosition> positions =  dealPositionRepository.findByDeal(entityId);
        DealPositionAssemblerFactory factory = new DealPositionAssemblerFactory();
        PositionAssembler assembler = factory.newAssembler(DealTypeCode.PHYSICAL_DEAL);
        return assembler.assemble(positions);
    }

    @Override
    public List<DealPositionSnapshot> findPositionsByDeal(
            EntityId dealId,
            AssemblerDirectiveCopyType copyType) {

        return switch (copyType) {
            case SHALLOW_COPY -> findPositionsByDeal(dealId);

            case  DEEP_COPY -> findPositionsByDealWithDeepCopy(dealId);

            default -> findPositionsByDeal(dealId);
        };
    }

    private List<DealPositionSnapshot> findPositionsByDealWithDeepCopy(EntityId dealId) {
        List<DealHourlyPositionSnapshot> hourlyPositionSnapshots = findHourlyPositionsByDeal(dealId);
        HashMap<LocalDate, List<DealHourlyPositionSnapshot>> positionMap = new HashMap<>();
        hourlyPositionSnapshots.forEach( c ->{
            List<DealHourlyPositionSnapshot> list = positionMap.computeIfAbsent(
                    c.getDetail().getStartDate(),
                    l -> new ArrayList<>());
            list.add(c);
        });

        List<DealPositionSnapshot> dealPositionSnapshots = findPositionsByDeal(dealId);
        for (DealPositionSnapshot snapshot : dealPositionSnapshots) {
            List<DealHourlyPositionSnapshot> list = positionMap.get(snapshot.getDealPositionDetail().getStartDate());
            if (list != null) {
                snapshot.setHourlyPositionSnapshots(list);
            }
        }
        return dealPositionSnapshots;
    }

    @Override
    public List<DealHourlyPositionSnapshot> findHourlyPositionsByDeal(EntityId dealId) {
        List<DealHourlyPosition> positions =  dealHourlyPositionRepository.findByDeal(dealId);
        DealHourlyPositionAssembler assembler = new DealHourlyPositionAssembler();
        return assembler.assemble(positions);
    }

    @Override
    public List<CostPositionView> fetchCostPositionViewsWithFX(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {

        return costPositionRepository.findCostPositionViewsWithFX(
                dealIds,
                currencyCode,
                createdDateTime);
    }

    @Override
    public List<CostPositionView> fetchCostPositionViewsWithFX(
            Integer dealId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {

        return costPositionRepository.findCostPositionViewsWithFX(
                dealId,
                currencyCode,
                createdDateTime);
    }

    @Override
    public List<Integer> findIdsByDeal(EntityId entityId) {
        return dealPositionRepository.findIdsByDeal(entityId);
    }

    @Override
    public List<Integer> findCostPositionIdsByDeal(EntityId dealId) {
        return costPositionRepository.findCostPositionIdsByDeal(dealId);
    }

    @Override
    public List<TotalCostPositionSummary> calculateTotalCostPositionSummaries(
            Integer dealId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {

        return costPositionRepository.calculateTotalCostSummaries(
                dealId,
                currencyCode,
                createdDateTime);
    }

    @Override
    public List<TotalCostPositionSummary> calculateTotalCostPositionSummaries(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime) {
        return costPositionRepository.calculateTotalCostSummaries(
                dealIds,
                currencyCode,
                createdDateTime);
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
    public List<DealPositionView> findViewsByIds(QuerySelectedPage selectedPage) {
        return dealPositionRepository.fetchViewsByIds(selectedPage);
    }

    @Override
    public List<CostPositionSnapshot> findCostPositionsByIds(QuerySelectedPage selectedPage) {
        List<CostPosition> costs = costPositionRepository.fetchByIds(selectedPage);
        CostPositionAssembler assembler = new CostPositionAssembler();
        return assembler.assemble(costs);
    }
}
