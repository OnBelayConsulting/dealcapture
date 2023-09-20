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
import com.onbelay.dealcapture.dealmodule.deal.shared.DealCostDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSnapshot;

import jakarta.persistence.*;

@Entity
@Table (name = "DEAL_COST")
@NamedQueries({
    @NamedQuery(
       name = DealCostRepositoryBean.FETCH_DEAL_COSTS,
       query = "SELECT dealCost " +
			   "  FROM DealCost dealCost " +
       		 "   WHERE dealCost.deal.id = :dealId " +
       	     "ORDER BY dealCost.detail.name DESC"),
    @NamedQuery(
       name = DealCostRepositoryBean.FIND_BY_DEAL_AND_NAME,
       query = "SELECT dealCost " +
			   "  FROM DealCost dealCost " +
       		 "   WHERE dealCost.deal.id = :dealId " +
       	     "     AND dealCost.detail.name = :name ")
})
public class DealCost extends TemporalAbstractEntity {

	private Integer id;
	
	private BaseDeal deal;
	private DealCostDetail detail = new DealCostDetail();
	
	protected DealCost() {
		
	}
	
	public DealCost(BaseDeal deal) {
	}
	
	public static DealCost create(
			BaseDeal deal,
			DealCostSnapshot snapshot) {
		
		DealCost dealCost = new DealCost(deal);
		dealCost.createWith(snapshot);
		return dealCost;
		
	}

	protected void createWith(DealCostSnapshot snapshot) {
		super.createWith(snapshot);
		detail.copyFrom(snapshot.getDetail());
		deal.addDealCost(this);
	}


	@Id
    @Column(name="DEAL_COST_SK", insertable = false, updatable = false)
    @SequenceGenerator(name="DealCostGen", sequenceName="DEAL_COST_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "DealCostGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer dealCostId) {
		this.id = dealCostId;
	}


	
	@Override
	@Transient
	public String getEntityName() {
		return "DealCost";
	}

	@ManyToOne
	@JoinColumn(name ="DEAL_SK")

	public BaseDeal getDeal() {
		return deal;
	}

	public void setDeal(BaseDeal dea1) {
		this.deal = dea1;
	}

	@Embedded
	public DealCostDetail getDetail() {
		return detail;
	}


	private void setDetail(DealCostDetail detail) {
		this.detail = detail;
	}


	@Override
	protected AuditAbstractEntity createHistory() {
		return DealCostAudit.create(this);
	}


	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return DealCostAudit.findRecentHistory(this);
	}
	
	@Transient
	protected static  DealCostRepositoryBean getDealCostRepository() {
		return (DealCostRepositoryBean) ApplicationContextFactory.getBean(DealCostRepositoryBean.BEAN_NAME);
	}

}
