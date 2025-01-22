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
package com.onbelay.dealcapture.pricing.model;

import com.onbelay.core.query.enums.ColumnDataType;
import com.onbelay.core.query.model.BaseColumnDefinitions;
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;
import org.springframework.stereotype.Component;

@Component(value = "interestCurveColumnDefinitions")
public class InterestCurveColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{

	public static final ColumnDefinition id = new ColumnDefinition("id", ColumnDataType.INTEGER, "id");
	public static final ColumnDefinition interestIndexId = new ColumnDefinition("interestIndexId", ColumnDataType.INTEGER, "interestIndex.id");
	public static final ColumnDefinition frequencyCode = new ColumnDefinition("frequencyCode", ColumnDataType.STRING, "detail.frequencyCodeValue");
	public static final ColumnDefinition curveDate = new ColumnDefinition("curveDate", ColumnDataType.DATE, "detail.curveDate");
	public static final ColumnDefinition observedDateTime = new ColumnDefinition("observedDateTime", ColumnDataType.DATE_TIME, "detail.observedDateTime");
	public static final ColumnDefinition indexName = new ColumnDefinition("indexName", ColumnDataType.STRING, "interestIndex.detail.name");
	
	public InterestCurveColumnDefinitions() {
		add(id);
		add(interestIndexId);
		add(frequencyCode);
		add(curveDate);
		add(observedDateTime);
		add(indexName);
	}

	@Override
	public String getCodeName() {
		return null;
	}

	@Override
	public String getDescriptionName() {
		return null;
	}
}
