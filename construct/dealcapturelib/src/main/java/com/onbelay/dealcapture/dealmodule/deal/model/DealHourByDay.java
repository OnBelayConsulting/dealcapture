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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDayDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;
import jakarta.persistence.*;

@Entity
@Table (name = "DEAL_HOUR_BY_DAY")
@NamedQueries({
    @NamedQuery(
       name = DealHourByDayRepositoryBean.FETCH_DEAL_HOURS_BY_DAYS,
       query = "SELECT dealHour " +
			   "  FROM DealHourByDay dealHour " +
       		 "   WHERE dealHour.deal.id = :dealId " +
       	     "ORDER BY dealHour.detail.dayTypeCodeValue, " +
			   "       dealHour.detail.dealDayDate "),
    @NamedQuery(
       name = DealHourByDayRepositoryBean.FIND_BY_DEAL_AND_TYPE,
       query = "SELECT dealHour " +
			   "  FROM DealHourByDay dealHour " +
       		 "   WHERE dealHour.deal.id = :dealId " +
       	     "     AND dealHour.detail.dayTypeCodeValue = :dayTypeCode " +
		   "  ORDER BY dealHour.detail.dealDayDate")
})
public class DealHourByDay extends TemporalAbstractEntity {

	private Integer id;

	private BaseDeal deal;
	private DealHourByDayDetail detail = new DealHourByDayDetail();
	
	protected DealHourByDay() {
		
	}

	public static DealHourByDay create(BaseDeal  deal, DealHourByDaySnapshot snapshot) {
		DealHourByDay dealDay = new DealHourByDay();
		dealDay.createWith(deal, snapshot);
		return dealDay;
	}

	protected void createWith(
			BaseDeal deal,
			DealHourByDaySnapshot snapshot) {

		super.createWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		if (detail.getDealDayTypeCode() != DayTypeCode.COST)
			detail.setDaySubTypeCodeValue("DEFAULT");
		deal.addDealHourByDay(this);
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="DealHourDayGen", sequenceName="DEAL_HOUR_BY_DAY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DealHourDayGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealDayId) {
		this.id = dealDayId;
	}

	@Override
	@Transient
	public String getEntityName() {
		return "DealHourByDay";
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
	public DealHourByDayDetail getDetail() {
		return detail;
	}


	private void setDetail(DealHourByDayDetail detail) {
		this.detail = detail;
	}


	@Override
	protected AuditAbstractEntity createHistory() {
		return new DealHourByDayAudit(this);
	}


	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return DealHourByDayAudit.findRecentHistory(this);
	}

}
