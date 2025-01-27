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
package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VanillaOptionDealSnapshot extends BaseDealSnapshot {

	private OptionDealDetail detail = new OptionDealDetail();

	private EntityId underlyingPriceIndexId;

	public VanillaOptionDealSnapshot() {
		super(DealTypeCode.VANILLA_OPTION);
	}

	public VanillaOptionDealSnapshot(String errorCode) {
		super(
				DealTypeCode.VANILLA_OPTION,
				errorCode);
	}

	public VanillaOptionDealSnapshot(
			String errorCode,
			boolean isPermissionException) {

		super(
				DealTypeCode.VANILLA_OPTION,
				errorCode,
				isPermissionException);
	}

	public VanillaOptionDealSnapshot(
			String errorCode,
			List<String> parameters) {
		super(
				DealTypeCode.VANILLA_OPTION,
				errorCode,
				parameters);
	}


	public OptionDealDetail getDetail() {
		return detail;
	}

	public void setDetail(OptionDealDetail detail) {
		this.detail = detail;
	}

	public EntityId getUnderlyingPriceIndexId() {
		return underlyingPriceIndexId;
	}

	public void setUnderlyingPriceIndexId(EntityId underlyingPriceIndexId) {
		this.underlyingPriceIndexId = underlyingPriceIndexId;
	}
}
