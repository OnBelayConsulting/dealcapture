package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.shared.DealPositionDetail;

import javax.persistence.Transient;

public abstract class DealPosition {

    @Transient
    public abstract DealPositionDetail getDealPositionDetail();

}
