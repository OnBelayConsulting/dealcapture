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
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.pricing.adapter.FxIndexRestAdapter;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshotCollection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name="fxIndices", description="FxIndices API")
@RequestMapping("/api/fxIndices")
public class FxIndexRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");

	@Autowired
	private FxIndexRestAdapter fxIndexRestAdapter;
	
	
	@Operation(summary="Create or update a fxIndex")
	@PostMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveFxIndex(
			@RequestHeader Map<String, String> headers,
			@RequestBody FxIndexSnapshot snapshot,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = fxIndexRestAdapter.save(snapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="Create or update a fxIndex")
	@PutMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveFxIndices(
			@RequestHeader Map<String, String> headers,
			@RequestBody List<FxIndexSnapshot> snapshots,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = fxIndexRestAdapter.save(snapshots);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}



	@Operation(summary="Create or update fx rates(curves) for a FxIndex")
	@PutMapping(
			value = "/{id}/curves",
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveFxCurves(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id,
			@RequestBody List<FxCurveSnapshot> snapshots,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result  = fxIndexRestAdapter.saveFxCurves(
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



	@Operation(summary="fetch fxIndices")
	@GetMapping(produces="application/json" )
	public ResponseEntity<FxIndexSnapshotCollection> fetchFxIndices(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		FxIndexSnapshotCollection collection;
		
		try {
			collection = fxIndexRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new FxIndexSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new FxIndexSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());

		} catch (RuntimeException r) {
			collection = new FxIndexSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<FxIndexSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="load fxIndex")
	@GetMapping(
			value = "/{id}",
			produces="application/json" )
	public ResponseEntity<FxIndexSnapshot> loadFxIndex(
			@PathVariable Integer id) {

		FxIndexSnapshot snapshot;

		try {
			snapshot = fxIndexRestAdapter.load(new EntityId(id));
		} catch (OBRuntimeException r) {
			snapshot = new FxIndexSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			snapshot = new FxIndexSnapshot(r.getMessage());
		} catch (RuntimeException r) {
			snapshot = new FxIndexSnapshot(r.getMessage());
		}

		return (ResponseEntity<FxIndexSnapshot>) processResponse(snapshot);
	}


	@Operation(summary="fetch all fx rates (curves)")
	@GetMapping(value="/curves" )
	public ResponseEntity<FxCurveSnapshotCollection> getFxCurves(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		FxCurveSnapshotCollection collection;
		
		try {
			collection = fxIndexRestAdapter.findFxCurves(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new FxCurveSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new FxCurveSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new FxCurveSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<FxCurveSnapshotCollection>) processResponse(collection);

	}


	@RequestMapping(
			value = "rates",
			consumes = {
					MediaType.MULTIPART_FORM_DATA_VALUE
			},
			produces = "application/json",
			method = RequestMethod.POST
	)
	public ResponseEntity<TransactionResult> uploadFxCurves(
			@RequestHeader Map<String, String> headers,
			@RequestParam("file") List<MultipartFile> submissions) {

		TransactionResult result;
		try {
			result = fxIndexRestAdapter.saveFxCurvesFile(
					submissions.get(0).getOriginalFilename(),
					submissions.get(0).getBytes());
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (IOException | RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}



}
