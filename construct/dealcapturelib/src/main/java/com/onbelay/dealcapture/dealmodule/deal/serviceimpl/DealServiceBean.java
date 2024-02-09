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
package com.onbelay.dealcapture.dealmodule.deal.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.assembler.AbstractDealAssembler;
import com.onbelay.dealcapture.dealmodule.deal.assembler.DealSnapshotAssemblerFactory;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.CreateDealFactory;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.ErrorDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service(value="dealService")
@Transactional
public class DealServiceBean extends BaseDomainService implements DealService {
	private static Logger logger =LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");
	@Autowired
	private DealRepository dealRepository;


	@Override
	public QuerySelectedPage findDealIds(DefinedQuery definedQuery) {

		List<Integer> dealIds =  dealRepository.findDealIds(definedQuery);
		return new QuerySelectedPage(
				dealIds,
				definedQuery.getOrderByClause());
	}

	@Override
	public List<BaseDealSnapshot> findByIds(QuerySelectedPage selectedPage) {
		List<BaseDeal> deals =  dealRepository.fetchByIds(selectedPage);
		return DealSnapshotAssemblerFactory.assemble(deals);
	}

	@Override
	public List<PhysicalDealSummary> findPhysicalDealSummariesByIds(List<Integer> physicalDealIds) {
		return dealRepository.findPhysicalDealSummariesByIds(physicalDealIds);
	}

	@Override
	public TransactionResult save(List<BaseDealSnapshot> snapshots) {

		ArrayList<Integer> ids = new ArrayList<>();
		
		for (BaseDealSnapshot snapshot: snapshots) {

			BaseDeal deal;
			
			if (snapshot.getEntityState() == EntityState.NEW) {
				 deal = CreateDealFactory.createDealFromSnapshot(snapshot);
				 deal.createWith(snapshot);
			} else {
				 deal = dealRepository.load(snapshot.getEntityId());
				 if (deal == null) {
					 logger.error(userMarker, "Deal id: {} is missing", snapshot.getEntityId());
					 throw new OBRuntimeException(DealErrorCode.INVALID_DEAL_ID.getCode());
				 }
				 logger.debug(userMarker, "Update deal # ", deal.getDealDetail().getTicketNo());
				 deal.updateWith(snapshot);
			}
			ids.add(deal.getId());
		}
		
		return new TransactionResult(ids);
	}

	@Override
	public void assignPositionIdentifierToDeals(
			String positionGenerationIdentifier,
			List<Integer> entityIds) {

		dealRepository.executeDealUpdateAssignForPositionGeneration(
				entityIds,
				positionGenerationIdentifier);
	}

	@Override
	public DealSummary getDealSummary(EntityId entityId) {
		return dealRepository.getDealSummary(entityId);
	}

	@Override
	public List<DealSummary> getAssignedDealSummaries(String positionGenerationIdentifier) {

		return dealRepository.findAssignedDealSummaries(positionGenerationIdentifier);
	}

	@Override
	public void updateDealPositionGenerationStatusToPending(List<Integer> dealIds) {
		dealRepository.executeDealUpdateSetPositionGenerationToPending(dealIds);
	}

	@Override
	public void updateDealPositionStatusToComplete(
			List<Integer> dealIds,
			LocalDateTime observedDateTime) {

		dealRepository.executeDealUpdatePositionGenerationToComplete(
				dealIds,
				observedDateTime);

	}

	@Override
	public TransactionResult save(BaseDealSnapshot snapshot) {

		BaseDeal deal;
		
		if (snapshot.getEntityState() == EntityState.NEW) {
			 deal = CreateDealFactory.createDealFromSnapshot(snapshot);
			 deal.createWith(snapshot);
		} else {
			 deal = dealRepository.load(snapshot.getEntityId());
			 if (deal == null) 
				 throw new OBRuntimeException(DealErrorCode.INVALID_DEAL_ID.getCode());
			 deal.updateWith(snapshot);
		}
		
		return new TransactionResult(deal.getId());
	}
	
	@Override
	public BaseDealSnapshot load(EntityId entityId) {

		BaseDeal deal = dealRepository.load(entityId);

		if (deal == null) {
			BaseDealSnapshot snapshot = new ErrorDealSnapshot(DealErrorCode.INVALID_DEAL_ID.getCode());
			snapshot.setErrorMessage("Invalid or missing deal id");
		}
		
		AbstractDealAssembler assembler = DealSnapshotAssemblerFactory.newAssembler(deal.getDealType());
		
		return assembler.assemble(deal);
	}


}
