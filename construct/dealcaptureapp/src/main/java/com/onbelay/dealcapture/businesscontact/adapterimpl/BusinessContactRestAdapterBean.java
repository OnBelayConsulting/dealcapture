package com.onbelay.dealcapture.businesscontact.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.businesscontact.adapter.BusinessContactRestAdapter;
import com.onbelay.dealcapture.businesscontact.service.BusinessContactService;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessContactRestAdapterBean extends BaseRestAdapterBean implements BusinessContactRestAdapter {

    @Autowired
    private BusinessContactService businessContactService;

    @Override
    public TransactionResult save(BusinessContactSnapshot snapshot) {
        initializeSession();
        return businessContactService.save(snapshot);
    }

    @Override
    public BusinessContactSnapshot load(EntityId priceIndexId) {
        initializeSession();
        return businessContactService.load(priceIndexId);
    }

    @Override
    public BusinessContactSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText == null || queryText.equals("default")) {
            definedQuery = new DefinedQuery("BusinessContact");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("lastName"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("BusinessContact", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("lastName"));
            }
        }

        QuerySelectedPage selectedPage  = businessContactService.findBusinessContactIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new BusinessContactSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<BusinessContactSnapshot> snapshots = businessContactService.findByIds(querySelectedPage);

        return new BusinessContactSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }
}
