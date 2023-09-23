package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.shared.DealPositionDetail;
import com.onbelay.dealcapture.dealmodule.deal.shared.PhysicalDealPositionDetail;

import jakarta.persistence.*;

public class PhysicalDealPosition extends DealPosition {

    private Integer id;

    private PhysicalDealPositionDetail detail = new PhysicalDealPositionDetail();

    @Id
    @Column(name="ENTITY_ID", insertable =  false, updatable = false)
    @SequenceGenerator(name="physicalPosGen", sequenceName="PHYSICAL_POSITION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "physicalPosGen")
    public Integer getId() {
        return id;
    }

    private void setId(Integer dealId) {
        this.id = dealId;
    }


    @Override
    @Transient
    public DealPositionDetail getDealPositionDetail() {
        return detail;
    }

    @Embedded
    public PhysicalDealPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(PhysicalDealPositionDetail detail) {
        this.detail = detail;
    }
}
