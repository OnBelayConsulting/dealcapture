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
package com.onbelay.dealcapture.pricing.controller;

import com.onbelay.core.controller.BaseRestController;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.pricing.adapter.PricingIndexRestAdapter;
import com.onbelay.dealcapture.pricing.snapshot.IndexPriceSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshotCollection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name="pricingIndices", description="PricingIndices API")
@RequestMapping("/api/pricingIndices")
public class PricingIndexRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");

	@Autowired
	private PricingIndexRestAdapter pricingIndexRestAdapter;
	
	
	@Operation(summary="Create or update a pricingIndex")
	@RequestMapping(method=RequestMethod.POST, produces="application/json", consumes="application/json"  )
	public ResponseEntity<TransactionResult> savePricingIndex(
			@RequestHeader Map<String, String> headers,
			@RequestBody PricingIndexSnapshot snapshot,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = pricingIndexRestAdapter.save(snapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="fetch pricingIndices")
	@RequestMapping(method=RequestMethod.GET, produces="application/json" )
	public ResponseEntity<PricingIndexSnapshotCollection> fetchPricingIndices(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		PricingIndexSnapshotCollection collection;
		
		try {
			collection = pricingIndexRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new PricingIndexSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new PricingIndexSnapshotCollection(r.getMessage());
		} catch (RuntimeException r) {
			collection = new PricingIndexSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<PricingIndexSnapshotCollection>) processResponse(collection);
	}



	@Operation(summary="fetch index prices")
	@RequestMapping(value="/prices", method=RequestMethod.GET )
	public ResponseEntity<IndexPriceSnapshotCollection> getIndexPrices(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		IndexPriceSnapshotCollection collection;
		
		try {
			collection = pricingIndexRestAdapter.findPrices(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new IndexPriceSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new IndexPriceSnapshotCollection(r.getMessage());
		} catch (RuntimeException r) {
			collection = new IndexPriceSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<IndexPriceSnapshotCollection>) processResponse(collection);

	}


	
}
