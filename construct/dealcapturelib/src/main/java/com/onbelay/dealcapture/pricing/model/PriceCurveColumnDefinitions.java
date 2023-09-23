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

import com.onbelay.core.query.model.BaseColumnDefinitions;
import org.springframework.stereotype.Component;

import com.onbelay.core.query.enums.ColumnDataType;
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;

@Component(value = "indexPriceColumnDefinitions")
public class PriceCurveColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{

	public static final ColumnDefinition id = new ColumnDefinition("id", ColumnDataType.INTEGER, "id");
	public static final ColumnDefinition priceDate = new ColumnDefinition("priceDate", ColumnDataType.DATE, "detail.priceDate");
	public static final ColumnDefinition observedDateTime = new ColumnDefinition("observedDateTime", ColumnDataType.DATE_TIME, "detail.observedDateTime");
	public static final ColumnDefinition indexName = new ColumnDefinition("indexName", ColumnDataType.STRING, "pricingIndex.detail.name");
	
	public PriceCurveColumnDefinitions() {
		add(id);
		add(priceDate);
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
