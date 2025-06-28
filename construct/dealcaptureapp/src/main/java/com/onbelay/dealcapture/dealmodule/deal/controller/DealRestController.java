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
import com.onbelay.dealcapture.dealmodule.deal.adapter.DealRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
@Tag(name="deals", description="Deals API")
@RequestMapping("/api/deals")
public class DealRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");
	
	@Autowired
	private DealRestAdapter dealRestAdapter;


	@Operation(summary="Create or update a deal")
	@PostMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveDeal(
			@RequestHeader Map<String, String> headers,
			@RequestBody BaseDealSnapshot dealSnapshot,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString()); 
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}


		TransactionResult result;
		try {
			  result = dealRestAdapter.save(dealSnapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="Create or update deal costs.")
	@PostMapping(
			value = "/{id}/dealCosts",
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveDealCosts(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id,
			@RequestBody List<DealCostSnapshot> dealCostSnapshots,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}


		TransactionResult result;
		try {
			result = dealRestAdapter.saveDealCosts(id, dealCostSnapshots);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}



	@Operation(summary="Create or update deal costs.")
	@PostMapping(
			value = "/dealCosts",
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveDealCost(
			@RequestHeader Map<String, String> headers,
			@RequestBody DealCostSnapshot dealCostSnapshot,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}


		TransactionResult result;
		try {
			result = dealRestAdapter.saveDealCost(dealCostSnapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@RequestMapping(
			consumes = {
					MediaType.MULTIPART_FORM_DATA_VALUE
			},
			produces = "application/json",
			method = RequestMethod.POST
	)
	public ResponseEntity<TransactionResult> uploadDealFile(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "file-data", defaultValue = "") String fileData,
			@RequestParam("file") List<MultipartFile> submissions) {

		TransactionResult result;
		try {
			result = dealRestAdapter.saveFile(
					submissions.get(0).getOriginalFilename(),
					submissions.get(0).getBytes());
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		} catch (IOException e) {
			result = new TransactionResult(e.getMessage());
        }

        return processResponse(result);
	}

	
	@Operation(summary="Save deals")
	@PutMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveDeals(
			@RequestHeader Map<String, String> headers,
			@RequestBody List<BaseDealSnapshot> snapshots,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		TransactionResult result;
		try {
			result = dealRestAdapter.save(snapshots);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}
	
	
	@Operation(summary="get deals")
	@GetMapping()
	public ResponseEntity<DealSnapshotCollection> getDeals(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {
		
		DealSnapshotCollection collection;
		
		try {
			collection = dealRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new DealSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new DealSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new DealSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<DealSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="get deal costs")
	@GetMapping("/{id}/dealCosts")
	public ResponseEntity<DealCostSnapshotCollection> getDealCosts(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id) {

		DealCostSnapshotCollection collection;

		try {
			collection = dealRestAdapter.fetchDealCosts(id);
		} catch (OBRuntimeException r) {
			collection = new DealCostSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new DealCostSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new DealCostSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<DealCostSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="get an existing deal")
	@GetMapping(value="/{id}")
	public ResponseEntity<BaseDealSnapshot> getDeal(
			@PathVariable Integer id) {

		BaseDealSnapshot snapshot;
		try {
			snapshot = dealRestAdapter.load(new EntityId(id));

			if (snapshot == null)
				return new ResponseEntity<BaseDealSnapshot>(HttpStatus.NOT_FOUND);

			HttpHeaders  headers = new HttpHeaders();
			headers.add(HttpHeaders.ETAG, "" + id);
			headers.add(HttpHeaders.LAST_MODIFIED, "V" + snapshot.getVersion());
		} catch (OBRuntimeException r) {
			snapshot = new ErrorDealSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			snapshot = new ErrorDealSnapshot(e.getMessage());
		}

		return (ResponseEntity<BaseDealSnapshot>) processResponse(snapshot);
	}


	@Operation(summary="get an existing deal cost")
	@GetMapping(value="/dealCosts/{id}")
	public ResponseEntity<DealCostSnapshot> getDealCost(
			@PathVariable Integer id) {

		DealCostSnapshot snapshot;
		try {
			snapshot = dealRestAdapter.loadDealCost(new EntityId(id));

			if (snapshot == null)
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);

			HttpHeaders  headers = new HttpHeaders();
			headers.add(HttpHeaders.ETAG, "" + id);
			headers.add(HttpHeaders.LAST_MODIFIED, "V" + snapshot.getVersion());
		} catch (OBRuntimeException r) {
			snapshot = new DealCostSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			snapshot = new DealCostSnapshot(e.getMessage());
		}

		return (ResponseEntity<DealCostSnapshot>) processResponse(snapshot);
	}

	@Operation(summary="Run Mark to Market")
	@PostMapping(
			value = "/mtm",
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<MarkToMarketResult> runMarkToMarket(
			@RequestHeader Map<String, String> headers,
			@RequestBody MarkToMarketJobRequest request,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error(userMarker, "Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}


		MarkToMarketResult result;
		try {
			result = dealRestAdapter.queueMarkToMarketJobs(request);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new MarkToMarketResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new MarkToMarketResult(e.getMessage());
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", "application/json; charset=utf-8");
		return result.isSuccessful() ? new ResponseEntity<MarkToMarketResult>(result, httpHeaders, HttpStatus.OK) : new ResponseEntity<MarkToMarketResult>(result, httpHeaders, HttpStatus.BAD_REQUEST);
	}

}
