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
package com.onbelay.dealcapture.organization.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.organization.model.OrganizationRole;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;

public abstract class AbstractOrganizationRoleAssembler extends EntityAssembler {
	
	public abstract OrganizationRoleSnapshot assemble(OrganizationRole deal);
	
	public abstract OrganizationRole assembleEntity(OrganizationRoleSnapshot snapshot);

	
	protected void setCommonAttributes(OrganizationRole role, OrganizationRoleSnapshot snapshot) {
		
		setEntityAttributes(role, snapshot);
		
		snapshot.getRoleDetail().copyFrom(role.getRoleDetail());

	}
	

	
}
