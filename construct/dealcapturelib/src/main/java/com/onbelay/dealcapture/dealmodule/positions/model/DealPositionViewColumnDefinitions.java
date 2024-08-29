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
package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.query.enums.ColumnDataType;
import com.onbelay.core.query.model.BaseColumnDefinitions;
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;
import org.springframework.stereotype.Component;

@Component(value = "dealPositionViewColumnDefinitions")
public class DealPositionViewColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{
	public static final ColumnDefinition startDate = new ColumnDefinition("startDate", ColumnDataType.DATE, "viewDetail.startDate");
	public static final ColumnDefinition endDate = new ColumnDefinition("endDate", ColumnDataType.DATE, "viewDetail.endDate");
	public static final ColumnDefinition dealType = new ColumnDefinition("dealType", ColumnDataType.STRING, "viewDetail.dealTypeCodeValue");
	public static final ColumnDefinition ticketNo = new ColumnDefinition("ticketNo", ColumnDataType.STRING, "viewDetail.ticketNo");
	public static final ColumnDefinition createdDateTime = new ColumnDefinition("createdDateTime", ColumnDataType.DATE_TIME, "viewDetail.createdDateTime");
	public static final ColumnDefinition currency = new ColumnDefinition("currency", ColumnDataType.STRING, "viewDetail.currencyCodeValue");

	public DealPositionViewColumnDefinitions() {
		add(startDate);
		add(endDate);
		add(dealType);
		add(ticketNo);
		add(createdDateTime);
		add(currency);
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
