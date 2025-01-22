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

@Component(value = "interestIndexColumnDefinitions")
public class InterestIndexColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{

	public static final ColumnDefinition name = new ColumnDefinition("name", ColumnDataType.STRING, "detail.name");
	public static final ColumnDefinition description = new ColumnDefinition("description", ColumnDataType.STRING, "detail.description");
	public static final ColumnDefinition isRiskFreeRate = new ColumnDefinition("isRiskFreeRate", ColumnDataType.BOOLEAN, "detail.isRiskFreeRate");
	public static final ColumnDefinition frequency = new ColumnDefinition("frequency", ColumnDataType.STRING, "detail.frequencyCodeValue");

	public InterestIndexColumnDefinitions() {
		add(name);
		add(description);
		add(isRiskFreeRate);
		add(frequency);
	}

	@Override
	public String getCodeName() {
		return name.getPath();
	}

	@Override
	public String getDescriptionName() {
		return description.getPath();
	}
}
