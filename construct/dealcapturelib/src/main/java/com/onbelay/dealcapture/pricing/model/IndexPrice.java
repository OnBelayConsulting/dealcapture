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
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.shared.IndexPriceDetail;
import com.onbelay.dealcapture.pricing.snapshot.IndexPriceSnapshot;

import javax.persistence.*;

@Entity
@Table(name = "INDEX_PRICE")
@NamedQueries({
    @NamedQuery(
       name = IndexPriceRepositoryBean.FETCH_PRICE_BY_PRICE_DATE_OBS_DATE,
       query = "SELECT price "
       		+ "   FROM IndexPrice price " +
       	     "   WHERE price.pricingIndex.id = :pricingIndexId "
       	     + "   AND price.detail.priceDate = :priceDate "
       	     + "   AND price.detail.observedDateTime = "
       	     + "    (SELECT MAX(searchPrice.detail.observedDateTime)"
       	     + "       FROM IndexPrice searchPrice"
       	     + "      WHERE searchPrice.pricingIndex.id = :pricingIndexId"
       	     + "        AND searchPrice.detail.priceDate = :priceDate"
       	     + "        AND searchPrice.detail.observedDateTime <= :currentDateTime"
       	     + "     )  ")
    
})
public class IndexPrice extends TemporalAbstractEntity {
	
	private IndexPriceDetail detail = new IndexPriceDetail();

	private Integer id;

	private PricingIndex pricingIndex;
	
	
	protected  IndexPrice() {
	}
	
	public IndexPrice(
			PricingIndex pricingIndex, 
			IndexPriceSnapshot snapshot) {
		this.detail.copyFrom(snapshot.getDetail());
		this.pricingIndex = pricingIndex;
		save();
	}
	
	@Override
	@Transient
	public String getEntityName() {
		return "IndexPrice";
	}


	@Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="IndexPriceGen", sequenceName="INDEX_PRICE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "IndexPriceGen")
	public Integer getId() {
		return id;
	}

	private void setId(Integer pricingIndexId) {
		this.id = pricingIndexId;
	}


	@Embedded
	public IndexPriceDetail getDetail() {
		return detail;
	}


	public void setDetail(IndexPriceDetail detail) {
		this.detail = detail;
	}

	@ManyToOne
	@JoinColumn(name ="PRICING_INDEX_ID")
	public PricingIndex getPricingIndex() {
		return pricingIndex;
	}


	protected void setPricingIndex(PricingIndex pricingIndex) {
		this.pricingIndex = pricingIndex;
	}
	
	
	public void updateWith(IndexPriceSnapshot snapshot) {
		this.detail.copyFrom(snapshot.getDetail());
		update();
	}

	protected void validate() throws OBValidationException {
		detail.validate();
		
		if (this.pricingIndex == null)
			throw new OBValidationException(PricingErrorCode.MISSING_BASE_INDEX.getCode());
		
	}
	
	
	@Override
	protected AuditAbstractEntity createHistory() {
		IndexPriceAudit audit = IndexPriceAudit.create(this);
		audit.copyFrom(this);
		return audit;
	}

	@Override
	public AuditAbstractEntity fetchRecentHistory() {
		return IndexPriceAudit.findRecentHistory(this);
	}
}
