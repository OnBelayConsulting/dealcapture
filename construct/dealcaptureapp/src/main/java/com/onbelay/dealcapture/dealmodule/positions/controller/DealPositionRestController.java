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
package com.onbelay.dealcapture.dealmodule.positions.controller;

import com.onbelay.core.controller.BaseRestController;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.dealmodule.positions.adapter.DealPositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name="Positions", description="Deal Positions API to support operations against all positions.")
@RequestMapping("/api/positions")
public class DealPositionRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");
	
	@Autowired
	private DealPositionRestAdapter dealPositionRestAdapter;


	@Operation(summary="Create or update a position")
	@PostMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> savePosition(
			@RequestHeader Map<String, String> headers,
			@RequestBody DealPositionSnapshot positionSnapshot,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString()); 
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			  result = dealPositionRestAdapter.save(
					  positionSnapshot.getDealId(),
					  positionSnapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}
	
	
	@Operation(summary="Save Positions")
	@PutMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> savePositions(
			@RequestHeader Map<String, String> headers,
			@RequestBody List<DealPositionSnapshot> snapshots,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result = dealPositionRestAdapter.save(snapshots);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}
	
	
	@Operation(summary="get Positions")
	@GetMapping(
			produces="application/json"
	)
	public ResponseEntity<DealPositionSnapshotCollection> getPositions(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {

		DealPositionSnapshotCollection collection;
		
		try {
			collection = dealPositionRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new DealPositionSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new DealPositionSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new DealPositionSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<DealPositionSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="get list of dateTimes when positions were created.")
	@GetMapping(
			value = "/createdDateTimes",
			produces="application/json"
	)
	public ResponseEntity<CreatedDateTimeCollection> getPositionCreatedDateTimes(
			@RequestHeader Map<String, String> headers) {

		CreatedDateTimeCollection collection;

		try {
			collection = dealPositionRestAdapter.getCreatedDateTimeCollection();
		} catch (OBRuntimeException r) {
			collection = new CreatedDateTimeCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new CreatedDateTimeCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new CreatedDateTimeCollection(r.getMessage());
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", "application/json; charset=utf-8");
		return collection.isSuccessful()
				? new ResponseEntity<CreatedDateTimeCollection>(collection, httpHeaders, HttpStatus.OK)
				: new ResponseEntity<CreatedDateTimeCollection>(collection, httpHeaders, HttpStatus.BAD_REQUEST);
	}


	@Operation(summary="get an existing position")
	@GetMapping(value="/{id}")
	public ResponseEntity<DealPositionSnapshot> getPosition(
			@PathVariable Integer id) {

		DealPositionSnapshot snapshot;
		try {
			snapshot = dealPositionRestAdapter.load(new EntityId(id));

			if (snapshot == null)
				return new ResponseEntity<DealPositionSnapshot>(HttpStatus.NOT_FOUND);

			HttpHeaders  headers = new HttpHeaders();
			headers.add(HttpHeaders.ETAG, "" + id);
			headers.add(HttpHeaders.LAST_MODIFIED, "V" + snapshot.getVersion());
		} catch (OBRuntimeException r) {
			snapshot = new ErrorDealPositionSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			snapshot = new ErrorDealPositionSnapshot(e.getMessage());
		}

		return (ResponseEntity<DealPositionSnapshot>) processResponse(snapshot);
	}


	@Operation(summary="get positions as a CSV file")
	@GetMapping(
			produces ="application/text")
	public HttpEntity<byte[]> getReportAsBytes(
			@RequestHeader Map<String, String> headersIn,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {


		FileReportResult reportResult;


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);


		try {
			reportResult = dealPositionRestAdapter.findPositionsAsCSV(
					queryText,
					start,
					limit);
			if (reportResult.isSuccessful()) {
				headers.set("Content-Disposition", "attachment; fileName=" + reportResult.getFileName());
				return new HttpEntity<>(reportResult.getDocumentInBytes(), headers);
			} else {
				return new HttpEntity<>(null, headers);
			}
		} catch (RuntimeException e) {
			return new HttpEntity<>(null, headers);
		}

	}


}
