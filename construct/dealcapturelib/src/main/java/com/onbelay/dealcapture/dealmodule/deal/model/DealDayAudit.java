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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayDetail;
import jakarta.persistence.*;

@Entity
@Table (name = "DEAL_DAY_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = DealDayAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM DealDayAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.dealDay = :dealDay")
})
public class DealDayAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "DealDayAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private BaseDeal deal;
	private DealDay dealDay;
	private DealDayDetail detail = new DealDayDetail();

	protected DealDayAudit() {

	}

	protected DealDayAudit(DealDay dealDay) {
		this.dealDay = dealDay;
		this.deal = dealDay.getDeal();
		this.detail.copyFrom(dealDay.getDetail());
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="DealDayAuditGen", sequenceName="DEAL_DAY_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DealDayAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealQuantityAuditId) {
		this.id = dealQuantityAuditId;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public DealDay getDealDay() {
		return dealDay;
	}

	public void setDealDay(DealDay dealDay) {
		this.dealDay = dealDay;
	}

	@ManyToOne
	@JoinColumn(name ="DEAL_ID")

	public BaseDeal getDea1() {
		return deal;
	}

	public void setDea1(BaseDeal dea1) {
		this.deal = dea1;
	}

	@Embedded
	public DealDayDetail getDetail() {
		return detail;
	}


	private void setDetail(DealDayDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return dealDay;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		DealDay dealDay = (DealDay) entity;
		this.deal = dealDay.getDeal();
		this.detail.copyFrom(dealDay.getDetail());
	}


	public static DealDayAudit findRecentHistory(DealDay dealDay) {
		String[] parmNames = {"dealDay", "date" };
		Object[] parms =     {dealDay,   DateUtils.getValidToDateTime()};

		return (DealDayAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
