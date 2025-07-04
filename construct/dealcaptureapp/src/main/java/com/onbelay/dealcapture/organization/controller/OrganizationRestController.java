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
package com.onbelay.dealcapture.organization.controller;

import com.onbelay.core.controller.BaseRestController;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.exception.DefinedQueryException;
import com.onbelay.dealcapture.organization.adapter.OrganizationRestAdapter;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSummaryCollection;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationSnapshotCollection;
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
@Tag(name="organizations", description="Organizations API")
@RequestMapping("/api/organizations")
public class OrganizationRestController extends BaseRestController {
	private static Logger logger = LogManager.getLogger();
	private static final Marker userMarker = MarkerManager.getMarker("USER");

	@Autowired
	private OrganizationRestAdapter organizationRestAdapter;


	@Operation(summary="Create or update an organization")
	@PostMapping(
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveOrganization(
			@RequestHeader Map<String, String> headers,
			@RequestBody OrganizationSnapshot snapshot,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error("Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}


		TransactionResult result;
		
		try {
			result = organizationRestAdapter.save(snapshot);
		} catch (OBRuntimeException r) {
			logger.error(userMarker,"Create/update failed ", r.getErrorCode(), r);
			result = new TransactionResult(r.getErrorCode(), r.getParms());
			result.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (RuntimeException e) {
			result = new TransactionResult(e.getMessage());
		}

		return processResponse(result);
	}


	@Operation(summary="Create or update an organization roles for an organization.")
	@PostMapping(
			value = "/{id}/roles",
			produces="application/json",
			consumes="application/json"  )
	public ResponseEntity<TransactionResult> saveOrganizationRoles(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id,
			@RequestBody List<OrganizationRoleSnapshot> snapshots,
			BindingResult bindingResult) {


		if (bindingResult.hasErrors()) {
			bindingResult.getAllErrors().forEach( e -> {
				logger.error("Error on ", e.toString());
			});
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}


		TransactionResult result;

		try {
			result = organizationRestAdapter.saveRoles(
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


	@Operation(summary="fetch organizations")
	@GetMapping( )
	public ResponseEntity<OrganizationSnapshotCollection> getOrganizations(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "orgRoleType", defaultValue="All") String orgRoleType,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {

		OrganizationSnapshotCollection collection;
		
		try {
			collection = organizationRestAdapter.find(
				queryText,
				orgRoleType,
				start, 
				limit);
		} catch (OBRuntimeException r) {
			collection = new OrganizationSnapshotCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new OrganizationSnapshotCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new OrganizationSnapshotCollection(r.getMessage());
		}

		return (ResponseEntity<OrganizationSnapshotCollection>) processResponse(collection);
	}


	@Operation(summary="Get an organization")
	@GetMapping( value = "/{id}")
	public ResponseEntity<OrganizationSnapshot> getOrganization(
			@RequestHeader Map<String, String> headers,
			@PathVariable Integer id) {

		OrganizationSnapshot snapshot ;

		try {
			snapshot = organizationRestAdapter.get(id);
		} catch (OBRuntimeException r) {
			snapshot = new OrganizationSnapshot(r.getErrorCode(), r.getParms());
			snapshot.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			snapshot = new OrganizationSnapshot(r.getMessage());
		} catch (RuntimeException r) {
			snapshot = new OrganizationSnapshot(r.getMessage());
		}

		return (ResponseEntity<OrganizationSnapshot>) processResponse(snapshot);
	}



	@Operation(summary="fetch organization role summaries")
	@GetMapping(value="/roleSummaries" )
	public ResponseEntity<OrganizationRoleSummaryCollection> getOrganizationRoleSummaries(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "query", defaultValue="default") String queryText,
			@RequestParam(value = "start", defaultValue="0")Integer start,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {

		OrganizationRoleSummaryCollection collection;

		try {
			collection = organizationRestAdapter.findSummaries(
					queryText,
					start,
					limit);
		} catch (OBRuntimeException r) {
			collection = new OrganizationRoleSummaryCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new OrganizationRoleSummaryCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new OrganizationRoleSummaryCollection(r.getMessage());
		}

		return (ResponseEntity<OrganizationRoleSummaryCollection>) processResponse(collection);
	}



	@Operation(summary="fetch organization role summaries by shortName")
	@GetMapping(value="/searchedRoleSummaries" )
	public ResponseEntity<OrganizationRoleSummaryCollection> findOrganizationRoleSummaries(
			@RequestHeader Map<String, String> headers,
			@RequestParam(value = "searchName", required = true) String searchName,
			@RequestParam(value = "roleType", defaultValue="CP")String roleType,
			@RequestParam(value = "limit", defaultValue="100")Integer limit) {

		OrganizationRoleSummaryCollection collection;

		try {
			OrganizationRoleType type = OrganizationRoleType.lookUp(roleType);
			collection = organizationRestAdapter.findSummariesLikeShortName(
					searchName,
					type,
					limit);
		} catch (OBRuntimeException r) {
			collection = new OrganizationRoleSummaryCollection(r.getErrorCode(), r.getParms());
			collection.setErrorMessage(errorMessageService.getErrorMessage(r.getErrorCode()));
		} catch (DefinedQueryException r) {
			collection = new OrganizationRoleSummaryCollection(CoreTransactionErrorCode.INVALID_QUERY.getCode());
			collection.setErrorMessage(r.getMessage());
		} catch (RuntimeException r) {
			collection = new OrganizationRoleSummaryCollection(r.getMessage());
		}

		return (ResponseEntity<OrganizationRoleSummaryCollection>) processResponse(collection);
	}

}
