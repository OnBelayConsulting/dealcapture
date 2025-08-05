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
package com.onbelay.dealcapture.job.controller;

import com.onbelay.core.controller.BaseRestController;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.job.adapter.DealJobRestAdapter;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshotCollection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name="Jobs", description="Deal Jobs API .")
@RequestMapping("/api/jobs")
public class DealJobRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");
	
	@Autowired
	private DealJobRestAdapter dealJobRestAdapter;

	@Operation(summary="Create a position generation request.")
	@PostMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> createAndQueueDealJob(
			@RequestHeader Map<String, String> headers,
			@RequestBody DealJobSnapshot snapshot,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString()); 
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			  result = dealJobRestAdapter.createAndQueueDealJob(snapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}

	@Operation(summary="get jobs")
	@GetMapping(
			produces="application/json"
	)
	public ResponseEntity<DealJobSnapshotCollection> getJobs(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {

		DealJobSnapshotCollection collection;
		
		try {
			collection = dealJobRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new DealJobSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new DealJobSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new DealJobSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<DealJobSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="get an existing deal job")
	@GetMapping(value="/{id}")
	public ResponseEntity<DealJobSnapshot> getJob(
			@PathVariable Integer id) {

		DealJobSnapshot snapshot;
		try {
			snapshot = dealJobRestAdapter.load(new EntityId(id));

			if (snapshot == null)
				return new ResponseEntity<DealJobSnapshot>(HttpStatus.NOT_FOUND);

			HttpHeaders  headers = new HttpHeaders();
			headers.add(HttpHeaders.ETAG, "" + id);
			headers.add(HttpHeaders.LAST_MODIFIED, "V" + snapshot.getVersion());
		} catch (OBRuntimeException r) {
			snapshot = new DealJobSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			snapshot = new DealJobSnapshot(e.getMessage());
		}

		return (ResponseEntity<DealJobSnapshot>) processResponse(snapshot);
	}

	@Operation(summary="Cancel an existing job.")
	@PostMapping(
			value = "/{id}/cancelled",
			produces="application/json" )
	public ResponseEntity<TransactionResult> cancelDealJob(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id) {

		TransactionResult result;
		try {
			result = dealJobRestAdapter.cancelJob(new EntityId(id));
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Cancel failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="Delete an existing job.")
	@PostMapping(
			value = "/{id}/deleted",
			produces="application/json"  )
	public ResponseEntity<TransactionResult> deleteDealJob(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id) {


		TransactionResult result;
		try {
			result = dealJobRestAdapter.deleteJob(new EntityId(id));
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Cancel failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}

}
