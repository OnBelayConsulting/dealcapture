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
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDetail;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import jakarta.persistence.*;

@Entity
@Table(name = "BASE_DEAL_AUDIT")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
       name = BaseDealAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT baseDealAudit " +
			   "  FROM BaseDealAudit baseDealAudit " +
       		    "WHERE baseDealAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND baseDealAudit.deal = :deal")
})
public abstract class BaseDealAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "BaseDealAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;
	private DealDetail dealDetail = new DealDetail();

	private BaseDeal deal;
	private PowerProfile powerProfile;
	
	private String dealTypeValue;
	
	private CounterpartyRole counterpartyRole;
	private CompanyRole companyRole;

	private BusinessContact companyTrader;
	private BusinessContact counterpartyTrader;
	private BusinessContact administrator;


	protected BaseDealAudit() {
		
	}
	
	
	protected BaseDealAudit(BaseDeal deal) {
		super();
		this.deal = deal;
	}
	
	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="BaseAuditDealGen", sequenceName="DEAL_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "BaseAuditDealGen")

	public Integer getId() {
		return id;
	}

	public void setId(Integer dealAuditId) {
		this.id = dealAuditId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POWER_PROFILE_ID")
	public PowerProfile getPowerProfile() {
		return powerProfile;
	}

	public void setPowerProfile(PowerProfile powerProfile) {
		this.powerProfile = powerProfile;
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


	public void setDealDetail(DealDetail dealDetail) {
		this.dealDetail = dealDetail;
	}


	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public BaseDeal getDeal() {
		return deal;
	}


	private void setDeal(BaseDeal deal) {
		this.deal = deal;
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


	@ManyToOne
	@JoinColumn(name ="COMPANY_TRADER_ID")
	public BusinessContact getCompanyTrader() {
		return companyTrader;
	}

	public void setCompanyTrader(BusinessContact companyTrader) {
		this.companyTrader = companyTrader;
	}

	@ManyToOne
	@JoinColumn(name ="COUNTERPARTY_TRADER_ID")
	public BusinessContact getCounterpartyTrader() {
		return counterpartyTrader;
	}

	public void setCounterpartyTrader(BusinessContact counterpartyTrader) {
		this.counterpartyTrader = counterpartyTrader;
	}

	@ManyToOne
	@JoinColumn(name ="ADMINISTRATOR_ID")
	public BusinessContact getAdministrator() {
		return administrator;
	}


	public void setAdministrator(BusinessContact administrator) {
		this.administrator = administrator;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return deal;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		BaseDeal deal = (BaseDeal) entity;
		this.powerProfile = deal.getPowerProfile();
		this.dealTypeValue = deal.getDealType().getCode();
		this.companyRole = deal.getCompanyRole();
		this.counterpartyRole = deal.getCounterpartyRole();
		this.companyTrader = deal.getCompanyTrader();
		this.administrator = deal.getAdministrator();
		this.counterpartyTrader = deal.getCounterpartyTrader();
		this.getDealDetail().copyFrom(deal.getDealDetail());
	}


	public static BaseDealAudit findRecentHistory(BaseDeal deal) {
		String[] parmNames = {"deal", "date" };
		Object[] parms =     {deal,   DateUtils.getValidToDateTime()};

		return (BaseDealAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}
	
}
