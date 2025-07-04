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
package com.onbelay.dealcapture.pricing.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceCurveSnapshot extends AbstractSnapshot {

	private EntityId indexId;
	
	private CurveDetail detail = new CurveDetail();

	public PriceCurveSnapshot() {

	}

	public PriceCurveSnapshot(String errorCode) {
		super(errorCode);
	}

	public PriceCurveSnapshot(String errorCode, boolean isPermissionException) {
		super(errorCode, isPermissionException);
	}

	public PriceCurveSnapshot(String errorCode, List<String> parameters) {
		super(errorCode, parameters);
	}

	public EntityId getIndexId() {
		return indexId;
	}

	public void setIndexId(EntityId indexId) {
		this.indexId = indexId;
	}

	public CurveDetail getDetail() {
		return detail;
	}

	public void setDetail(CurveDetail detail) {
		this.detail = detail;
	}

}
