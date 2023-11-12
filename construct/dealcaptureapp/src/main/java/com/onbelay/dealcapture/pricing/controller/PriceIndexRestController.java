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
import com.onbelay.dealcapture.pricing.adapter.PriceIndexRestAdapter;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshotCollection;
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
@Tag(name="priceIndices", description="PriceIndices API")
@RequestMapping("/api/priceIndices")
public class PriceIndexRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");

	@Autowired
	private PriceIndexRestAdapter priceIndexRestAdapter;
	
	
	@Operation(summary="Create or update a priceIndex")
	@RequestMapping(method=RequestMethod.POST, produces="application/json", consumes="application/json"  )
	public ResponseEntity<TransactionResult> savePriceIndex(
			@RequestHeader Map<String, String> headers,
			@RequestBody PriceIndexSnapshot snapshot,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = priceIndexRestAdapter.save(snapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="fetch priceIndices")
	@RequestMapping(method=RequestMethod.GET, produces="application/json" )
	public ResponseEntity<PriceIndexSnapshotCollection> fetchPriceIndices(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		PriceIndexSnapshotCollection collection;
		
		try {
			collection = priceIndexRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new PriceIndexSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new PriceIndexSnapshotCollection(r.getMessage());
		} catch (RuntimeException r) {
			collection = new PriceIndexSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<PriceIndexSnapshotCollection>) processResponse(collection);
	}



	@Operation(summary="fetch index prices")
	@RequestMapping(value="/prices", method=RequestMethod.GET )
	public ResponseEntity<PriceCurveSnapshotCollection> getIndexPrices(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		PriceCurveSnapshotCollection collection;
		
		try {
			collection = priceIndexRestAdapter.findPrices(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new PriceCurveSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new PriceCurveSnapshotCollection(r.getMessage());
		} catch (RuntimeException r) {
			collection = new PriceCurveSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<PriceCurveSnapshotCollection>) processResponse(collection);

	}


	
}
