package com.siteworx.cq5.ooyala.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import com.siteworx.cq5.ooyala.service.OoyalaService;

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

@Component(immediate = true, metatype = false, description = "Ooyala Uploader Proxy Servlet")
@Service
@Properties({ @Property(name = "sling.servlet.paths", value = "/bin/ooyala/upload/assets") })
public class UploadProxyServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -3738183473154158783L;
	
	private final Pattern assetIdPattern = Pattern.compile("^(.*?assets/)(.*?)/(.*)$");
	
	private static final String UPLOAD_STATUS_ACTION = "upload_status";

	@Reference
	private OoyalaService ooyalaService;

	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		final String requestURI = request.getRequestURI();
		final String requestAction = getActionFromPath(requestURI);
		
		try {
			JSONObject requestJson;
			final StringBuilder stringBuilder = new StringBuilder();
			final InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				final char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
			requestJson = new JSONObject(stringBuilder.toString());
			
			if (requestAction != null && UPLOAD_STATUS_ACTION.equals(requestAction)) {
				final String assetId = getAssetIdFromPath(requestURI);
				final String uploadStatusResponse = ooyalaService.setUploadStatus(requestJson, assetId, requestAction);
				if (uploadStatusResponse != null && !"".equals(uploadStatusResponse)) {
					response.setStatus(200);
					response.getWriter().write(uploadStatusResponse);
				} else response.setStatus(500);
				return;
			} else {
				final String initUploadResponse = ooyalaService.initUpload(requestJson);
				if (initUploadResponse != null && !"".equals(initUploadResponse)) {
					response.setStatus(200);
					response.getWriter().write(initUploadResponse);
				} else response.setStatus(500);
			}
		} catch (JSONException je) {
			je.printStackTrace();
			response.sendError(500);
		}
		return;
	}
	
	private String getActionFromPath(String path) {
		final Matcher matcher = assetIdPattern.matcher(path);
		if (matcher.matches()) {
			return matcher.group(3);
		} else {
			return null;
		}
	}
	
	private String getAssetIdFromPath(String path) {
		final Matcher matcher = assetIdPattern.matcher(path);
		if (matcher.matches()) {
			return matcher.group(2);
		} else {
			return null;
		}
	}
}
