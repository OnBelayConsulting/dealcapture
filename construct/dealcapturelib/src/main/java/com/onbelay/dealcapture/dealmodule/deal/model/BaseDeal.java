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
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.repository.BusinessContactRepository;
import com.onbelay.dealcapture.dealmodule.deal.assembler.DealCostAssembler;
import com.onbelay.dealcapture.dealmodule.deal.assembler.DealDayByMonthAssembler;
import com.onbelay.dealcapture.dealmodule.deal.assembler.DealHourByDayAssembler;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealDayByMonthRepository;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealHourByDayRepository;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.*;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.repository.OrganizationRoleRepository;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
       name = DealRepositoryBean.FIND_DEAL_BY_TICKET_NO,
       query = "SELECT deal " +
			   "  FROM BaseDeal deal " +
       	     "   WHERE deal.dealDetail.ticketNo = :ticketNo")
})
public abstract class BaseDeal extends TemporalAbstractEntity {
	private static final Logger logger = LogManager.getLogger();
	private Integer id;
    private DealDetail dealDetail = new DealDetail();
    private PowerProfile powerProfile;
	private String dealTypeValue;

    private CompanyRole companyRole;
    private CounterpartyRole counterpartyRole;

	private BusinessContact companyTrader;
	private BusinessContact counterpartyTrader;
	private BusinessContact administrator;

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

		if (companyTrader == null)
			throw new OBValidationException(DealErrorCode.MISSING_COMPANY_TRADER.getCode());

	}

	
	protected void updateRelationships(BaseDealSnapshot snapshot) {
		
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

		if (snapshot.getPowerProfileId() != null) {
			this.powerProfile = getPowerProfileRepository().load(snapshot.getPowerProfileId());
		}

		if (snapshot.getCompanyTraderId() != null) {
			this.companyTrader = getBusinessContactRepository().load(snapshot.getCompanyTraderId());
		}

		if (snapshot.getCounterpartyTraderId() != null) {
			this.counterpartyTrader = getBusinessContactRepository().load(snapshot.getCounterpartyTraderId());
		}

		if (snapshot.getAdministratorId() != null) {
			this.administrator = getBusinessContactRepository().load(snapshot.getAdministratorId());
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POWER_PROFILE_ID")
	public PowerProfile getPowerProfile() {
		return powerProfile;
	}

	public void setPowerProfile(PowerProfile powerProfile) {
		this.powerProfile = powerProfile;
	}

	public List<DealCostSnapshot> fetchCurrentDealCosts() {
    	DealCostAssembler assembler = new DealCostAssembler(this.generateEntityId());
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

	protected void addDealDayByMonth(DealDayByMonth dealDayByMonth) {
		dealDayByMonth.setDeal(this);
		dealDayByMonth.save();
	}

	public List<Integer> saveDealDayByMonths(List<DealDayByMonthSnapshot> snapshots) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (DealDayByMonthSnapshot snapshot : snapshots) {
			Integer id = saveDealDayByMonth(snapshot);
			if (id != null) {
				ids.add(id);
			}
		}
		return ids;
	}

	public Integer saveDealDayByMonth(DealDayByMonthSnapshot snapshot) {
		DealDayByMonth dealDayByMonth = null;
		if (snapshot.getDetail().areAllValuesNull()) {
			if (snapshot.getEntityState() == EntityState.NEW) {
				return null;
			}
			if (snapshot.getEntityState() == EntityState.MODIFIED) {
				snapshot.setEntityState(EntityState.DELETE);
			}
		}

		switch (snapshot.getEntityState()) {

			case NEW -> {
				 dealDayByMonth = DealDayByMonth.create(this, snapshot);
			}
			case MODIFIED -> {
				dealDayByMonth = getDealDayByMonthRepository().load(snapshot.getEntityId());
				dealDayByMonth.updateWith(snapshot);
			}

			case DELETE -> {
				dealDayByMonth = getDealDayByMonthRepository().load(snapshot.getEntityId());
				dealDayByMonth.delete();
			}
		}
		if (dealDayByMonth != null) {
			return dealDayByMonth.getId();
		} else {
			return null;
		}
	}


	public List<DealDayByMonth> fetchDealDayByMonths() {
		return getDealDayByMonthRepository().fetchDealDayByMonths(id);
	}


	public List<DealDayByMonth> fetchDealDayByMonths(DayTypeCode code) {
		return getDealDayByMonthRepository().fetchDealDayByMonths(
				id,
				code);
	}


	public List<Integer> saveDealHourByDays(List<DealHourByDaySnapshot> snapshots) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (DealHourByDaySnapshot snapshot : snapshots) {
			Integer id = saveDealHourByDay(snapshot);
			if (id != null) {
				ids.add(id);
			}
		}
		return ids;
	}

	public Integer saveDealHourByDay(DealHourByDaySnapshot snapshot) {
		switch (snapshot.getEntityState()) {

			case NEW -> {
				DealHourByDay dealHourByDay = DealHourByDay.create(this, snapshot);
				return dealHourByDay.getId();
			}
			case MODIFIED -> {
				DealHourByDay dealHourByDay = getDealHourByDayRepository().load(snapshot.getEntityId());
				dealHourByDay.updateWith(snapshot);
				return dealHourByDay.getId();
			}

			case DELETE -> {
				DealHourByDay dealHourByDay = getDealHourByDayRepository().load(snapshot.getEntityId());
				dealHourByDay.delete();
			}
		}
		return null;

	}

	public List<DealHourByDay> fetchDealHourByDays() {
		return getDealHourByDayRepository().fetchDealHourByDays(id);
	}


	public List<DealHourByDay> fetchDealHoursForADay(LocalDate dayDate) {
		return getDealHourByDayRepository().fetchDealHourByDayForADay(id, dayDate);
	}


	public List<String> fetchCostNames() {
		return getDealCostRepository().getDealCostNames(this.id);
	}


	public void saveDealOverridesByMonth(DealOverrideMonthSnapshot snapshot) {
		DealOverrideSnapshot dealOverrideSnapshot = new DealOverrideSnapshot();
		dealOverrideSnapshot.setHeadings(snapshot.getHeadings());
		dealOverrideSnapshot.addOverrideMonth(snapshot);
		saveDealOverrides(dealOverrideSnapshot);
	}

	public void saveHourlyDealOverrides(DealOverrideHoursForDaySnapshot snapshot) {
		HourlyOverrideLattice lattice = buildHourlyOverrideLattice(snapshot.getDayDate());
		List<String> dealCostNames = getDealCostRepository().getDealCostNames(this.id);
		int QUANTITY_IDX = snapshot.indexOfQuantityHeading();
		int PRICE_IDX = snapshot.indexOfPriceHeading();

		for (DealOverrideHourSnapshot hourOverride: snapshot.getOverrideHours()) {
			if (QUANTITY_IDX >= 0) {
				if (hourOverride.getValues().get(QUANTITY_IDX) != null) {
					if (lattice.getQuantityDealHourByDaySnapshot() == null) {
						lattice.setQuantityDealHourByDaySnapshot(new DealHourByDaySnapshot(snapshot.getDayDate(), (DayTypeCode.QUANTITY)));
					} else {
						if (lattice.getQuantityDealHourByDaySnapshot().getEntityState() == EntityState.UNMODIFIED)
							lattice.getQuantityDealHourByDaySnapshot().setEntityState(EntityState.MODIFIED);
					}
					lattice.getQuantityDealHourByDaySnapshot().getDetail().setHourValue(
							hourOverride.getHourEnding(),
							hourOverride.getValues().get(QUANTITY_IDX));
				} else {
					if (lattice.getQuantityDealHourByDaySnapshot() != null) {
						if (lattice.getQuantityDealHourByDaySnapshot().getEntityState() != EntityState.NEW) {
							lattice.getQuantityDealHourByDaySnapshot().setEntityState(EntityState.MODIFIED);
							lattice.getQuantityDealHourByDaySnapshot().getDetail().setHourValue(
									hourOverride.getHourEnding(),
									null);
						}
					}
				}
			}

			// Handle Price overrides
			if (PRICE_IDX >= 0) {
				if (hourOverride.getValues().get(PRICE_IDX) != null) {
					if (lattice.getPriceDealHourByDaySnapshot() == null) {
						lattice.setPriceDealHourByDaySnapshot(new DealHourByDaySnapshot(snapshot.getDayDate(), DayTypeCode.PRICE));
					} else {
						if (lattice.getPriceDealHourByDaySnapshot().getEntityState() == EntityState.UNMODIFIED)
							lattice.getPriceDealHourByDaySnapshot().setEntityState(EntityState.MODIFIED);
					}
					lattice.getPriceDealHourByDaySnapshot().getDetail().setHourValue(
							hourOverride.getHourEnding(),
							hourOverride.getValues().get(PRICE_IDX));
				} else {
					if (lattice.getPriceDealHourByDaySnapshot() != null) {
						if (lattice.getPriceDealHourByDaySnapshot().getEntityState() != EntityState.NEW) {
							lattice.getPriceDealHourByDaySnapshot().setEntityState(EntityState.MODIFIED);
							lattice.getPriceDealHourByDaySnapshot().getDetail().setHourValue(
									hourOverride.getHourEnding(),
									null);
						}
					}
				}
			}

			// Handle cost overrides. The overrides are in order of the headings.

			for (String costName : dealCostNames) {
				int costIdx = snapshot.indexOfCostHeading(costName);
				if (costIdx >= 0) {
					DealHourByDaySnapshot costByDay = lattice.getDealHourCosts().get(costName);
					if (costByDay == null) {
						costByDay = new DealHourByDaySnapshot(lattice.getDayDate(), DayTypeCode.COST);
						costByDay.getDetail().setDaySubTypeCodeValue(costName);

						lattice.getDealHourCosts().put(
								costName,
								costByDay);
					} else {
						if (costByDay.getEntityState() == EntityState.UNMODIFIED)
							costByDay.setEntityState(EntityState.MODIFIED);
					}
					BigDecimal costValue = hourOverride.getValues().get(costIdx);
					costByDay.getDetail().setHourValue(
							hourOverride.getHourEnding(),
							costValue);
				}

			}

		}
		if (lattice.getQuantityDealHourByDaySnapshot() != null) {
			saveDealHourByDay(lattice.getQuantityDealHourByDaySnapshot());
		}
		if (lattice.getPriceDealHourByDaySnapshot() != null) {
			saveDealHourByDay(lattice.getPriceDealHourByDaySnapshot());
		}
		for (DealHourByDaySnapshot costSnapshot : lattice.getDealHourCosts().values()) {
			saveDealHourByDay(costSnapshot);
		}

	}

	/**
	 * This method provides an easier way of managing deal overrides.
	 * Note that the headers in the DealOverrideSnapshot must match costs in this deal.
	 * @param dealOverrideSnapshot - describes one or more quantity, price or cost overrides by month and day.
	 */
	public void saveDealOverrides(DealOverrideSnapshot dealOverrideSnapshot) {
		List<String> dealCostNames = getDealCostRepository().getDealCostNames(this.id);
		int QUANTITY_IDX = dealOverrideSnapshot.indexOfQuantityHeading();
		int PRICE_IDX = dealOverrideSnapshot.indexOfPriceHeading();

		Map<LocalDate, OverrideLattice> overridesMap = buildOverrideLattice();


		// Update or create existing override snapshots
		for (DealOverrideMonthSnapshot monthOverride : dealOverrideSnapshot.getOverrideMonths()) {
			OverrideLattice lattice = overridesMap.get(monthOverride.getMonthDate());
			if (lattice == null) {
				lattice = new OverrideLattice();
				lattice.setMonthDate(monthOverride.getMonthDate());
				overridesMap.put(monthOverride.getMonthDate(), lattice);
			}
			for (DealOverrideDaySnapshot dayOverride : monthOverride.getOverrideDays()) {
				Integer dayOfMonth = dayOverride.getDayOfMonth();

				// Handle Quantity overrides
				if (QUANTITY_IDX >= 0) {
					if (dayOverride.getValues().get(QUANTITY_IDX) != null) {
						if (lattice.getQuantityDealDayByMonthSnapshot() == null) {
							lattice.setQuantityDealDayByMonthSnapshot(new DealDayByMonthSnapshot(DayTypeCode.QUANTITY, monthOverride.getMonthDate()));
						} else {
							if (lattice.getQuantityDealDayByMonthSnapshot().getEntityState() == EntityState.UNMODIFIED)
								lattice.getQuantityDealDayByMonthSnapshot().setEntityState(EntityState.MODIFIED);
						}
						lattice.getQuantityDealDayByMonthSnapshot().getDetail().setDayValue(
								dayOfMonth,
								dayOverride.getValues().get(QUANTITY_IDX));
					} else {
						if (lattice.getQuantityDealDayByMonthSnapshot() != null) {
							if (lattice.getQuantityDealDayByMonthSnapshot().getEntityState() != EntityState.NEW) {
								lattice.getQuantityDealDayByMonthSnapshot().setEntityState(EntityState.MODIFIED);
								lattice.getQuantityDealDayByMonthSnapshot().getDetail().setDayValue(
										dayOfMonth,
										null);
							}
						}
					}
				}

				// Handle Price overrides
				if (PRICE_IDX >= 0) {
					if (dayOverride.getValues().get(PRICE_IDX) != null) {
						if (lattice.getPriceDealDayByMonthSnapshot() == null) {
							lattice.setPriceDealDayByMonthSnapshot(new DealDayByMonthSnapshot(DayTypeCode.PRICE, monthOverride.getMonthDate()));
						} else {
							if (lattice.getPriceDealDayByMonthSnapshot().getEntityState() == EntityState.UNMODIFIED)
								lattice.getPriceDealDayByMonthSnapshot().setEntityState(EntityState.MODIFIED);
						}
						lattice.getPriceDealDayByMonthSnapshot().getDetail().setDayValue(
								dayOfMonth,
								dayOverride.getValues().get(PRICE_IDX));
					} else {
						if (lattice.getPriceDealDayByMonthSnapshot() != null) {
							if (lattice.getPriceDealDayByMonthSnapshot().getEntityState() != EntityState.NEW) {
								lattice.getPriceDealDayByMonthSnapshot().setEntityState(EntityState.MODIFIED);
								lattice.getPriceDealDayByMonthSnapshot().getDetail().setDayValue(
										dayOfMonth,
										null);
							}
						}
					}
				}

				// Handle cost overrides. The overrides are in order of the headings.

				for (String costName : dealCostNames) {
					int costIdx = dealOverrideSnapshot.indexOfCostHeading(costName);
					if (costIdx >= 0) {
						DealDayByMonthSnapshot costByMonth = lattice.getDealDayCosts().get(costName);
						if (costByMonth == null) {
							costByMonth = new DealDayByMonthSnapshot(DayTypeCode.COST, lattice.getMonthDate());
							costByMonth.getDetail().setDaySubTypeCodeValue(costName);

							lattice.getDealDayCosts().put(
									costName,
									costByMonth);
						} else {
							if (costByMonth.getEntityState() == EntityState.UNMODIFIED)
								costByMonth.setEntityState(EntityState.MODIFIED);
						}
						BigDecimal costValue = dayOverride.getValues().get(costIdx);
						costByMonth.getDetail().setDayValue(
								dayOfMonth,
								costValue);
					}

				}

			}

		}
		for (OverrideLattice updatedLattice : overridesMap.values()) {
			if (updatedLattice.getQuantityDealDayByMonthSnapshot() != null) {
				saveDealDayByMonth(updatedLattice.getQuantityDealDayByMonthSnapshot());
			}
			if (updatedLattice.getPriceDealDayByMonthSnapshot() != null) {
				saveDealDayByMonth(updatedLattice.getPriceDealDayByMonthSnapshot());
			}
			for (DealDayByMonthSnapshot costSnapshot : updatedLattice.getDealDayCosts().values()) {
				saveDealDayByMonth(costSnapshot);
			}
		}


	}

	private Map<LocalDate, OverrideLattice> buildOverrideLattice() {
		HashMap<LocalDate, OverrideLattice> overridesMap = new HashMap<>();
		DealDayByMonthAssembler assembler = new DealDayByMonthAssembler(this);
		// Prepare existing overrides to be updated (if exists)
		for (DealDayByMonth dd : getDealDayByMonthRepository().fetchDealDayByMonths(id)) {
			OverrideLattice overrideLattice = overridesMap.get(dd.getDetail().getDealMonthDate());
			if (overrideLattice == null) {
				overrideLattice = new OverrideLattice();
				overridesMap.put(dd.getDetail().getDealMonthDate(), overrideLattice);
			}

			switch (dd.getDetail().getDealDayTypeCode()) {
				case PRICE -> overrideLattice.setPriceDealDayByMonthSnapshot(assembler.assemble(dd));
				case QUANTITY -> overrideLattice.setQuantityDealDayByMonthSnapshot(assembler.assemble(dd));
				default -> overrideLattice.addCostDealDayByMonthSnapshot(assembler.assemble(dd));
			}

		}
		return overridesMap;
	}

	public List<OverrideLattice> fetchOverrideLattices() {
		DealDayByMonthAssembler assembler = new DealDayByMonthAssembler(this);
		HashMap<LocalDate, OverrideLattice> latticeHashMap = new HashMap<>();
		for (DealDayByMonth byMonth : fetchDealDayByMonths()) {
			OverrideLattice lattice = latticeHashMap.get(byMonth.getDetail().getDealMonthDate());
			if (lattice == null) {
				lattice = new OverrideLattice();
				lattice.setMonthDate(byMonth.getDetail().getDealMonthDate());
				latticeHashMap.put(byMonth.getDetail().getDealMonthDate(), lattice);
			}
			switch (byMonth.getDetail().getDealDayTypeCode()) {
				case PRICE -> lattice.setPriceDealDayByMonthSnapshot(assembler.assemble(byMonth));
				case QUANTITY -> lattice.setQuantityDealDayByMonthSnapshot(assembler.assemble(byMonth));
				default -> {
					lattice.getDealDayCosts().put(
							byMonth.getDetail().getDaySubTypeCodeValue(),
							assembler.assemble(byMonth));
				}
			}
		}
		ArrayList<OverrideLattice> lattices = new ArrayList<>();

		// First Lattice
		LocalDate currentDate = dealDetail.getStartDate().withDayOfMonth(1);
		OverrideLattice lattice = latticeHashMap.get(currentDate);
		if (lattice == null) {
			lattice = new OverrideLattice();
			lattice.setMonthDate(currentDate);
		}

		if (dealDetail.getStartDate().isAfter(currentDate))
			lattice.setMonthStartDate(dealDetail.getStartDate());
		else
			lattice.setMonthStartDate(currentDate);

		LocalDate endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
		if (dealDetail.getEndDate().isBefore(endOfMonth))
			lattice.setMonthEndDate(dealDetail.getEndDate());
		else
			lattice.setMonthEndDate(endOfMonth);


		lattices.add(lattice);
		currentDate = currentDate.withDayOfMonth(1);
		currentDate = currentDate.plusMonths(1);


		// Next lattices
		while (currentDate.isAfter(dealDetail.getEndDate()) == false) {
			lattice = latticeHashMap.get(currentDate);
			if (lattice == null) {
				lattice = new OverrideLattice();
				lattice.setMonthDate(currentDate);
			}
			lattice.setMonthStartDate(currentDate);
			endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
			if (dealDetail.getEndDate().isBefore(endOfMonth))
				lattice.setMonthEndDate(dealDetail.getEndDate());
			else
				lattice.setMonthEndDate(endOfMonth);
			lattices.add(lattice);
			currentDate = currentDate.plusMonths(1);
		}
		return lattices;
	}

	public HourlyOverrideLattice buildHourlyOverrideLattice(LocalDate dayDate) {
		DealHourByDayAssembler assembler = new DealHourByDayAssembler(this);
		HourlyOverrideLattice lattice = new HourlyOverrideLattice();
		lattice.setDayDate(dayDate);

		for (DealHourByDay byDay : fetchDealHourByDays()) {
			switch (byDay.getDetail().getDealDayTypeCode()) {
				case PRICE -> lattice.setPriceDealHourByDaySnapshot(assembler.assemble(byDay));
				case QUANTITY -> lattice.setQuantityDealHourByDaySnapshot(assembler.assemble(byDay));
				default -> {
					lattice.getDealHourCosts().put(
							byDay.getDetail().getDaySubTypeCodeValue(),
							assembler.assemble(byDay));
				}
			}

		}
		return lattice;
	}

	public List<DealCost> fetchDealCosts() {
		return getDealCostRepository().fetchDealCosts(id);
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
	protected static DealDayByMonthRepository getDealDayByMonthRepository() {
		return (DealDayByMonthRepository) ApplicationContextFactory.getBean(DealDayByMonthRepositoryBean.BEAN_NAME);
	}
	@Transient
	protected static DealHourByDayRepository getDealHourByDayRepository() {
		return (DealHourByDayRepository) ApplicationContextFactory.getBean(DealHourByDayRepository.BEAN_NAME);
	}

	@Transient
	protected static PriceIndexRepository getPriceIndexRepository() {
		return (PriceIndexRepository) ApplicationContextFactory.getBean(PriceIndexRepository.BEAN_NAME);
	}
	
	@Transient
	protected static OrganizationRoleRepository getOrganizationRoleRepository() {
		return (OrganizationRoleRepository) ApplicationContextFactory.getBean(OrganizationRoleRepository.BEAN_NAME);
	}


	@Transient
	protected static BusinessContactRepository getBusinessContactRepository() {
		return (BusinessContactRepository) ApplicationContextFactory.getBean(BusinessContactRepository.BEAN_NAME);
	}


	@Transient
	protected static PowerProfileRepository getPowerProfileRepository() {
		return (PowerProfileRepository) ApplicationContextFactory.getBean(PowerProfileRepository.BEAN_NAME);
	}


	public void addDealHourByDay(DealHourByDay dealHourByDay) {
		dealHourByDay.setDeal(this);
		dealHourByDay.save();
	}
}
