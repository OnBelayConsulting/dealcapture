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
public class FinancialSwapDealSnapshot extends BaseDealSnapshot {

	private FinancialSwapDealDetail detail = new FinancialSwapDealDetail();

	private EntityId paysPriceIndexId;
	private EntityId receivesPriceIndexId;

	public FinancialSwapDealSnapshot() {
		super(DealTypeCode.FINANCIAL_SWAP);
	}

	public FinancialSwapDealSnapshot(String errorCode) {
		super(
				DealTypeCode.FINANCIAL_SWAP,
				errorCode);
	}

	public FinancialSwapDealSnapshot(
			String errorCode,
			boolean isPermissionException) {

		super(
				DealTypeCode.FINANCIAL_SWAP,
				errorCode,
				isPermissionException);
	}

	public FinancialSwapDealSnapshot(
			String errorCode,
			List<String> parameters) {
		super(
				DealTypeCode.FINANCIAL_SWAP,
				errorCode,
				parameters);
	}


	public FinancialSwapDealDetail getDetail() {
		return detail;
	}

	public void setDetail(FinancialSwapDealDetail detail) {
		this.detail = detail;
	}

	public EntityId getPaysPriceIndexId() {
		return paysPriceIndexId;
	}

	public void setPaysPriceIndexId(EntityId paysPriceIndexId) {
		this.paysPriceIndexId = paysPriceIndexId;
	}

	public EntityId getReceivesPriceIndexId() {
		return receivesPriceIndexId;
	}

	public void setReceivesPriceIndexId(EntityId receivesPriceIndexId) {
		this.receivesPriceIndexId = receivesPriceIndexId;
	}
}
