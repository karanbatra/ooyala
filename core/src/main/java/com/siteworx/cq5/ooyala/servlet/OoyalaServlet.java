package com.siteworx.cq5.ooyala.servlet;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siteworx.cq5.ooyala.client.OoyalaApiCredential;
import com.siteworx.cq5.ooyala.service.OoyalaConfigurationService;
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

/**
 * The main servlet for interacting with Ooyala through CQ's content finder.
 *
 * @author leonardo@siteworx.com
 * @author rboll@siteworx.com
 *
 */
@Component(immediate=true, metatype=false, description="Ooyala Query Servlet")
@Service
@Properties({
	@Property(name="sling.servlet.paths", value="/bin/wcm/ooyala")
})
public class OoyalaServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 5690283614755652392L;
	private static final Logger log = LoggerFactory.getLogger(OoyalaServlet.class);
	private static final int DEFAULT_LIMIT = 10;
	private static final String DEFAULT_PATH = "/content";
	
	@Reference
	private OoyalaService ooyalaService;
	
	@Reference
	private OoyalaConfigurationService ooyalaConfigurationService;
    
    /**
	 * Returns results based on the query and searchBy request parameters.
	 */
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		final OoyalaApiCredential credentials = ooyalaConfigurationService.getCredentialsForPath(DEFAULT_PATH);
		final String queryString = request.getParameter("query");
		final String searchBy = request.getParameter("searchBy");
		final int[] offsetAndLimit = getLimit(request);
		
		if(log.isDebugEnabled()){
			log.debug("PATH = " + DEFAULT_PATH);
			log.debug("QUERY = " + queryString);
			log.debug("TYPE = " + searchBy);
		}
		
		try {
			String jsonResponse = "";
			if (queryString == null || "".equals(queryString.trim())) {
				jsonResponse = getJSON(ooyalaService.getAllVideos(credentials, offsetAndLimit[0], offsetAndLimit[1]));
			}
			else if (queryString != null && queryString.trim().length() > 2) {
				jsonResponse = getJSON(ooyalaService.getVideos(credentials, searchBy, queryString, offsetAndLimit[0], offsetAndLimit[1]));
			} else if (queryString.trim().length() < 2) {
				jsonResponse = "{}";
			}
			response.setContentType("application/json");
			response.getWriter().write(jsonResponse);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.sendError(500, e.getMessage());
		}
	}

	/**
	 * Manipulates the response JSON from Ooyala to fit the needs of built-in CQ functionality.
	 *
	 * @param rawResponse The raw JSON response from Ooyala.
	 * @return A well formed JSON response.
	 * @throws JSONException
	 */
	private String getJSON(String rawResponse) throws JSONException {
		// move embedCode into path to use existing extjs drag-drop
		JSONObject jsonFixed = new JSONObject(rawResponse);
		JSONArray items = (JSONArray) jsonFixed.get("items");
		if (log.isDebugEnabled()) {
			log.debug("Response length = " + items.length());
		}
		for(int i=0; i < items.length(); i++){
			final JSONObject currItem = (JSONObject) items.get(i);
			final String embedCode = (String) currItem.get("embed_code");
			int duration = (Integer) currItem.get("duration");
			String durationString = String.format("%d min, %d sec", 
				    TimeUnit.MILLISECONDS.toMinutes(duration),
				    TimeUnit.MILLISECONDS.toSeconds(duration) - 
				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
				);
			currItem.put("duration", durationString);
			if(embedCode != null){
				currItem.put("path", embedCode);
			}else{
				log.error("embedCode is null");
			}
		}

		jsonFixed = new JSONObject().put("hits", items);
		return jsonFixed.toString();
	}

	/**
	 * Parses the limit and offset string from CQ into integers.
	 *
	 * @param request The request from which to fetch the limit parameter.
	 * @return An integer array containing the offset and limit for the Ooyala request.
	 */
	private int[] getLimit(SlingHttpServletRequest request) {
		// limit string format : 10..20
		// [0] = offset
		// [1] = limit
		final String limitString = request.getParameter("limit");
		if(limitString != null){
			try{
				int offset = Integer.parseInt(limitString.substring(0, limitString.indexOf(".")));
				int limit = Integer.parseInt(limitString.substring(limitString.lastIndexOf(".") + 1, limitString.length())) - offset;
				return new int[] {offset, limit};
			}catch(NumberFormatException nfe){
				nfe.printStackTrace();
			}
		}
		return new int[] {0, DEFAULT_LIMIT};
	}
	
	@Activate
	protected void activate(ComponentContext context) throws RepositoryException {
		if (log.isDebugEnabled())
			log.debug("Activating "+this.getClass());
	}

}
