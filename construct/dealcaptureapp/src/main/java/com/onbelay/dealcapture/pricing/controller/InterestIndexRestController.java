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
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.pricing.adapter.InterestIndexRestAdapter;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshotCollection;
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
@Tag(name="interestIndices", description="InterestIndices API")
@RequestMapping("/api/interestIndices")
public class InterestIndexRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");

	@Autowired
	private InterestIndexRestAdapter interestIndexRestAdapter;
	
	
	@Operation(summary="Create or update a interestIndex")
	@PostMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveInterestIndex(
			@RequestHeader Map<String, String> headers,
			@RequestBody InterestIndexSnapshot snapshot,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = interestIndexRestAdapter.save(snapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="Create or update a interestIndex")
	@PutMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveInterestIndices(
			@RequestHeader Map<String, String> headers,
			@RequestBody List<InterestIndexSnapshot> snapshots,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = interestIndexRestAdapter.save(snapshots);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}



	@Operation(summary="Create or update interestCurves for a InterestIndex")
	@PutMapping(
			value = "/{id}/curves",
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveInterestCurves(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id,
			@RequestBody List<InterestCurveSnapshot> snapshots,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = interestIndexRestAdapter.saveInterestCurves(
					id,
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



	@Operation(summary="fetch interestIndices")
	@GetMapping(produces="application/json" )
	public ResponseEntity<InterestIndexSnapshotCollection> fetchInterestIndices(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		InterestIndexSnapshotCollection collection;
		
		try {
			collection = interestIndexRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new InterestIndexSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new InterestIndexSnapshotCollection(r.getMessage());
		} catch (RuntimeException r) {
			collection = new InterestIndexSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<InterestIndexSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="load interestIndex")
	@GetMapping(
			value = "/{id}",
			produces="application/json" )
	public ResponseEntity<InterestIndexSnapshot> loadInterestIndex(
			@PathVariable Integer id) {

		InterestIndexSnapshot snapshot;

		try {
			snapshot = interestIndexRestAdapter.load(new EntityId(id));
		} catch (OBRuntimeException r) {
			snapshot = new InterestIndexSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			snapshot = new InterestIndexSnapshot(r.getMessage());
		} catch (RuntimeException r) {
			snapshot = new InterestIndexSnapshot(r.getMessage());
		}

		return (ResponseEntity<InterestIndexSnapshot>) processResponse(snapshot);
	}


	@Operation(summary="fetch all index interests")
	@GetMapping(value="/curves" )
	public ResponseEntity<InterestCurveSnapshotCollection> getIndexInterests(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		InterestCurveSnapshotCollection collection;
		
		try {
			collection = interestIndexRestAdapter.findInterestCurves(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new InterestCurveSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new InterestCurveSnapshotCollection(r.getMessage());
		} catch (RuntimeException r) {
			collection = new InterestCurveSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<InterestCurveSnapshotCollection>) processResponse(collection);

	}


	
}
