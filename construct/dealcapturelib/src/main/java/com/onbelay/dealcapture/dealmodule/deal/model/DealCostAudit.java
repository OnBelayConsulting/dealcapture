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
package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostDetail;
import jakarta.persistence.*;

@Entity
@Table (name = "DEAL_COST_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = DealCostAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT dealCostAudit " +
			   "  FROM DealCostAudit dealCostAudit " +
       		    "WHERE dealCostAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND dealCostAudit.dealCost = :dealCost")
})
public class DealCostAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "DealCostAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;
	
	private BaseDeal dea1;
	private DealCost dealCost;
	private DealCostDetail detail = new DealCostDetail();
	
	protected DealCostAudit() {
		
	}
	
	protected DealCostAudit(DealCost dealCost) {
		this.dealCost = dealCost;
	}
	
	public static DealCostAudit create(
			DealCost dealCost) {
		
		DealCostAudit audit = new DealCostAudit(dealCost);
		audit.dea1 = dealCost.getDeal();
		audit.detail.copyFrom(dealCost.getDetail());
		audit.save();
		return audit;
		
	}
	
	

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="DealCostAuditGen", sequenceName="DEAL_COST_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DealCostAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealCostAuditId) {
		this.id = dealCostAuditId;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public DealCost getDealCost() {
		return dealCost;
	}

	public void setDealCost(DealCost dealCost) {
		this.dealCost = dealCost;
	}

	@ManyToOne
	@JoinColumn(name ="DEAL_ID")

	public BaseDeal getDea1() {
		return dea1;
	}

	public void setDea1(BaseDeal dea1) {
		this.dea1 = dea1;
	}

	@Embedded
	public DealCostDetail getDetail() {
		return detail;
	}


	private void setDetail(DealCostDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return dealCost;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		DealCost dealCost = (DealCost) entity;
		this.dea1 = dealCost.getDeal();
		this.detail.copyFrom(dealCost.getDetail());
	}


	public static DealCostAudit findRecentHistory(DealCost dealCost) {
		String[] parmNames = {"dealCost", "date" };
		Object[] parms =     {dealCost,   DateUtils.getValidToDateTime()};

		return (DealCostAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
