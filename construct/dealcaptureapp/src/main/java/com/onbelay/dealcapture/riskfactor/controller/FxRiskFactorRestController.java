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
package com.onbelay.dealcapture.riskfactor.controller;

import com.onbelay.core.controller.BaseRestController;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.riskfactor.adapter.FxRiskFactorRestAdapter;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshotCollection;
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

import java.util.List;
import java.util.Map;

@RestController
@Tag(name="fxRiskFactors", description="FxRiskFactors API")
@RequestMapping("/api/fxIndices")
public class FxRiskFactorRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");

	@Autowired
	private FxRiskFactorRestAdapter fxRiskFactorRestAdapter;

	@Operation(summary="Create or update a fxRiskFactor")
	@PutMapping(
			value = "/{id}/riskFactors",
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveFxRiskFactors(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id,
			@RequestBody List<FxRiskFactorSnapshot> snapshots,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = fxRiskFactorRestAdapter.save(
					new EntityId(id),
					snapshots);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="value fxRiskFactors based on a query")
	@PostMapping(
			value = "/riskFactors/valued",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> valueFxRiskFactors(
			@RequestParam(value = "query", defaultValue="default") String queryText) {

		TransactionResult result;
		try {
			result  = fxRiskFactorRestAdapter.valueRiskFactors(queryText);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}



	@Operation(summary="fetch fxRiskFactors")
	@GetMapping(
			value = "/riskFactors",
			produces="application/json" )
	public ResponseEntity<FxRiskFactorSnapshotCollection> findFxRiskFactors(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		FxRiskFactorSnapshotCollection collection;
		
		try {
			collection = fxRiskFactorRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new FxRiskFactorSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new FxRiskFactorSnapshotCollection(r.getMessage());
		} catch (RuntimeException r) {
			collection = new FxRiskFactorSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<FxRiskFactorSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="load fxRiskFactor")
	@GetMapping(
			value = "/riskFactors/{id}",
			produces="application/json" )
	public ResponseEntity<FxRiskFactorSnapshot> loadFxRiskFactor(
			@PathVariable Integer id) {

		FxRiskFactorSnapshot snapshot;

		try {
			snapshot = fxRiskFactorRestAdapter.load(new EntityId(id));
		} catch (OBRuntimeException r) {
			snapshot = new FxRiskFactorSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			snapshot = new FxRiskFactorSnapshot(r.getMessage());
		} catch (RuntimeException r) {
			snapshot = new FxRiskFactorSnapshot(r.getMessage());
		}

		return (ResponseEntity<FxRiskFactorSnapshot>) processResponse(snapshot);
	}

}
