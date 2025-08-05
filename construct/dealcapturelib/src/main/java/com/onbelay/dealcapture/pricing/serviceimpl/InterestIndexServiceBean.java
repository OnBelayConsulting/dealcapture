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
package com.onbelay.dealcapture.pricing.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.busmath.model.InterestRate;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.pricing.assembler.InterestCurveSnapshotAssembler;
import com.onbelay.dealcapture.pricing.assembler.InterestIndexSnapshotAssembler;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.InterestCurve;
import com.onbelay.dealcapture.pricing.model.InterestIndex;
import com.onbelay.dealcapture.pricing.repository.InterestCurveRepository;
import com.onbelay.dealcapture.pricing.repository.InterestIndexRepository;
import com.onbelay.dealcapture.pricing.service.InterestIndexService;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;
import com.onbelay.shared.enums.FrequencyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service(value = "interestIndexService")
@Transactional
public class InterestIndexServiceBean extends BaseDomainService implements InterestIndexService {

	@Autowired
	private InterestIndexRepository interestIndexRepository;
	
	@Autowired
	private InterestCurveRepository interestCurveRepository;


	@Override
	public InterestIndexSnapshot load(EntityId entityId) {

		InterestIndex interestIndex = interestIndexRepository.load(entityId);
		InterestIndexSnapshotAssembler assembler = new InterestIndexSnapshotAssembler();
		return assembler.assemble(interestIndex);
	}

	@Override
	public InterestRate getCurrentInterestRate(LocalDate currentDate) {
		InterestIndex interestIndex = interestIndexRepository.findInterestIndexByIsRiskFreeRate();
		if (interestIndex == null) {
			throw new OBRuntimeException(PricingErrorCode.MISSING_INTEREST_INDEX.getCode());
		}
		InterestCurve curve = interestCurveRepository.fetchCurrentInterestRate(
				interestIndex.generateEntityId(),
				currentDate,
				FrequencyCode.DAILY);

		if (curve != null)
			return new InterestRate(curve.getDetail().getCurveValue());

		curve = interestCurveRepository.fetchCurrentInterestRate(
					interestIndex.generateEntityId(),
					currentDate.withDayOfMonth(1),
					FrequencyCode.MONTHLY);

		if (curve == null)
			return new InterestRate(CalculatedErrorType.ERROR);
		else
			return new InterestRate(curve.getDetail().getCurveValue());

	}

	@Override
	public QuerySelectedPage findInterestIndexIds(DefinedQuery definedQuery) {

		return new QuerySelectedPage(
				interestIndexRepository.findInterestIndexIds(definedQuery),
				definedQuery.getOrderByClause());
	}

	@Override
	public List<InterestIndexSnapshot> findByIds(QuerySelectedPage selectedPage) {
		List<InterestIndex> indices = interestIndexRepository.fetchByIds(selectedPage);
		InterestIndexSnapshotAssembler assembler = new InterestIndexSnapshotAssembler();
		return assembler.assemble(indices);
	}

	@Override
	public InterestIndexSnapshot findInterestIndexByName(String indexName) {
		InterestIndex index = interestIndexRepository.findInterestIndexByName(indexName);
		if (index == null) {
			throw new OBRuntimeException(PricingErrorCode.MISSING_INTEREST_INDEX.getCode());
		}

		InterestIndexSnapshotAssembler assembler = new InterestIndexSnapshotAssembler();
		return assembler.assemble(index);
	}

	@Override
	public TransactionResult save(List<InterestIndexSnapshot> snapshots) {

		TransactionResult result = new TransactionResult();
		for (InterestIndexSnapshot snapshot : snapshots) {
			TransactionResult childResult = save(snapshot);
			if (childResult.getEntityId() != null)
				result.getIds().add(childResult.getId());
		}
		return result;
	}

	@Override
	public TransactionResult save(InterestIndexSnapshot snapshot) {

		InterestIndex interestIndex;

		if (snapshot.getEntityState() == EntityState.NEW) {
			interestIndex = InterestIndex.create(snapshot);
			return new TransactionResult(interestIndex.getId());
		} else if (snapshot.getEntityState() == EntityState.MODIFIED){
			interestIndex = interestIndexRepository.load(snapshot.getEntityId());
			interestIndex.updateWith(snapshot);
			return new TransactionResult(interestIndex.getId());
		} else if (snapshot.getEntityState() == EntityState.DELETE) {
			interestIndex = interestIndexRepository.load(snapshot.getEntityId());
			interestIndex.delete();
			return new TransactionResult();
		}

		return new TransactionResult();
	}


	/////////////// Interest Curves ///////////////////////


	@Override
	public QuerySelectedPage findInterestCurveIds(DefinedQuery definedQuery) {

		return new QuerySelectedPage(
				interestCurveRepository.findInterestCurveIds(definedQuery),
				definedQuery.getOrderByClause());
	}

	@Override
	public List<InterestCurveSnapshot> fetchInterestCurvesByIds(QuerySelectedPage querySelectedPage) {
		List<InterestCurve> interests = interestCurveRepository.fetchByIds(querySelectedPage);
		InterestCurveSnapshotAssembler assembler = new InterestCurveSnapshotAssembler();
		return assembler.assemble(interests);
	}


	@Override
	public TransactionResult saveInterestCurves(
			EntityId pricingIndexId,
			List<InterestCurveSnapshot> interests) {

		InterestIndex interestIndex = interestIndexRepository.load(pricingIndexId);
		
		return new TransactionResult(
				interestIndex.saveInterestCurves(interests));
	}
}
