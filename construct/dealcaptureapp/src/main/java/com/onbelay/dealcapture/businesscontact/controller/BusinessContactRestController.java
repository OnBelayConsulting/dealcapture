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
package com.onbelay.dealcapture.businesscontact.controller;

import com.onbelay.core.controller.BaseRestController;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.businesscontact.adapter.BusinessContactRestAdapter;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshot;
import com.onbelay.dealcapture.businesscontact.snapshot.BusinessContactSnapshotCollection;
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
@Tag(name="businessContacts", description="BusinessContacts API")
@RequestMapping("/api/businessContacts")
public class BusinessContactRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");

	@Autowired
	private BusinessContactRestAdapter businessContactRestAdapter;


	@Operation(summary="Create or update an businessContact")
	@PostMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveBusinessContact(
			@RequestHeader Map<String, String> headers,
			@RequestBody BusinessContactSnapshot snapshot,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error("Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}


		TransactionResult result;
		
		try {
			result = businessContactRestAdapter.save(snapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="fetch businessContacts")
	@GetMapping( )
	public ResponseEntity<BusinessContactSnapshotCollection> getBusinessContacts(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {

		BusinessContactSnapshotCollection collection;
		
		try {
			collection = businessContactRestAdapter.find(
				queryText,
				start,
				limit);
		} catch (OBRuntimeException r) {
			collection = new BusinessContactSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new BusinessContactSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new BusinessContactSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<BusinessContactSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="Get an businessContact")
	@GetMapping( value = "/{id}")
	public ResponseEntity<BusinessContactSnapshot> getBusinessContact(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id) {

		BusinessContactSnapshot snapshot ;

		try {
			snapshot = businessContactRestAdapter.load(new EntityId(id));
		} catch (OBRuntimeException r) {
			snapshot = new BusinessContactSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			snapshot = new BusinessContactSnapshot(r.getMessage());
		} catch (RuntimeException r) {
			snapshot = new BusinessContactSnapshot(r.getMessage());
		}

		return (ResponseEntity<BusinessContactSnapshot>) processResponse(snapshot);
	}



}
