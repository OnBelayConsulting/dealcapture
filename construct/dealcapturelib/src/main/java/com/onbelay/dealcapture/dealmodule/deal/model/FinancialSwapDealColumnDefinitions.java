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
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;

public class FinancialSwapDealColumnDefinitions extends DealColumnDefinitions implements ColumnDefinitions{
	public static final ColumnDefinition paysValuation = new ColumnDefinition("paysValuation", ColumnDataType.STRING, "detail.paysValuationCodeValue");
	public static final ColumnDefinition receivesValuation = new ColumnDefinition("receivesValuation", ColumnDataType.STRING, "detail.receivesValuationCodeValue");
	public static final ColumnDefinition paysIndexName = new ColumnDefinition("paysIndexName", ColumnDataType.STRING, "paysPriceIndex.detail.name");
	public static final ColumnDefinition receivesIndexName = new ColumnDefinition("receivesIndexName", ColumnDataType.STRING, "receivesPriceIndex.detail.name");

	public FinancialSwapDealColumnDefinitions() {
		add(paysValuation);
		add(receivesValuation);
		add(paysIndexName);
		add(receivesIndexName);
	}
}
