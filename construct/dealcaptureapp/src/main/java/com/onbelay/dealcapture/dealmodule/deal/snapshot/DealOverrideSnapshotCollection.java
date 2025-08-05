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
package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshotCollection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DealOverrideSnapshotCollection extends AbstractSnapshotCollection<DealOverrideDaySnapshot> {

	public static final String ITEM_TYPE = "deals";
	private LocalDate startDate;
	private LocalDate endDate;
	private List<String> headings = new ArrayList<>();

	public DealOverrideSnapshotCollection() {
		super(ITEM_TYPE);
	}


	public DealOverrideSnapshotCollection(
			String errorMessage) {

		super(
				ITEM_TYPE,
				errorMessage);
	}

	public DealOverrideSnapshotCollection(
			String errorMessage,
			List<String> parms) {

		super(
				ITEM_TYPE,
				errorMessage,
				parms);
	}


	public DealOverrideSnapshotCollection(
			int start,
			int limit,
			int totalItems) {

		super(
				ITEM_TYPE,
				start,
				limit,
				totalItems);
	}

	public DealOverrideSnapshotCollection(
			int start,
			int limit,
			int totalItems,
			List<DealOverrideDaySnapshot> snapshots ) {

		super(
				ITEM_TYPE,
				start,
				limit,
				totalItems,
				snapshots);
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public List<String> getHeadings() {
		return headings;
	}

	public void setHeadings(List<String> headings) {
		this.headings = headings;
	}
}
