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
package com.onbelay.dealcapture.dealmodule.deal.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;

public abstract class AbstractDealAssembler extends EntityAssembler {
	
	public abstract BaseDealSnapshot assemble(BaseDeal deal);
	
	protected void setCommonAttributes(BaseDeal deal, BaseDealSnapshot snapshot) {
		
		setEntityAttributes(deal, snapshot);
		
		snapshot.getDealDetail().copyFrom(deal.getDealDetail());
		
		snapshot.setCompanyRoleId(
				deal.getCompanyRole().generateEntityId());

		
		snapshot.setCounterpartyRoleId(
				deal.getCounterpartyRole().generateEntityId());

	}
	

	
}
