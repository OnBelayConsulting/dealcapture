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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthDetail;
import jakarta.persistence.*;

@Entity
@Table (name = "DEAL_DAY_BY_MTH_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = DealDayByMonthAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM DealDayByMonthAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.dealDayByMonth = :dealDayByMonth")
})
public class DealDayByMonthAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "DealDayByMonthAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private BaseDeal deal;
	private DealDayByMonth dealDayByMonth;
	private DealDayByMonthDetail detail = new DealDayByMonthDetail();

	protected DealDayByMonthAudit() {

	}

	protected DealDayByMonthAudit(DealDayByMonth dealDayByMonth) {
		this.dealDayByMonth = dealDayByMonth;
		this.deal = dealDayByMonth.getDeal();
		this.detail.copyFrom(dealDayByMonth.getDetail());
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
	public DealDayByMonth getDealDayByMonth() {
		return dealDayByMonth;
	}

	public void setDealDayByMonth(DealDayByMonth dealDayByMonth) {
		this.dealDayByMonth = dealDayByMonth;
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
	public DealDayByMonthDetail getDetail() {
		return detail;
	}


	private void setDetail(DealDayByMonthDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return dealDayByMonth;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		DealDayByMonth dealDayByMonth = (DealDayByMonth) entity;
		this.deal = dealDayByMonth.getDeal();
		this.detail.copyFrom(dealDayByMonth.getDetail());
	}


	public static DealDayByMonthAudit findRecentHistory(DealDayByMonth dealDayByMonth) {
		String[] parmNames = {"dealDayByMonth", "date" };
		Object[] parms =     {dealDayByMonth,   DateUtils.getValidToDateTime()};

		return (DealDayByMonthAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
