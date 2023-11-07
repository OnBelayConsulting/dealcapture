package com.onbelay.dealcapture.organization.subscribe.subscriber;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.organization.service.OrganizationService;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;
import com.onbelay.dealcapture.organization.subscribe.snapshot.SubOrganizationSnapshot;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizationUpdaterTest extends DealCaptureAppSpringTestCase {

    @Autowired
    private OrganizationUpdater organizationUpdater;

    @Autowired
    private OrganizationService organizationService;

    @Test
    public void updateOrganization() {
        ArrayList<SubOrganizationSnapshot> snapshotsIn = new ArrayList<>();
        SubOrganizationSnapshot snapshotIn = new SubOrganizationSnapshot();
        snapshotIn.setEntityId(new EntityId(1));
        snapshotIn.getDetail().setLegalName("My Corp.");
        snapshotsIn.add(snapshotIn);

        organizationUpdater.updateOrganizations(snapshotsIn);
        flush();

        OrganizationSnapshot updated = organizationService.load(myOrganization.generateEntityId());
        assertEquals("My Corp.", updated.getDetail().getLegalName());
    }

    @Test
    public void deleteExistingOrganization() {
        ArrayList<SubOrganizationSnapshot> snapshotsIn = new ArrayList<>();
        SubOrganizationSnapshot snapshotIn = new SubOrganizationSnapshot();
        snapshotIn.setEntityState(EntityState.DELETE);
        snapshotIn.setEntityId(new EntityId(1));
        snapshotsIn.add(snapshotIn);

        organizationUpdater.updateOrganizations(snapshotsIn);
        flush();

        OrganizationSnapshot updated = organizationService.load(myOrganization.generateEntityId());
        assertEquals(true, updated.getEntityId().isDeleted());
    }

    @Test
    public void createNewOrganization() {
        ArrayList<SubOrganizationSnapshot> snapshotsIn = new ArrayList<>();
        SubOrganizationSnapshot snapshotIn = new SubOrganizationSnapshot();
        snapshotIn.setEntityId(new EntityId(3));
        snapshotIn.getDetail().setLegalName("My Corp.");
        snapshotIn.getDetail().setShortName("My");
        snapshotsIn.add(snapshotIn);

        organizationUpdater.updateOrganizations(snapshotsIn);
        flush();

        OrganizationSnapshot created = organizationService.findByExternalReference(3);
        assertNotNull(created);
        assertEquals("My Corp.", created.getDetail().getLegalName());
    }

    @Test
    public void deleteNonexistentOrganization() {
        ArrayList<SubOrganizationSnapshot> snapshotsIn = new ArrayList<>();
        SubOrganizationSnapshot snapshotIn = new SubOrganizationSnapshot();
        snapshotIn.setEntityState(EntityState.DELETE);
        snapshotIn.setEntityId(new EntityId(3));
        snapshotsIn.add(snapshotIn);

        organizationUpdater.updateOrganizations(snapshotsIn);
        flush();

        OrganizationSnapshot created = organizationService.findByExternalReference(3);
        assertNull(created);
    }




}
