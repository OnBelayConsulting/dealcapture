package com.onbelay.dealcapture.dealmodule.positions.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.adapter.PowerProfilePositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.service.PowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshotCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PowerProfilePositionRestAdapterBean extends BaseRestAdapterBean implements PowerProfilePositionRestAdapter {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;


    @Override
    public PowerProfilePositionSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("PowerProfilePosition");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("PowerProfilePosition", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("PowerProfilePosition");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "startDate"));
        }

        QuerySelectedPage allIds = powerProfilePositionsService.findPositionIds(definedQuery);

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new PowerProfilePositionSnapshotCollection(
                    start,
                    limit,
                    allIds.getIds().size());
        }

        int toIndex = start + limit;

        if (toIndex > allIds.getIds().size())
            toIndex =  allIds.getIds().size();
        int fromIndex = start;

        List<Integer> selected = allIds.getIds().subList(fromIndex, toIndex);
        QuerySelectedPage limitedPageSelection = new QuerySelectedPage(
                selected,
                allIds.getOrderByClause());

        List<PowerProfilePositionSnapshot> snapshots = powerProfilePositionsService.findByIds(limitedPageSelection);
        return new PowerProfilePositionSnapshotCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

    @Override
    public PowerProfilePositionSnapshot load(EntityId entityId) {
        initializeSession();
        return powerProfilePositionsService.load(entityId);
    }
}
