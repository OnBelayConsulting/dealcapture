package com.onbelay.dealcapture.businesscontact.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.businesscontact.assembler.BusinessContactSnapshotAssembler;
import com.onbelay.dealcapture.businesscontact.enums.BusinessContactErrorCode;
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.repository.BusinessContactRepository;
import com.onbelay.dealcapture.businesscontact.service.BusinessContactService;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(value = "businessContactService")
@Transactional
public class BusinessContactServiceBean extends BaseDomainService implements BusinessContactService {

	@Autowired
	private BusinessContactRepository businessContactRepository;

	@Override
	public List<BusinessContactSnapshot> findByIds(QuerySelectedPage selectedPage) {
		List<BusinessContact> organizations = businessContactRepository.fetchByIds(selectedPage);
		BusinessContactSnapshotAssembler assembler = new BusinessContactSnapshotAssembler();
		return assembler.assemble(organizations);
	}

	@Override
	public QuerySelectedPage findBusinessContactIds(DefinedQuery definedQuery) {

		return new QuerySelectedPage(
			businessContactRepository.findBusinessContactIds(definedQuery),
			definedQuery.getOrderByClause());
	}

	@Override
	public BusinessContactSnapshot findByExternalReference(Integer id) {
		BusinessContact contact = businessContactRepository.findByExternalReference(id);
		if (contact == null) {
			return null;
		}
		BusinessContactSnapshotAssembler assembler = new BusinessContactSnapshotAssembler();

		return assembler.assemble(contact);
	}

	@Override
	public BusinessContactSnapshot load(EntityId entityId) {

		BusinessContact businessContact = businessContactRepository.load(entityId);
		if (businessContact == null)
			throw new OBRuntimeException(BusinessContactErrorCode.MISSING_BUSINESS_CONTACT.getCode());
		
		BusinessContactSnapshotAssembler assembler = new BusinessContactSnapshotAssembler();
		return assembler.assemble(businessContact);
	}

	@Override
	public TransactionResult save(BusinessContactSnapshot snapshot) {

		BusinessContact businessContact;
		
		if (snapshot.getEntityState() == EntityState.NEW) {

			businessContact = BusinessContact.create(snapshot);

		} else {
			businessContact = businessContactRepository.load(snapshot.getEntityId());
			if (businessContact == null)
				throw new OBRuntimeException(BusinessContactErrorCode.MISSING_BUSINESS_CONTACT.getCode());
			businessContact.updateWith(snapshot);
		}
		return new TransactionResult(businessContact.getId());
	}

	@Override
	public TransactionResult save(List<BusinessContactSnapshot> snapshots) {

		TransactionResult result = new TransactionResult();
		
		for (BusinessContactSnapshot snapshot : snapshots) {
			BusinessContact businessContact;
			
			if (snapshot.getEntityState() == EntityState.NEW) {

				businessContact = BusinessContact.create(snapshot);

			} else {
				businessContact = businessContactRepository.load(snapshot.getEntityId());
				businessContact.updateWith(snapshot);
			}
			result.getIds().add(businessContact.getId());
			
		}
		return result;
	}
	
}
