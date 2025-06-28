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
package com.onbelay.dealcapture.job.model;

import com.onbelay.core.query.enums.ColumnDataType;
import com.onbelay.core.query.model.BaseColumnDefinitions;
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;
import org.springframework.stereotype.Component;

@Component(value = "dealJobColumnDefinitions")
public class DealJobColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{
	public static final ColumnDefinition jobId = new ColumnDefinition("jobId", ColumnDataType.INTEGER, "id");
	public static final ColumnDefinition currencyCode = new ColumnDefinition("currencyCode", ColumnDataType.STRING, "detail.currencyCodeValue");
	public static final ColumnDefinition createdDateTime = new ColumnDefinition("createdDateTime", ColumnDataType.DATE_TIME, "detail.createdDateTime");
	public static final ColumnDefinition valuedDateTime = new ColumnDefinition("valuedDateTime", ColumnDataType.DATE_TIME, "detail.valuedDateTime");
	public static final ColumnDefinition fromDate = new ColumnDefinition("fromDate", ColumnDataType.DATE, "detail.fromDate");
	public static final ColumnDefinition toDate = new ColumnDefinition("toDate", ColumnDataType.DATE, "detail.toDate");

	public DealJobColumnDefinitions() {
		add(jobId);
		add(createdDateTime);
		add(valuedDateTime);
		add(fromDate);
		add(toDate);
	}

	@Override
	public String getCodeName() {
		return jobId.getPath();
	}

	@Override
	public String getDescriptionName() {
		return jobId.getPath();
	}
}
