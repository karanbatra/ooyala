package com.siteworx.cq5.ooyala.client.request;

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
 * An extension of {@link OoyalaRequest} for retrieving labels via Ooyala's RESTful API.
 *
 * @author leonardo@siteworx.com
 * @author rboll@siteworx.com
 *
 */
public class GetLabels extends OoyalaRequest {
	
	/**
	 * Constructs an instance of GetLabels with the provided credentials and the proper url, 
	 * based on Ooyala's RESTful API (v2).
	 * @param credentials The {@link OoyalaApiCredential} object for authenticating the API request.
	 */
	public GetLabels(OoyalaApiCredential credentials) {
		super(credentials, HTTPMethod.GET, "/v2/labels");
	}
	
}