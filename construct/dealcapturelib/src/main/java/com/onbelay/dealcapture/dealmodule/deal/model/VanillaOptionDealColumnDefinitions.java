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

public class VanillaOptionDealColumnDefinitions extends DealColumnDefinitions implements ColumnDefinitions{
	public static final ColumnDefinition tradeType = new ColumnDefinition("tradeType", ColumnDataType.STRING, "detail.tradeTypeCodeValue");
	public static final ColumnDefinition optionType = new ColumnDefinition("optionType", ColumnDataType.STRING, "detail.optionTypeCodeValue");
	public static final ColumnDefinition optionStyle = new ColumnDefinition("optionStyle", ColumnDataType.STRING, "detail.optionStyleCodeValue");
	public static final ColumnDefinition underlyingIndexName = new ColumnDefinition("underlyingIndexName", ColumnDataType.STRING, "underlyingPriceIndex.detail.name");

	public VanillaOptionDealColumnDefinitions() {
		add(tradeType);
		add(optionType);
		add(optionStyle);
		add(underlyingIndexName);
	}
}
