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
package com.onbelay.dealcapture.organization.model;

import com.onbelay.core.query.enums.ColumnDataType;
import com.onbelay.core.query.model.BaseColumnDefinitions;
import com.onbelay.core.query.model.ColumnDefinition;
import com.onbelay.core.query.model.ColumnDefinitions;
import org.springframework.stereotype.Component;

@Component(value = "organizationRoleColumnDefinitions")
public class OrganizationRoleColumnDefinitions extends BaseColumnDefinitions implements ColumnDefinitions{

	public static final ColumnDefinition organizationId = new ColumnDefinition("organizationId", ColumnDataType.INTEGER, "organization.id");
	public static final ColumnDefinition organizationShortName = new ColumnDefinition("shortName", ColumnDataType.STRING, "organization.detail.shortName");
	public static final ColumnDefinition organizationRoleType = new ColumnDefinition("organizationRoleType", ColumnDataType.STRING, "organizationRoleTypeCode");
	
	public OrganizationRoleColumnDefinitions() {
		add(organizationId);
		add(organizationShortName);
		add(organizationRoleType);
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
