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

import com.onbelay.core.query.enums.ColumnDataType;
import com.onbelay.core.query.model.BaseColumnDefinitions;
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;
import org.springframework.stereotype.Component;

public class DealColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{
	public static final ColumnDefinition dealId = new ColumnDefinition("dealId", ColumnDataType.INTEGER, "id");
	public static final ColumnDefinition companyShortName = new ColumnDefinition("companyShortName", ColumnDataType.STRING, "companyRole.detail.shortName");
	public static final ColumnDefinition counterpartyShortName = new ColumnDefinition("counterpartyShortName", ColumnDataType.STRING, "counterpartyRole.detail.shortName");
	public static final ColumnDefinition dealType = new ColumnDefinition("dealType", ColumnDataType.STRING, "dealTypeValue");
	public static final ColumnDefinition commodity = new ColumnDefinition("commodity", ColumnDataType.STRING, "dealDetail.commodityCodeValue");
	public static final ColumnDefinition dealStatus = new ColumnDefinition("dealStatus", ColumnDataType.STRING, "dealDetail.dealStatusCodeValue");
	public static final ColumnDefinition ticketNo = new ColumnDefinition("ticketNo", ColumnDataType.STRING, "dealDetail.ticketNo");
	public static final ColumnDefinition buySellCode = new ColumnDefinition("buySellCode", ColumnDataType.STRING, "dealDetail.buySellCodeValue");
	public static final ColumnDefinition startDate = new ColumnDefinition("startDate", ColumnDataType.DATE, "dealDetail.startDate");
	public static final ColumnDefinition endDate = new ColumnDefinition("endDate", ColumnDataType.DATE, "dealDetail.endDate");

	public DealColumnDefinitions() {
		add(dealId);
		add(companyShortName);
		add(counterpartyShortName);
		add(dealType);
		add(dealStatus);
		add(commodity);
		add(ticketNo);
		add(buySellCode);
		add(startDate);
		add(endDate);
	}

	@Override
	public String getCodeName() {
		return ticketNo.getPath();
	}

	@Override
	public String getDescriptionName() {
		return ticketNo.getPath();
	}
}
