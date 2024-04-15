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

@Component(value = "powerProfilePositionColumnDefinitions")
public class PowerProfilePositionColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{
	public static final ColumnDefinition powerProfileId = new ColumnDefinition("powerProfileId", ColumnDataType.STRING, "powerProfileId");
	public static final ColumnDefinition powerProfileName = new ColumnDefinition("powerProfileName", ColumnDataType.STRING, "powerProfile.detail.name");
	public static final ColumnDefinition powerFlowCode = new ColumnDefinition("powerFlowCode", ColumnDataType.STRING, "detail.powerFlowCodeValue");
	public static final ColumnDefinition startDate = new ColumnDefinition("startDate", ColumnDataType.STRING, "detail.startDate");
	public static final ColumnDefinition endDate = new ColumnDefinition("endDate", ColumnDataType.STRING, "detail.endDate");
	public static final ColumnDefinition createdDate = new ColumnDefinition("createdDate", ColumnDataType.STRING, "detail.createdDate");

	public PowerProfilePositionColumnDefinitions() {
		add(powerProfileId);
		add(powerProfileName);
		add(powerFlowCode);
		add(startDate);
		add(endDate);
		add(createdDate);
	}

	@Override
	public String getCodeName() {
		return powerProfileName.getPath();
	}

	@Override
	public String getDescriptionName() {
		return powerProfileName.getPath();
	}
}
