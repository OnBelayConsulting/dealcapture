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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDayDetail;
import jakarta.persistence.*;

@Entity
@Table (name = "DEAL_HOUR_BY_DAY_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = DealHourByDayAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM DealHourByDayAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.dealHourByDay = :dealHourByDay")
})
public class DealHourByDayAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "DealHourByDayAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private BaseDeal deal;
	private DealHourByDay dealHourByDay;
	private DealHourByDayDetail detail = new DealHourByDayDetail();

	protected DealHourByDayAudit() {

	}

	protected DealHourByDayAudit(DealHourByDay dealHourByDay) {
		this.dealHourByDay = dealHourByDay;
		this.deal = dealHourByDay.getDeal();
		this.detail.copyFrom(dealHourByDay.getDetail());
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="DealDayAuditGen", sequenceName="DEAL_DAY_BY_MTH_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DealDayAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealQuantityAuditId) {
		this.id = dealQuantityAuditId;
	}

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public DealHourByDay getDealHourByDay() {
		return dealHourByDay;
	}

	public void setDealHourByDay(DealHourByDay dealHourByDay) {
		this.dealHourByDay = dealHourByDay;
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
	public DealHourByDayDetail getDetail() {
		return detail;
	}


	private void setDetail(DealHourByDayDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return dealHourByDay;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		DealHourByDay dealHourByDay = (DealHourByDay) entity;
		this.deal = dealHourByDay.getDeal();
		this.detail.copyFrom(dealHourByDay.getDetail());
	}


	public static DealHourByDayAudit findRecentHistory(DealHourByDay dealHourByDay) {
		String[] parmNames = {"dealHourByDay", "date" };
		Object[] parms =     {dealHourByDay,   DateUtils.getValidToDateTime()};

		return (DealHourByDayAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
