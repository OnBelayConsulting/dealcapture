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
package com.onbelay.dealcapture.job.serviceimpl;

import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.job.assembler.DealJobAssembler;
import com.onbelay.dealcapture.job.enums.JobActionCode;
import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.model.DealJob;
import com.onbelay.dealcapture.job.repository.DealJobRepository;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
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


@Service(value="dealJobService")
@Transactional
public class DealJobServiceBean extends BaseDomainService implements DealJobService {
	private static Logger logger =LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");

	@Autowired
	private DealJobRepository dealJobRepository;


	@Override
	public QuerySelectedPage findJobIds(DefinedQuery definedQuery) {

		List<Integer> dealIds =  dealJobRepository.findJobIds(definedQuery);
		return new QuerySelectedPage(
				dealIds,
				definedQuery.getOrderByClause());
	}

	@Override
	public List<DealJobSnapshot> findByIds(QuerySelectedPage selectedPage) {
		List<DealJob> jobs =  dealJobRepository.fetchByIds(selectedPage);
		DealJobAssembler assembler = new DealJobAssembler();
		return assembler.assemble(jobs);
	}

	@Override
	public void changeJobStatus(
			EntityId jobId,
			JobActionCode actionCode) {
		DealJob job = dealJobRepository.load(jobId);
		job.changeJobStatus(actionCode);
	}

	@Override
	public void startPositionGenerationExecution(
			EntityId jobId,
			LocalDateTime createdDateTime,
			String positionGenerationIdentifier,
			LocalDateTime startExecutionTime) {

		DealJob job = dealJobRepository.load(jobId);
		job.updateStartExecution(
				createdDateTime,
				positionGenerationIdentifier,
				startExecutionTime);
	}

	@Override
	public void endPositionGenerationExecution(
			EntityId jobId,
			LocalDateTime executionEndDateTime) {
		DealJob job = dealJobRepository.load(jobId);

		job.updateEndExecution(executionEndDateTime);
	}

	@Override
	public TransactionResult save(List<DealJobSnapshot> snapshots) {

		ArrayList<Integer> ids = new ArrayList<>();
		
		for (DealJobSnapshot snapshot: snapshots) {
			TransactionResult childResult = save(snapshot);
			if (childResult.getId() != null) {
				ids.add(childResult.getId());
			}
		}
		
		return new TransactionResult(ids);
	}


	@Override
	public TransactionResult save(DealJobSnapshot snapshot) {

		DealJob job;

		switch (snapshot.getEntityState()) {

			case NEW -> {
				job = new DealJob();
				job.createWith(snapshot);
				return new TransactionResult(job.getId());
			}
			case MODIFIED -> {
				job = dealJobRepository.load(snapshot.getEntityId());
				if (job == null) {
					logger.error(userMarker, "job id: {} is missing", snapshot.getEntityId());
					throw new OBRuntimeException(DealErrorCode.INVALID_DEAL_ID.getCode());
				}
				logger.debug(userMarker, "Update job # ", job.getId());
				job.updateWith(snapshot);
				return new TransactionResult(job.getId());
			}
			case DELETE -> {
				job = dealJobRepository.load(snapshot.getEntityId());
				if (job == null) {
					logger.error(userMarker, "job id: {} is missing", snapshot.getEntityId());
					throw new OBRuntimeException(DealErrorCode.INVALID_DEAL_ID.getCode());
				}
				job.delete();
				return new TransactionResult();
			}

			case UNMODIFIED -> {
				return new TransactionResult();
			}
		}
		return new TransactionResult();

	}
	
	@Override
	public DealJobSnapshot load(EntityId entityId) {

		DealJob job = dealJobRepository.load(entityId);
		DealJobAssembler assembler = new DealJobAssembler();
		return assembler.assemble(job);
	}

	@Override
	public void failJobExecution(
			EntityId jobId,
			String errorCode,
			String errorMessage,
			LocalDateTime executionEndDateTime) {

		DealJob job = dealJobRepository.load(jobId);

		job.updateToFailedExecution(
				errorCode,
				errorMessage,
				executionEndDateTime);

	}

	@Override
	public void startPositionValuationExecution(
			EntityId jobId,
			LocalDateTime valuationDateTime,
			LocalDateTime executionStartDateTime) {
		DealJob job = dealJobRepository.load(jobId);

    	job.updateStartValuation(
				valuationDateTime,
				executionStartDateTime);

	}

	@Override
	public void endPositionValuationExecution(
			EntityId jobId,
			LocalDateTime executionEndDateTime) {
		DealJob job = dealJobRepository.load(jobId);

		job.updateEndExecution(executionEndDateTime);

	}

}
