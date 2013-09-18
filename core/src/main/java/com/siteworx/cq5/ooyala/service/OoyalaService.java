package com.siteworx.cq5.ooyala.service;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import com.siteworx.cq5.ooyala.client.OoyalaApiCredential;
import com.siteworx.cq5.ooyala.client.OoyalaClient;
import com.siteworx.cq5.ooyala.client.OoyalaClientException;
import com.siteworx.cq5.ooyala.client.OoyalaPostClient;
import com.siteworx.cq5.ooyala.client.request.GetAssetUploadingURL;
import com.siteworx.cq5.ooyala.client.request.GetLabels;
import com.siteworx.cq5.ooyala.client.request.GetPlayers;
import com.siteworx.cq5.ooyala.client.request.GetVideos;
import com.siteworx.cq5.ooyala.client.request.OoyalaPostRequest;
import com.siteworx.cq5.ooyala.client.request.OoyalaPutRequest;

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
 * Provides high level access to the Ooyala RESTful API.
 *
 * @author leonardo@siteworx.com
 * @author rboll@siteworx.com
 *
 */
@Component(immediate=true, metatype=false)
@Service(value={OoyalaService.class})
public class OoyalaService {

	private static final String SEARCH_BY_LABEL = "searchByLabel";
	private static final String SEARCH_BY_TITLE = "searchByTitle";
	private static final String SEARCH_BY_DESC = "searchByDescription";
	private static final String SEARCH_BY_META = "searchByMeta";
		
	private static final String DEFAULT_PATH = "/content";
	
	@Reference
	private OoyalaConfigurationService configService;
	
	private OoyalaClient ooyalaClient = new OoyalaClient();
	private OoyalaPostClient ooyalaPostClient = new OoyalaPostClient();

	/**
	 * A getter for the list of metadata keys.
	 *
	 * @return The list of metadata keys from the configuration service.
	 */
	public List<String> getMetaKeys() {
		return configService.getMetadataKeys();
	}
	
	public String getAllVideos(OoyalaApiCredential credentials) throws OoyalaClientException {
		return getVideos(credentials, "", "");
	}
	
	public String getAllVideos(OoyalaApiCredential credentials, int offset, int limit) throws OoyalaClientException {
		return getVideos(credentials, "", "", offset, limit);
	}
	
	public String getVideos(OoyalaApiCredential credentials, String searchBy, String queryString) throws OoyalaClientException {
		return getVideos(credentials, searchBy, queryString, 0, 0);
	}
	
	/**
	 * Constructs a request for videos, based on the parameters, and sends it to Ooyala.
	 *
	 * @param credentials The {@link OoyalaApiCredential} object for authenticating the API request.
	 * @param searchBy The type of search to be performed. Available options are "searchByTitle", "searchByLabel", "searchByDescription", "searchByMeta".
	 * @param queryString The query string to execute, in conjunction with the searchBy parameter.
	 * @param offset The request's offest, used by the API in determining the sequence of results to return.
	 * @param limit The request's limit, the maximum results to be returned from the request.
	 * @return The JSON string returned by Ooyala's API.
	 * @throws OoyalaClientException
	 */
	public String getVideos(OoyalaApiCredential credentials, String searchBy, String queryString, int offset, int limit) throws OoyalaClientException {
		if(queryString == null) throw new NullPointerException("queryString is null");
		
		final GetVideos request = new GetVideos(credentials, false, false);
		
		if (SEARCH_BY_LABEL.equals(searchBy)) {
			request.addLabelQuery(queryString);
		} else if (SEARCH_BY_TITLE.equals(searchBy)) {
			request.addTitleQuery(queryString);
		} else if (SEARCH_BY_DESC.equals(searchBy)) {
			request.addDescriptionQuery(queryString);
		} else if (SEARCH_BY_META.equals(searchBy)) {
			request.addMetaQuery(queryString);
		}
		if (limit > 0)
			request.setLimit(limit);
		if (offset > 0) {
			request.setOffset(offset);
		}
		return ooyalaClient.request(request);
	}
	
	public String getAllLabels(OoyalaApiCredential credentials) {
		return getLabels(credentials);
	}
	
	public String getLabels(OoyalaApiCredential credentials) {
		return getLabels(credentials, 0);
	}
	
	public String getLabels(String requestPath, int limit) {
		return getLabels(configService.getCredentialsForPath(requestPath), limit);
	}
	
	/**
	 * Constructs a request for labels, without any query, and sends it to Ooyala.
	 *
	 * @param credentials The {@link OoyalaApiCredential} object for authenticating the API request.
	 * @param limit The request's limit, the maximum results to be returned from the request.
	 * @return The JSON string returned by Ooyala's API.
	 */
	public String getLabels(OoyalaApiCredential credentials, int limit) {
		if (credentials == null)
			return null;
		else {
			try {
				GetLabels request = new GetLabels(credentials);
				if (limit > 0)
					request.setLimit(limit);
				else 
					request.setLimit(500);
				return ooyalaClient.request(request);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * Constructs a request for players, without any query, and sends it to Ooyala.
	 *
	 * @param credentials The {@link OoyalaApiCredential} object for authenticating the API request.
	 * @return The JSON string returned by Ooyala's API.
	 */
	public String getAllPlayers(OoyalaApiCredential credentials){
		try {
			GetPlayers request = new GetPlayers(credentials, true);
			return ooyalaClient.request(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Constructs a request for players, without any query, and sends it to Ooyala.
	 *
	 * @param requestPath The path of the object type expected from Ooyala.
	 * @return The JSON string returned by Ooyala's API.
	 */
	public String getAllPlayers(String requestPath){
		try {
			GetPlayers request = new GetPlayers(configService.getCredentialsForPath(requestPath), true);
			return ooyalaClient.request(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Proxies the uploader post request to the Ooyala API using the current credentials.
	 * @param json The raw JSON object to send as the body.
	 * @return
	 * @throws JSONException 
	 */
	public String initUpload(JSONObject json) throws JSONException {
		try {
			OoyalaApiCredential credentials = configService.getCredentialsForPath(DEFAULT_PATH);
			if (isValidForUploadPost(json) && credentials != null) {
				OoyalaPostRequest request = new OoyalaPostRequest(credentials);
				request.setBody(json);
				String postResponse = ooyalaPostClient.request(request);
				if (postResponse != null && !"".equals(postResponse)) {
					JSONObject postResponseJson = new JSONObject(postResponse);
					GetAssetUploadingURL urlRequest = new GetAssetUploadingURL(credentials, postResponseJson.getString("embed_code"));
					String getResponse = ooyalaClient.request(urlRequest);
					JSONArray array = new JSONArray(getResponse);
					postResponseJson = postResponseJson.put("uploading_urls", array);
					return postResponseJson.toString();
				}
				else {
					return null;
				}
			} else {
				return null;
			}
		} catch (OoyalaClientException oce) {
			oce.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param json
	 * @param assetId
	 * @param requestAction
	 * @return
	 */
	public String setUploadStatus(JSONObject json, String assetId, String requestAction) {
		try {
			OoyalaApiCredential credentials = configService.getCredentialsForPath(DEFAULT_PATH);
			OoyalaPutRequest request = new OoyalaPutRequest(credentials, assetId+"/"+requestAction);
			request.setBody(json);
			final String setUploadStatusResponse = ooyalaPostClient.request(request);
			if (setUploadStatusResponse != null && !"".equals(setUploadStatusResponse)) {
				return setUploadStatusResponse;
			} else return null;
		} catch (OoyalaClientException oce) {
			oce.printStackTrace();
			return null;
		}
	}
	
	private boolean isValidForUploadPost(JSONObject json) {
		if (json.has("name") && json.has("asset_type") && json.has("file_name") && json.has("file_size")) {
			return true;
		} else return false;
	}
	
}
