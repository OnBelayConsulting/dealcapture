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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.utils.DateUtils;
import com.onbelay.dealcapture.pricing.shared.IndexPriceDetail;

@Entity
@Table(name = "INDEX_PRICE_AUDIT")
@NamedQueries({
    @NamedQuery(
       name = IndexPriceAudit.FIND_AUDIT_BY_TO_DATE,
       query = "SELECT indexPriceAudit " +
			   "  FROM IndexPriceAudit indexPriceAudit " +
       		    "WHERE indexPriceAudit.historyDateTimeStamp.validToDateTime = :date " +
       		      "AND indexPriceAudit.indexPrice = :indexPrice")
})
public class IndexPriceAudit extends AuditAbstractEntity {
	public static final String FIND_AUDIT_BY_TO_DATE = "IndexPriceAudit.FIND_AUDIT_BY_TO_DATE";

	private Integer id;

	private IndexPrice indexPrice;
	
	private PricingIndex pricingIndex;

	private IndexPriceDetail detail = new IndexPriceDetail();
	
	
	protected IndexPriceAudit() {
		
	}
	
	protected static IndexPriceAudit create(PricingIndex pricingIndex) {
		IndexPriceAudit audit = new  IndexPriceAudit();
		audit.pricingIndex = pricingIndex;
		audit.copyFrom(pricingIndex);
		return audit;
	}

	@Id
    @Column(name="AUDIT_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="IndexPriceAuditGen", sequenceName="INDEX_PRICE_AUDIT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "IndexPriceAuditGen")
	public Integer getId() {
		return id;
	}

	public void setId(Integer pricingIndexId) {
		this.id = pricingIndexId;
	}
	
	

	@ManyToOne
	@JoinColumn(name ="ENTITY_ID")
	public IndexPrice getIndexPrice() {
		return indexPrice;
	}

	private void setIndexPrice(IndexPrice indexPrice) {
		this.indexPrice = indexPrice;
	}

	@ManyToOne
	@JoinColumn(name ="PRICING_INDEX_ID")
	public PricingIndex getPricingIndex() {
		return pricingIndex;
	}

	private void setPricingIndex(PricingIndex pricingIndex) {
		this.pricingIndex = pricingIndex;
	}

	@Embedded
	public IndexPriceDetail getDetail() {
		return detail;
	}

	private void setDetail(IndexPriceDetail detail) {
		this.detail = detail;
	}


	@Override
	@Transient
	public TemporalAbstractEntity getParent() {
		return indexPrice;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		IndexPrice indexPrice = (IndexPrice) entity;
		this.pricingIndex = indexPrice.getPricingIndex();
		this.detail.copyFrom(indexPrice.getDetail());
	}

	public static IndexPriceAudit create(IndexPrice indexPriceIn) {
		IndexPriceAudit audit = new IndexPriceAudit();
		audit.setIndexPrice(indexPriceIn);
		audit.copyFrom(indexPriceIn);
		return audit;
	}


	public static IndexPriceAudit findRecentHistory(IndexPrice pricingIndex) {
		String[] parmNames = {"indexPrice", "date" };
		Object[] parms =     {pricingIndex,   DateUtils.getValidToDateTime()};

		return (IndexPriceAudit) getAuditEntityRepository().executeSingleResultQuery(
				FIND_AUDIT_BY_TO_DATE,
				parmNames,
				parms);

	}


}
