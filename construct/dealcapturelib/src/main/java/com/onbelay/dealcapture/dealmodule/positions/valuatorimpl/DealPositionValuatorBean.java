package com.onbelay.dealcapture.dealmodule.positions.valuatorimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.valuator.DealPositionValuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Transactional
public class DealPositionValuatorBean implements DealPositionValuator {

    @Autowired
    private DealPositionRepository dealPositionRepository;

    @Override
    public void valuePositions(
            EntityId dealId,
            LocalDateTime currentDateTime) {
            List<DealPosition> positions = dealPositionRepository.findByDeal(dealId);
            positions.forEach(p -> p.valuePosition(currentDateTime));
    }

    @Override
    public void valuePositions(
            QuerySelectedPage page,
            LocalDateTime currentDateTime) {
        List<DealPosition> positions = dealPositionRepository.fetchByIds(page);
        positions.forEach(p -> p.valuePosition(currentDateTime));
    }
}
