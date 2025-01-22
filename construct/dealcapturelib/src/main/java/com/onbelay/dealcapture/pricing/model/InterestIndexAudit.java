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
package com.onbelay.dealcapture.pricing.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexDetail;
import jakarta.persistence.*;

@Entity
@Table(name = "INTEREST_INDEX_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = InterestIndexAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT interestIndexAudit " +
			   "  FROM InterestIndexAudit interestIndexAudit " +
       		    "WHERE interestIndexAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND interestIndexAudit.interestIndex = :interestIndex")
})
public class InterestIndexAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "InterestIndexAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private InterestIndex interestIndex;

	private InterestIndexDetail detail = new InterestIndexDetail();

	protected InterestIndexAudit() {
		
	}
	
	protected static InterestIndexAudit create(InterestIndex interestIndex) {
		InterestIndexAudit audit = new InterestIndexAudit();
		audit.interestIndex = interestIndex;
		audit.copyFrom(interestIndex);
		return audit;
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="InterestIndexAuditGen", sequenceName="INTEREST_INDEX_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "InterestIndexAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer interestIndexId) {
		this.id = interestIndexId;
	}
	
	

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public InterestIndex getInterestIndex() {
		return interestIndex;
	}

	private void setInterestIndex(InterestIndex interestIndex) {
		this.interestIndex = interestIndex;
	}

	@Embedded
	public InterestIndexDetail getDetail() {
		return detail;
	}

	private void setDetail(InterestIndexDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return interestIndex;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		InterestIndex interestIndex = (InterestIndex) entity;
		this.detail.copyFrom(interestIndex.getDetail());
	}


	public static InterestIndexAudit findRecentHistory(InterestIndex interestIndex) {
		String[] parmNames = {"interestIndex", "date" };
		Object[] parms =     {interestIndex,   DateUtils.getValidToDateTime()};

		return (InterestIndexAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
