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
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.assembler.DealCostAssembler;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealDayRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.*;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.repository.OrganizationRoleRepository;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BASE_DEAL")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
       name = DealRepositoryBean.FETCH_ALL_DEALS,
       query = "SELECT deal " +
			   "  FROM BaseDeal deal " +
       	     "ORDER BY deal.dealDetail.ticketNo DESC"),
    @NamedQuery(
       name = DealRepositoryBean.FETCH_ASSIGNED_DEAL_SUMMARIES,
       query = "SELECT new com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary( "
			   + "          deal.id, "
			   + "          deal.dealDetail.ticketNo, "
			   + "          deal.dealDetail.startDate,"
			   + "          deal.dealDetail.endDate,"
			   + "          deal.dealTypeValue, "
			   + "          deal.dealDetail.buySellCodeValue,"
			   + "          deal.dealDetail.reportingCurrencyCodeValue,"
			   + "          deal.dealDetail.costCurrencyCodeValue,"
			   + "		    deal.dealDetail.volumeQuantity,"
			   + "          deal.dealDetail.volumeUnitOfMeasureCodeValue,"
			   + "          deal.dealDetail.settlementCurrencyCodeValue)"
			   + "   FROM BaseDeal deal " +
			   " WHERE deal.dealDetail.positionGenerationIdentifier = :identifier " +
       	     "ORDER BY deal.dealDetail.ticketNo DESC"),
    @NamedQuery(
       name = DealRepositoryBean.GET_DEAL_SUMMARY,
       query = "SELECT new com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary( "
			   + "          deal.id, "
			   + "          deal.dealDetail.ticketNo, "
			   + "          deal.dealDetail.startDate,"
			   + "          deal.dealDetail.endDate,"
			   + "          deal.dealTypeValue, "
			   + "          deal.dealDetail.buySellCodeValue,"
			   + "          deal.dealDetail.reportingCurrencyCodeValue,"
			   + "          deal.dealDetail.costCurrencyCodeValue,"
			   + "		    deal.dealDetail.volumeQuantity,"
			   + "          deal.dealDetail.volumeUnitOfMeasureCodeValue,"
			   + "          deal.dealDetail.settlementCurrencyCodeValue)"
       		+ "   FROM BaseDeal deal " +
       	     "   WHERE deal.id = :dealId"),
    @NamedQuery(
       name = DealRepositoryBean.FIND_DEAL_BY_TICKET_NO,
       query = "SELECT deal " +
			   "  FROM BaseDeal deal " +
       	     "   WHERE deal.dealDetail.ticketNo = :ticketNo")
})
public abstract class BaseDeal extends TemporalAbstractEntity {
	
	private Integer id;
    private DealDetail dealDetail = new DealDetail();
    
	private String dealTypeValue;

    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;

	protected BaseDeal() {
	}

	protected BaseDeal(DealTypeCode dealType) {
		super();
		dealTypeValue = dealType.getCode();
	}
	
	public void createWith(BaseDealSnapshot snapshot) {
		super.createWith(snapshot);
		this.dealDetail.setDefaults();
		this.dealDetail.copyFrom(snapshot.getDealDetail());

		if (dealDetail.getCostCurrencyCode() == null)
			dealDetail.setCostCurrencyCode(dealDetail.getReportingCurrencyCode());

		this.dealTypeValue = snapshot.getDealType().getCode();
	}
	
	public void updateWith(BaseDealSnapshot snapshot) {
		this.dealDetail.copyFrom(snapshot.getDealDetail());
	}

	@Override
	protected void validate() throws OBValidationException {
		super.validate();
		
		dealDetail.validate();
		
		if (companyRole == null)
			throw new OBValidationException(DealErrorCode.MISSING_COMPANY_ROLE.getCode());
		
		if (counterpartyRole == null)
			throw new OBValidationException(DealErrorCode.MISSING_COUNTERPARTY_ROLE.getCode());
		
	}


	
	protected void setAssociationsFromSnapshot(BaseDealSnapshot snapshot) {
		
		if (snapshot.getCompanyRoleId() != null) {
			this.companyRole = (CompanyRole) getOrganizationRoleRepository().load(snapshot.getCompanyRoleId());
			if (this.companyRole == null) {
				this.companyRole = (CompanyRole) getOrganizationRoleRepository().getByShortNameAndRoleType(
						snapshot.getCompanyRoleId().getCode(),
						OrganizationRoleType.COMPANY_ROLE);
			}
		}
		
		if (snapshot.getCounterpartyRoleId() != null) {
			this.counterpartyRole = (CounterpartyRole) getOrganizationRoleRepository().load(snapshot.getCounterpartyRoleId());
			if (this.counterpartyRole == null) {
				this.counterpartyRole = (CounterpartyRole) getOrganizationRoleRepository().getByShortNameAndRoleType(
						snapshot.getCounterpartyRoleId().getCode(),
						OrganizationRoleType.COUNTERPARTY_ROLE);
			}
		}
	}
	

	@Id
    @Column(name="ENTITY_ID", insertable =  false, updatable = false)
    @SequenceGenerator(name="BaseDealGen", sequenceName="DEAL_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "BaseDealGen")

	public Integer getId() {
		return id;
	}

    private void setId(Integer dealId) {
		this.id = dealId;
	}


    public List<DealCostSnapshot> fetchCurrentDealCosts() {
    	DealCostAssembler assembler = new DealCostAssembler(this);
    	return assembler.assemble(
    			getDealCostRepository().fetchDealCosts(this.id));
    }

	@Transient
	public DealTypeCode getDealType() {
		return DealTypeCode.lookUp(dealTypeValue);
	}
	
	@Column(name = "DEAL_TYPE_CODE")
	private String getDealTypeValue() {
		return dealTypeValue;
	}

	private void setDealTypeValue(String dealTypeValue) {
		this.dealTypeValue = dealTypeValue;
	}
	
    
    @Embedded
    public DealDetail getDealDetail() {
		return dealDetail;
	}

	protected void setDealDetail(DealDetail dealDetail) {
		this.dealDetail = dealDetail;
	}

	protected void addDealCost(DealCost cost) {
		cost.setDeal(this);
		cost.save();
	}

	public List<Integer> saveDealCosts(List<DealCostSnapshot> costs) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (DealCostSnapshot snapshot : costs) {
			if (snapshot.getEntityState() == EntityState.NEW) {
				DealCost cost = DealCost.create(this, snapshot);
				ids.add(cost.getId());
			} else if (snapshot.getEntityState() == EntityState.MODIFIED) {
				DealCost cost = getDealCostRepository().load(snapshot.getEntityId());
				cost.updateWith(snapshot);
				ids.add(cost.getId());
			} else if (snapshot.getEntityState() == EntityState.DELETE) {
				DealCost cost = getDealCostRepository().load(snapshot.getEntityId());
				cost.delete();
			}
		}
		return ids;
	}

	protected void addDealDay(DealDay dealDay) {
		dealDay.setDeal(this);
		dealDay.save();
	}

	public List<Integer> saveDealDays(List<DealDaySnapshot> snapshots) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (DealDaySnapshot snapshot : snapshots) {
			if (snapshot.getEntityState() == EntityState.NEW) {
				DealDay dealDay = DealDay.create(this, snapshot);
				ids.add(dealDay.getId());
			} else if (snapshot.getEntityState() == EntityState.MODIFIED) {
				DealDay dealDay =  getDealDayRepository().load(snapshot.getEntityId());
				dealDay.updateWith(snapshot);
				ids.add(dealDay.getId());
			} else if (snapshot.getEntityState() == EntityState.DELETE) {
				DealDay dealDay =  getDealDayRepository().load(snapshot.getEntityId());
				dealDay.delete();
			}
		}
		return ids;
	}

	public List<DealDay> fetchDealDays() {
		return getDealDayRepository().fetchDealDays(id);
	}


	public List<DealDay> fetchDealDays(DayTypeCode code) {
		return getDealDayRepository().fetchDealDays(
				id,
				code);
	}


	public List<DealCost> fetchDealCosts() {
		return getDealCostRepository().fetchDealCosts(id);
	}

	public void updateDealWithPositionGenerationStatus(String positionGeneratorIdentifier) {

		dealDetail.setPositionGenerationStatusCode(PositionGenerationStatusCode.COMPLETE);
		dealDetail.setPositionGenerationIdentifier(positionGeneratorIdentifier);
		LocalDateTime currentDateTime = LocalDateTime.now();
		dealDetail.setPositionGenerationDateTime(currentDateTime);
	}


	@ManyToOne
	@JoinColumn(name ="COUNTERPARTY_ROLE_ID")
	public CounterpartyRole getCounterpartyRole() {
		return counterpartyRole;
	}

	private void setCounterpartyRole(CounterpartyRole counterpartyRole) {
		this.counterpartyRole = counterpartyRole;
	}

	@ManyToOne
	@JoinColumn(name ="COMPANY_ROLE_ID")
	public CompanyRole getCompanyRole() {
		return companyRole;
	}

	private void setCompanyRole(CompanyRole companyRole) {
		this.companyRole = companyRole;
	}

	
	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return BaseDealAudit.findRecentHistory(this);
	}

	@Override
	public EntityId generateEntityId() {
		return new EntityId(
				getId(),
				dealDetail.getTicketNo(),
				dealDetail.getTicketNo(),
				getIsExpired());
	}
	@Transient
	protected static DealRepositoryBean getEntityRepository() {
		return (DealRepositoryBean) ApplicationContextFactory.getBean(DealRepositoryBean.BEAN_NAME);
	}
	
	@Transient
	protected static  DealCostRepositoryBean getDealCostRepository() {
		return (DealCostRepositoryBean) ApplicationContextFactory.getBean(DealCostRepositoryBean.BEAN_NAME);
	}

	@Transient
	protected static DealDayRepository getDealDayRepository() {
		return (DealDayRepository) ApplicationContextFactory.getBean(DealDayRepositoryBean.BEAN_NAME);
	}

	@Transient
	protected static PriceIndexRepository getPriceIndexRepository() {
		return (PriceIndexRepository) ApplicationContextFactory.getBean(PriceIndexRepository.BEAN_NAME);
	}
	
	@Transient
	protected static OrganizationRoleRepository getOrganizationRoleRepository() {
		return (OrganizationRoleRepository) ApplicationContextFactory.getBean(OrganizationRoleRepository.BEAN_NAME);
	}
}
