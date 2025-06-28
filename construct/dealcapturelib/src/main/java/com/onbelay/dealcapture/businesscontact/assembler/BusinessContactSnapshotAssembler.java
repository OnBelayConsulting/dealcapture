package com.onbelay.dealcapture.businesscontact.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BusinessContactSnapshotAssembler extends EntityAssembler {
	
	
	public BusinessContactSnapshot assemble(BusinessContact businessContact) {
		
		BusinessContactSnapshot snapshot = new BusinessContactSnapshot();
		setEntityAttributes(businessContact, snapshot);
		
		snapshot.getDetail().copyFrom(businessContact.getDetail());
		
		return snapshot;
	}
	
	public List<BusinessContactSnapshot> assemble(List<BusinessContact> contacts) {
		
		
		ArrayList<BusinessContactSnapshot> snapshots = new ArrayList<BusinessContactSnapshot>();
		for (BusinessContact contact : contacts) {
			snapshots.add(
					assemble(contact));
		}
		
		return snapshots;
	}
	

}
