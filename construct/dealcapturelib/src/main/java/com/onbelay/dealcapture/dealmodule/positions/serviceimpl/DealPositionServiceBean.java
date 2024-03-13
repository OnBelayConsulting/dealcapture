package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.assembler.DealPositionAssemblerFactory;
import com.onbelay.dealcapture.dealmodule.positions.assembler.PositionAssembler;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPosition;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.repository.PositionRiskFactorMappingRepository;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class DealPositionServiceBean implements DealPositionService {

    @Autowired
    private DealPositionRepository dealPositionRepository;

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
    public List<DealPositionView> fetchDealPositionViews(List<Integer> positionIds) {
        SubLister<Integer> subLister = new SubLister<>(positionIds, 2000);
        ArrayList<DealPositionView> reports = new ArrayList<>();
        while (subLister.moreElements()) {

            List<Integer> idList = subLister.nextList();
            List<DealPositionView> subReports = dealPositionRepository.findDealPositionViews(idList);
            HashMap<Integer, DealPositionView> reportMap = new HashMap<>();
            subReports.forEach(c-> reportMap.put(c.getId(), c));
            for (PositionRiskFactorMappingSummary summary : positionRiskFactorMappingRepository.findAllMappingSummaries(idList)) {
                DealPositionView report = reportMap.get(summary.getDealPositionId());
                report.addMappingSummary(summary);
            }

            reports.addAll(subReports);
        }



        return reports;
    }

    @Override
    public List<DealPositionSnapshot> findByDeal(EntityId entityId) {
        List<DealPosition> positions =  dealPositionRepository.findByDeal(entityId);
        DealPositionAssemblerFactory factory = new DealPositionAssemblerFactory();
        PositionAssembler assembler = factory.newAssembler(DealTypeCode.PHYSICAL_DEAL);
        return assembler.assemble(positions);
    }

    @Override
    public List<DealPositionView> findDealPositionViewsByDeal(EntityId dealId) {
        return dealPositionRepository.findDealPositionViewsByDeal(dealId);
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

}
