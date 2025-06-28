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
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.dealmodule.positions.adapter.PowerProfilePositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshotCollection;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name="Power Profile Positions", description="Power Profile Positions API.")
@RequestMapping("/api/powerProfile/positions")
public class PowerProfilePositionRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");
	
	@Autowired
	private PowerProfilePositionRestAdapter powerProfilePositionRestAdapter;


	@Operation(summary="get Positions")
	@GetMapping
	public ResponseEntity<PowerProfilePositionSnapshotCollection> getPositions(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {

		PowerProfilePositionSnapshotCollection collection;
		
		try {
			collection = powerProfilePositionRestAdapter.find(
				queryText, 
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new PowerProfilePositionSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new PowerProfilePositionSnapshotCollection(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new PowerProfilePositionSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<PowerProfilePositionSnapshotCollection>) processResponse(collection);
	}

	@Operation(summary="get an existing position")
	@GetMapping(value="/{id}")
	public ResponseEntity<PowerProfilePositionSnapshot> getPosition(
			@PathVariable Integer id) {

		PowerProfilePositionSnapshot snapshot;
		try {
			snapshot = powerProfilePositionRestAdapter.load(new EntityId(id));

			if (snapshot == null)
				return new ResponseEntity<PowerProfilePositionSnapshot>(HttpStatus.NOT_FOUND);

			HttpHeaders  headers = new HttpHeaders();
			headers.add(HttpHeaders.ETAG, "" + id);
			headers.add(HttpHeaders.LAST_MODIFIED, "V" + snapshot.getVersion());
		} catch (OBRuntimeException r) {
			snapshot = new PowerProfilePositionSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			snapshot = new PowerProfilePositionSnapshot(e.getMessage());
		}

		return (ResponseEntity<PowerProfilePositionSnapshot>) processResponse(snapshot);
	}


}
