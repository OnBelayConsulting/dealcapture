/*
 * Copyright (c) 2018-2019 OnBelay Consulting
 * All Rights Reserved
*/
package com.onbelay.dealcapture.businesscontact.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;

import java.util.List;

public class BusinessContactSnapshot extends AbstractSnapshot {

	private BusinessContactDetail detail = new BusinessContactDetail();

	public BusinessContactSnapshot() {
	}

	public BusinessContactSnapshot(String errorCode) {
		super(errorCode);
	}

	public BusinessContactSnapshot(String errorCode, boolean isPermissionException) {
		super(errorCode, isPermissionException);
	}

	public BusinessContactSnapshot(String errorCode, List<String> parameters) {
		super(errorCode, parameters);
	}

	public BusinessContactDetail getDetail() {
		return detail;
	}

	public void setDetail(BusinessContactDetail detail) {
		this.detail = detail;
	}
	
	public String toString() {
		
		return getEntityId() + " " + detail.toString();
	}

}
