package com.siteworx.cq5.ooyala.client.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siteworx.cq5.ooyala.client.OoyalaApiCredential;

/**
 * Copyright (c) 2012, Ooyala, Inc.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * •	Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * •	Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

/**
 * An extension of {@link OoyalaRequest} for retrieving videos via Ooyala's RESTful API.
 *
 * @author leonardo@siteworx.com
 * @author rboll@siteworx.com
 *
 */
public class GetVideos extends OoyalaQueryRequest {
	
	private static final Logger log = LoggerFactory.getLogger(GetVideos.class);
	
	/**
	 * Constructs an instance of GetVideos with the provided credentials and the proper url,
	 * based on Ooyala's RESTful API (v2).
	 * @param credentials The {@link OoyalaApiCredential} object for authenticating the API request.
	 * @param includeLabels A boolean value indicating whether the response should include asset labels.
	 * @param includeMetadata A boolean value indicating whether the response should include asset metadata.
	 */
	public GetVideos(OoyalaApiCredential credentials, boolean includeLabels, boolean includeMetadata) {
		super(credentials, HTTPMethod.GET, "/v2/assets");
		
		this.getParameters().put(ASSET_TYPE_PARAM, "video");
		
		if(includeLabels && includeMetadata){
			this.getParameters().put("include", "labels,metadata");
		}else if(includeLabels && !includeMetadata){
			this.getParameters().put("include", "labels");
		}else if(!includeLabels && includeMetadata){
			this.getParameters().put("include", "metadata");
		}
	}
	
	/**
	 * Adds a query for video title.
	 * @param titleQueryString The string to query Ooyala's API with.
	 */
	public void addTitleQuery(String titleQueryString) {
		getQueryParameters().put("name", "'" + titleQueryString + "'");
	}
	
	/**
	 * Adds a query for description.
	 * @param descQueryString The string to query Ooyala's API with.
	 */
	public void addDescriptionQuery(String descQueryString) {
		getQueryParameters().put("description", "'" + descQueryString + "'");
	}
	
	/**
	 * Adds a query for custom asset metadata. Note: The metadata key must exist.
	 * @param metaQueryString The string to query Ooyala's API with. Should be in the form <key>:<value>.
	 */
	public void addMetaQuery(String metaQueryString) {
		try {
			String[] params = metaQueryString.split(":");
			getQueryParameters().put("metadata."+params[0], "'"+params[1]+"'");
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isDebugEnabled())
				log.debug("Meta query malformed: " + metaQueryString);
		}
	}
	
	/**
	 * Adds a query for labels.
	 * @param labelQueryString The string to query Ooyala's API with. Must be an exact match to an existing label name.
	 */
	public void addLabelQuery(String labelQueryString) {
		getQueryParameters().put("label", labelQueryString);
	}
	
}