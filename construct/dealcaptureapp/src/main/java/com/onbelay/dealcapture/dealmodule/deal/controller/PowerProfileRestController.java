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
package com.onbelay.dealcapture.dealmodule.deal.controller;

import com.onbelay.core.controller.BaseRestController;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.dealmodule.deal.adapter.PowerProfileRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshotCollection;
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

import java.util.List;
import java.util.Map;

@RestController
@Tag(name="powerProfiles", description="PowerProfiles API")
@RequestMapping("/api/powerProfiles")
public class PowerProfileRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");
	
	@Autowired
	private PowerProfileRestAdapter powerProfileRestAdapter;
	
	
	@Operation(summary="Create or update a powerProfile")
	@PostMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> savePowerProfile(
			@RequestHeader Map<String, String> headers,
			@RequestBody PowerProfileSnapshot powerProfileSnapshot,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString()); 
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}


		TransactionResult result;
		try {
			  result = powerProfileRestAdapter.save(powerProfileSnapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}
	
	
	@Operation(summary="Save powerProfiles")
	@PutMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> savePowerProfiles(
			@RequestHeader Map<String, String> headers,
			@RequestBody List<PowerProfileSnapshot> snapshots,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result = powerProfileRestAdapter.save(snapshots);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}
	
	
	@Operation(summary="get powerProfiles")
	@GetMapping()
	public ResponseEntity<PowerProfileSnapshotCollection> getPowerProfiles(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		PowerProfileSnapshotCollection collection;
		
		try {
			collection = powerProfileRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new PowerProfileSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new PowerProfileSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new PowerProfileSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<PowerProfileSnapshotCollection>) processResponse(collection);
	}

	@Operation(summary="get an existing powerProfile")
	@GetMapping(value="/{id}")
	public ResponseEntity<PowerProfileSnapshot> getPowerProfile(
			@PathVariable Integer id) {

		PowerProfileSnapshot snapshot;
		try {
			snapshot = powerProfileRestAdapter.load(new EntityId(id));

			if (snapshot == null)
				return new ResponseEntity<PowerProfileSnapshot>(HttpStatus.NOT_FOUND);

			HttpHeaders  headers = new HttpHeaders();
			headers.add(HttpHeaders.ETAG, "" + id);
			headers.add(HttpHeaders.LAST_MODIFIED, "V" + snapshot.getVersion());
		} catch (OBRuntimeException r) {
			snapshot = new PowerProfileSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			snapshot = new PowerProfileSnapshot(e.getMessage());
		}

		return (ResponseEntity<PowerProfileSnapshot>) processResponse(snapshot);
	}

}
