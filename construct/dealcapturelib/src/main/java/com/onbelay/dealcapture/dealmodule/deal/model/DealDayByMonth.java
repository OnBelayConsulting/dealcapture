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
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthSnapshot;
import jakarta.persistence.*;

@Entity
@Table (name = "DEAL_DAY_BY_MTH")
@NamedQueries({
    @NamedQuery(
       name = DealDayByMonthRepositoryBean.FETCH_DEAL_DAYS,
       query = "SELECT dealDayByMonth " +
			   "  FROM DealDayByMonth dealDayByMonth " +
       		 "   WHERE dealDayByMonth.deal.id = :dealId " +
       	     "ORDER BY dealDayByMonth.detail.dayTypeCodeValue, " +
			   "       dealDayByMonth.detail.dealMonthDate "),
    @NamedQuery(
       name = DealDayByMonthRepositoryBean.FIND_BY_DEAL_AND_TYPE,
       query = "SELECT dealDayByMonth " +
			   "  FROM DealDayByMonth dealDayByMonth " +
       		 "   WHERE dealDayByMonth.deal.id = :dealId " +
       	     "     AND dealDayByMonth.detail.dayTypeCodeValue = :dayTypeCode " +
		   "  ORDER BY dealDayByMonth.detail.dealMonthDate")
})
public class DealDayByMonth extends TemporalAbstractEntity {

	private Integer id;

	private BaseDeal deal;
	private DealDayByMonthDetail detail = new DealDayByMonthDetail();
	
	protected DealDayByMonth() {
		
	}

	public static DealDayByMonth create(BaseDeal  deal, DealDayByMonthSnapshot snapshot) {
		DealDayByMonth dealDayByMonthByMonth = new DealDayByMonth();
		dealDayByMonthByMonth.createWith(deal, snapshot);
		return dealDayByMonthByMonth;
	}

	protected void createWith(
			BaseDeal deal,
			DealDayByMonthSnapshot snapshot) {

		super.createWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		if (detail.getDealDayTypeCode() != DayTypeCode.COST)
			detail.setDaySubTypeCodeValue("DEFAULT");
		deal.addDealDayByMonth(this);
	}

	public void updateWith(DealDayByMonthSnapshot snapshot) {
		super.updateWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		update();
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="DealDayMthGen", sequenceName="DEAL_DAY_BY_MTH_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DealDayMthGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealDayByMonthId) {
		this.id = dealDayByMonthId;
	}


	
	@Override
	@Transient
	public String getEntityName() {
		return "DealDayByMonth";
	}

	@ManyToOne
	@JoinColumn(name ="DEAL_ID")

	public BaseDeal getDeal() {
		return deal;
	}

	public void setDeal(BaseDeal dea1) {
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
	protected AuditAbstractEntity createHistory() {
		return new DealDayByMonthAudit(this);
	}


	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return DealDayByMonthAudit.findRecentHistory(this);
	}
	

}
