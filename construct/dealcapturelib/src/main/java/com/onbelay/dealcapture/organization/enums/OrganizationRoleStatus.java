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
package com.onbelay.dealcapture.organization.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the Organization Role types for an OrganizationRole
 * 
 * @author lefeuvrem
 *
 */
public enum OrganizationRoleStatus {
	PENDING 	("P", "PENDING"),
    VERIFIED    ("V", "VERIFIED"),
    SUSPENDED   ("S", "SUSPENDED");

	private final String code;
	private final String name;

    private static final Map<String,OrganizationRoleStatus> lookup 
    	= new HashMap<String,OrganizationRoleStatus>();

    private static final Map<String,OrganizationRoleStatus> lookupByName 
        = new HashMap<String,OrganizationRoleStatus>();

    static {
    	for(OrganizationRoleStatus c : EnumSet.allOf(OrganizationRoleStatus.class))
         lookup.put(c.code, c);
        for(OrganizationRoleStatus c : EnumSet.allOf(OrganizationRoleStatus.class))
         lookupByName.put(c.name, c);
    }
    
	private OrganizationRoleStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
    public String getName() {
        return name;
    }
    
	public static OrganizationRoleStatus lookUp(String code) {
		return lookup.get(code);
	}

    public static OrganizationRoleStatus lookUpByName(String name) {
        return lookupByName.get(name);
    }
    
    public static OrganizationRoleStatus lookUpByNameOrCode(String name) {
		OrganizationRoleStatus rule = OrganizationRoleStatus.lookUp(name);
		if (rule != null) {
			return rule; 
		} else {
			return lookupByName.get(name);
		}
    }

}
