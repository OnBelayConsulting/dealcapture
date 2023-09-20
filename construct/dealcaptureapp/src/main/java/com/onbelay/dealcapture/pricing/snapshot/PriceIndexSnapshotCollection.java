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
package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshotCollection;

import java.util.List;

public class PriceIndexSnapshotCollection extends AbstractSnapshotCollection<PriceIndexSnapshot> {

	public static final String ITEM_TYPE = "priceIndices";
	
	public PriceIndexSnapshotCollection() {
		super(ITEM_TYPE);
	}

	public PriceIndexSnapshotCollection(
			String errorMessage) {

		super(
				ITEM_TYPE,
				errorMessage);
	}

	public PriceIndexSnapshotCollection(
			String errorMessage,
			List<String> parms) {

		super(
				ITEM_TYPE,
				errorMessage,
				parms);
	}


	public PriceIndexSnapshotCollection(
			int start,
			int limit,
			int totalItems) {

		super(
				ITEM_TYPE,
				start,
				limit,
				totalItems);
	}

	public PriceIndexSnapshotCollection(
			int start,
			int limit,
			int totalItems,
			List<PriceIndexSnapshot> snapshots ) {

		super(
				ITEM_TYPE,
				start,
				limit,
				totalItems,
				snapshots);
	}

}