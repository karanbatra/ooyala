package com.siteworx.cq5.ooyala.client.request;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

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
 * Base class for all Ooyala requests.
 *
 * @author leonardo@siteworx.com
 * @author rboll@siteworx.com
 *
 */
public abstract class OoyalaRequest {
	
	public static final String API_KEY_PARAM = "api_key";
	public static final String EXPIRATION_PARAM = "expiration";
	public static final String ASSET_TYPE_PARAM = "asset_type";
	
	final OoyalaApiCredential credentials;
	String requestPath = "/v2/assets";
	final SortedMap<String, String> parameters = new TreeMap<String, String>();
	
	final String protocol = "https";
	final String host = "api.ooyala.com";
	
	final HTTPMethod method;
	
	/**
	 * An enum of all available HTTP methods.
	 */
	public enum HTTPMethod {
		GET, POST, PUT, HEAD, PATCH, DELETE, OPTIONS
	}
	
	/**
	 * Constructs an instance of OoyalaRequest with the provided credentials and the proper url,
	 * based on Ooyala's RESTful API (v2).
	 *
	 * @param credentials The {@link OoyalaApiCredential} object for authenticating the API request.
	 * @param method The HTTP method to be used when connecting to Ooyala. Usually GET.
	 * @param requestPath The request path for the type of object expected in the API response.
	 * @throws IllegalArgumentException Throws {@link IllegalArgumentException} if any parameters is null.
	 */
	public OoyalaRequest(OoyalaApiCredential credentials, HTTPMethod method, String requestPath){
		if(credentials == null) throw new IllegalArgumentException("credentials cannot be null.");
		if(empty(credentials.getApiKey())) throw new IllegalArgumentException("api_key cannot be null.");
		if(empty(credentials.getApiSecret())) throw new IllegalArgumentException("apiSecret cannot be null.");
		if(method == null) throw new IllegalArgumentException("The argument 'method' cannot be null.");
		if(empty(requestPath)) throw new IllegalArgumentException("The argument 'requestPath' cannot be null.");
		
		this.credentials = credentials;
		this.method = method;
		this.requestPath = requestPath;
		parameters.put(API_KEY_PARAM, credentials.getApiKey());
	}

	/**
	 * A helper method for determining whether a string is empty or null.
	 *
	 * @param in The string to test.
	 * @return true if the string is empty or null, false otherwise.
	 */
	private static final boolean empty(String in){
		return in == null || in.trim().length() == 0;
	}
	
	/**
	 * A setter for the API key.
	 *
	 * @param api_key The new Ooyala API key.
	 */
	public void setApiKey(String api_key){
		if(api_key == null)throw new IllegalArgumentException("The argument api_key cannot be null.");
		parameters.put(API_KEY_PARAM, api_key);
	}
	
	/**
	 * A setter for the expiration time of the request.
	 *
	 * @param seconds The UNIX timestamp for the expiration time of the request.
	 */
	public void setExpiration(long seconds){
		parameters.put(EXPIRATION_PARAM, Long.toString(seconds));
	}
	
	/**
	 * A getter for the parameters map.
	 *
	 * @return The current parameters map.
	 */
	public SortedMap<String, String> getParameters(){
		return this.parameters;
	}
	
	/**
	 * A getter for the request credentials.
	 *
	 * @return The {@link OoyalaApiCredential} containing the current request credentials.
	 */
	public OoyalaApiCredential getCredentials() {
		return credentials;
	}

	/**
	 * A getter for the requestPath.
	 *
	 * @return The String representing the request's requestPath.
	 */
	public String getRequestPath() {
		return requestPath;
	}

	/**
	 * A getter for the request's {@link HTTPMethod}.
	 *
	 * @return The current {@link HTTPMethod}.
	 */
	public HTTPMethod getMethod() {
		return method;
	}
	
	/**
	 * A getter for the request's protocol.
	 *
	 * @return The string representing the request's protocol.
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * A getter for the request's host.
	 *
	 * @return The string representing the request's host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * A setter for the request's limit, the maximum results to be returned from the request.
	 *
	 * @param limit The integer limit.
	 */
	public void setLimit(int limit) {
		this.parameters.put("limit", ""+limit);
	}
	
	/**
	 * A setter for the request's offest, used by the API in determining the sequence of results to return.
	 *
	 * @param offset The integer offset.
	 */
	public void setOffset(int offset) {
		this.parameters.put("offset", ""+offset);
	}
	
	
	@Override
	public String toString() {
		final int maxLen = 20;
		StringBuilder builder = new StringBuilder();
		builder.append("OoyalaRequest [credentials=")
				.append(credentials)
				.append(", parameters=")
				.append(parameters != null ? toString(parameters.entrySet(),
						maxLen) : null).append(", protocol=").append(protocol)
				.append(", host=").append(host).append(", requestPath=")
				.append(requestPath).append(", method=").append(method)
				.append("]");
		return builder.toString();
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
}