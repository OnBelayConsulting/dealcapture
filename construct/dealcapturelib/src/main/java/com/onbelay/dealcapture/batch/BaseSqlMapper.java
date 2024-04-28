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
package com.onbelay.dealcapture.batch;

public abstract class BaseSqlMapper implements OBSqlMapper {

	protected final  boolean isAddPrimaryKey;

	public BaseSqlMapper(boolean isAddPrimaryKey) {
		this.isAddPrimaryKey = isAddPrimaryKey;
	}

	public String createPlaceHolders() {
		if (getColumnNames().size() < 1)
			return "()";
		
		StringBuffer buffer = new StringBuffer("(?");
		
		for (int i=1; i < getColumnNames().size(); i++) {
			buffer.append(",?");
		}
		buffer.append(")");
		return buffer.toString();
	}

	@Override
	public boolean isAddPrimaryKey() {
		return isAddPrimaryKey;
	}
}
