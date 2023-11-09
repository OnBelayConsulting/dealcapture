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
package com.onbelay.dealcapture.riskfactor.model;

import com.onbelay.core.query.enums.ColumnDataType;
import com.onbelay.core.query.model.BaseColumnDefinitions;
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;
import org.springframework.stereotype.Component;

@Component(value = "riskFactorColumnDefinitions")
public class riskFactorColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{

	public static final ColumnDefinition marketDate = new ColumnDefinition("marketDate", ColumnDataType.DATE, "detail.marketDate");
	public static final ColumnDefinition indexId = new ColumnDefinition("indexId", ColumnDataType.DATE, "index.id");

	public riskFactorColumnDefinitions() {
		add(indexId);
		add(marketDate);
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