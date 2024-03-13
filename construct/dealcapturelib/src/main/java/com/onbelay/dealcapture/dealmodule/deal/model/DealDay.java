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

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealDayRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDaySnapshot;

import jakarta.persistence.*;

@Entity
@Table (name = "DEAL_DAY")
@NamedQueries({
    @NamedQuery(
       name = DealDayRepositoryBean.FETCH_DEAL_DAYS,
       query = "SELECT dealDay " +
			   "  FROM DealDay dealDay " +
       		 "   WHERE dealDay.deal.id = :dealId " +
       	     "ORDER BY dealDay.detail.dayTypeCodeValue, " +
			   "       dealDay.detail.dealDayDate "),
    @NamedQuery(
       name = DealDayRepositoryBean.FIND_BY_DEAL_AND_TYPE,
       query = "SELECT dealDay " +
			   "  FROM DealDay dealDay " +
       		 "   WHERE dealDay.deal.id = :dealId " +
       	     "     AND dealDay.detail.dayTypeCodeValue = :dayTypeCode " +
		   "  ORDER BY dealDay.detail.dealDayDate")
})
public class DealDay extends TemporalAbstractEntity {

	private Integer id;

	private BaseDeal deal;
	private DealDayDetail detail = new DealDayDetail();
	
	protected DealDay() {
		
	}

	public static DealDay create(BaseDeal  deal, DealDaySnapshot snapshot) {
		DealDay dealDay = new  DealDay();
		dealDay.createWith(deal, snapshot);
		return dealDay;
	}

	protected void createWith(
			BaseDeal deal,
			DealDaySnapshot snapshot) {

		super.createWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		if (detail.getDealDayTypeCode() != DayTypeCode.COST)
			detail.setDaySubTypeCodeValue("DEFAULT");
		deal.addDealDay(this);
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="DealDayGen", sequenceName="DEAL_DAY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DealDayGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealDayId) {
		this.id = dealDayId;
	}


	
	@Override
	@Transient
	public String getEntityName() {
		return "DealDay";
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
	public DealDayDetail getDetail() {
		return detail;
	}


	private void setDetail(DealDayDetail detail) {
		this.detail = detail;
	}


	@Override
	protected AuditAbstractEntity createHistory() {
		return new DealDayAudit(this);
	}


	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return DealDayAudit.findRecentHistory(this);
	}
	
	@Transient
	protected static DealDayRepository getDealDayRepository() {
		return (DealDayRepository) ApplicationContextFactory.getBean(DealDayRepositoryBean.BEAN_NAME);
	}

}
