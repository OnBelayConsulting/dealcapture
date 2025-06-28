package com.onbelay.dealcapture.businesscontact.service;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.model.BusinessContactFixture;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusinessContactServiceTest extends DealCaptureSpringTestCase {

    @Autowired
    private BusinessContactService businessContactService;

    private BusinessContact businessContact;
    @Override
    public void setUp() {
        super.setUp();
        businessContact = BusinessContactFixture.createCounterpartyTrader("sam", "sneed", "hj@core.com");

        flush();
    }

    @Test
    public void fetchBusinessContacts() {
        DefinedQuery definedQuery = new DefinedQuery("BusinessContact");
        definedQuery.getWhereClause().addExpression(
                new DefinedWhereExpression(
                        "lastName",
                        ExpressionOperator.EQUALS,
                        "sneed"
                )
        );
        QuerySelectedPage page = businessContactService.findBusinessContactIds(definedQuery);
        assertEquals(1, page.getIds().size());
        List<BusinessContactSnapshot> snapshots = businessContactService.findByIds(page);
        assertEquals(1, snapshots.size());
        BusinessContactSnapshot snapshot = snapshots.get(0);
        assertEquals("sam", snapshot.getDetail().getFirstName());
    }

    @Test
    public void testCreateBusinessContact() {

        BusinessContactSnapshot snapshot = new BusinessContactSnapshot();
        snapshot.getDetail().setLastName("DaRose");
        snapshot.getDetail().setFirstName("Jack");
        snapshot.getDetail().setEmail("me@network.com");

        snapshot.getDetail().setIsAdministrator(true);
        snapshot.getDetail().setIsCompanyTrader(true);
        snapshot.getDetail().setIsCounterpartyTrader(false);


        TransactionResult result = businessContactService.save(snapshot);
        flush();
        BusinessContactSnapshot created = businessContactService.load(result.getEntityId());
        assertEquals("DaRose", created.getDetail().getLastName());
        assertEquals("Jack", created.getDetail().getFirstName());
        assertEquals("me@network.com", created.getDetail().getEmail());
        assertEquals(true, created.getDetail().getIsAdministrator().booleanValue());
        assertEquals(true, created.getDetail().getIsCompanyTrader().booleanValue());
        assertEquals(false, created.getDetail().getIsCounterpartyTrader().booleanValue());
    }

    @Test
    public void updateBusinessContact() {
        BusinessContactSnapshot snapshot = new BusinessContactSnapshot();
        snapshot.setEntityId(businessContact.generateEntityId());
        snapshot.setEntityState(EntityState.MODIFIED);
        snapshot.getDetail().setLastName("DaRose");
        businessContactService.save(snapshot);
        flush();
        BusinessContactSnapshot updated = businessContactService.load(businessContact.generateEntityId());
        assertEquals("DaRose", updated.getDetail().getLastName());
    }

}
