package com.onbelay.dealcapture.organization.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;
import com.onbelay.dealcapture.organization.adapter.OrganizationRestAdapter;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.service.OrganizationService;
import com.onbelay.dealcapture.organization.snapshot.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationRestAdapterBean extends BaseRestAdapterBean implements OrganizationRestAdapter {

    @Autowired
    private OrganizationService organizationService;

    @Override
    public TransactionResult save(OrganizationSnapshot snapshot) {
        initializeSession();
        return organizationService.save(snapshot);
    }

    @Override
    public OrganizationSnapshot get(Integer id) {
        initializeSession();
        return organizationService.load(new EntityId(id));
    }

    @Override
    public OrganizationSnapshotCollection find(
            String queryText,
            String orgRoleTypeString,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;
        QuerySelectedPage allIds;

        if (orgRoleTypeString.equalsIgnoreCase("All") == false) {
            OrganizationRoleType orgRoleType = OrganizationRoleType.lookUp(orgRoleTypeString);
            allIds = findOrganizationIdsByRoleType(queryText, orgRoleType);
        } else {


            if (queryText != null) {
                if (queryText.equalsIgnoreCase("default")) {
                    definedQuery = new DefinedQuery("Organization");
                } else {
                    DefinedQueryBuilder builder = new DefinedQueryBuilder("Organization", queryText);
                    definedQuery = builder.build();
                }
            } else {
                definedQuery = new DefinedQuery("Organization");
            }

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause().addOrderExpression(
                        new DefinedOrderExpression(
                                "shortName"));
            }

            allIds = organizationService.findOrganizationIds(definedQuery);
        }

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new OrganizationSnapshotCollection(
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

        List<OrganizationSnapshot> snapshots = organizationService.findByIds(limitedPageSelection);
        return new OrganizationSnapshotCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

    private QuerySelectedPage findOrganizationIdsByRoleType(
              String queryText,
              OrganizationRoleType  orgRoleType) {

        initializeSession();

        DefinedQuery definedQuery;


        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("OrganizationRole");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("OrganizationRole", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("OrganizationRole");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "shortName"));
        }
        definedQuery.getWhereClause().surroundWithBrackets();
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "organizationRoleType",
                        ExpressionOperator.EQUALS,
                        orgRoleType.getCode())
        );

        return organizationService.findOrganizationIdsFromOrganizationRoleType(definedQuery);


    }

    @Override
    public TransactionResult saveRoles(
            EntityId organizationId,
            List<OrganizationRoleSnapshot> snapshots) {

        initializeSession();

        return organizationService.saveOrganizationRoles(
                organizationId,
                snapshots);
    }

    @Override
    public OrganizationRoleSummaryCollection findSummariesLikeShortName(
            String shortName,
            OrganizationRoleType roleType,
            Integer limit) {
        initializeSession();

        List<OrganizationRoleSummary> summaries = organizationService.findOrganizationRoleSummariesLikeShortName(
                shortName,
                roleType);

        return new OrganizationRoleSummaryCollection(
                0,
                limit,
                summaries.size(),
                summaries);
    }

    @Override
    public OrganizationRoleSummaryCollection findSummaries(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("OrganizationRole");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("OrganizationRole", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("OrganizationRole");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "shortName"));
        }

        QuerySelectedPage allIds = organizationService.findOrganizationRoleIds(definedQuery);

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new OrganizationRoleSummaryCollection(
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

        List<OrganizationRoleSummary> snapshots = organizationService.findOrganizationRoleSummariesByIds(limitedPageSelection);
        return new OrganizationRoleSummaryCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

}
