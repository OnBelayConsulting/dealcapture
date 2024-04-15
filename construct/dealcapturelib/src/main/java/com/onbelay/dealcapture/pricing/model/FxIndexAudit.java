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
import com.onbelay.dealcapture.pricing.snapshot.FxIndexDetail;
import jakarta.persistence.*;

@Entity
@Table(name = "FX_INDEX_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = FxIndexAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT audit " +
			   "  FROM FxIndexAudit audit " +
       		    "WHERE audit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND audit.fxIndex = :fxIndex")
})
public class FxIndexAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "FxIndexAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private FxIndex fxIndex;
	
	private FxIndexDetail detail = new FxIndexDetail();
	
	protected FxIndexAudit() {
		
	}
	
	protected static FxIndexAudit create(FxIndex fxIndex) {
		FxIndexAudit audit = new FxIndexAudit();
		audit.fxIndex = fxIndex;
		audit.copyFrom(fxIndex);
		return audit;
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="FxIndexAuditGen", sequenceName="FX_INDEX_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FxIndexAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer fxIndexId) {
		this.id = fxIndexId;
	}
	
	

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public FxIndex getFxIndex() {
		return fxIndex;
	}

	private void setFxIndex(FxIndex fxIndex) {
		this.fxIndex = fxIndex;
	}

	@Embedded
	public FxIndexDetail getDetail() {
		return detail;
	}

	private void setDetail(FxIndexDetail detail) {
		this.detail = detail;
	}

	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return fxIndex;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		FxIndex fxIndex = (FxIndex) entity;
		this.detail.copyFrom(fxIndex.getDetail());
	}


	public static FxIndexAudit findRecentHistory(FxIndex fxIndex) {
		String[] parmNames = {"fxIndex", "date" };
		Object[] parms =     {fxIndex,   DateUtils.getValidToDateTime()};

		return (FxIndexAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
