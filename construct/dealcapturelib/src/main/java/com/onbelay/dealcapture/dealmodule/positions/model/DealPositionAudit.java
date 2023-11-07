/*
 Copyright 2019, OnBelay Consulting Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  
 */
package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import jakarta.persistence.*;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionDetail;

@Entity
@Table (name = "DEAL_POSITION_AUDIT")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
       name = DealPositionAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM DealPositionAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.dealPosition = :dealPosition")
})
public abstract class DealPositionAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "DealPositionAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;
	
	private BaseDeal deal;
	private DealPosition dealPosition;
	private DealPositionDetail dealPositionDetail = new DealPositionDetail();
	
	protected DealPositionAudit() {
		
	}
	
	protected DealPositionAudit(DealPosition dealPosition) {
		this.dealPosition = dealPosition;
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="DealPositionAuditGen", sequenceName="DEAL_POSITION_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DealPositionAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealDealPositionAuditId) {
		this.id = dealDealPositionAuditId;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public DealPosition getDealPosition() {
		return dealPosition;
	}

	public void setDealPosition(DealPosition dealPosition) {
		this.dealPosition = dealPosition;
	}


	@ManyToOne
	@JoinColumn(name = "DEAL_ID")
	public BaseDeal getDeal() {
		return deal;
	}

	public void setDeal(BaseDeal deal) {
		this.deal = deal;
	}

	@Embedded
	public DealPositionDetail getDealPositionDetail() {
		return dealPositionDetail;
	}


	private void setDealPositionDetail(DealPositionDetail detail) {
		this.dealPositionDetail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return dealPosition;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		DealPosition dealPosition = (DealPosition) entity;
		this.deal = dealPosition.getDeal();
		this.dealPositionDetail.copyFrom(dealPosition.getDealPositionDetail());
	}


	public static DealPositionAudit findRecentHistory(DealPosition dealPosition) {
		String[] parmNames = {"dealPosition", "date" };
		Object[] parms =     {dealPosition,   DateUtils.getValidToDateTime()};

		return (DealPositionAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
