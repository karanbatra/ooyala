package com.siteworx.cq5.ooyala.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.TidyJSONWriter;
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
 * The main servlet for retrieving autocomplete suggestions from Ooyala.
 *
 * @author leonardo@siteworx.com
 * @author rboll@siteworx.com
 *
 */
@Component(immediate=true, metatype=false, description="Ooyala Suggestion Servlet")
@Service
@Properties({
	@Property(name="sling.servlet.paths", value="/bin/wcm/ooyala/suggestions")
})
public class OoyalaSuggestionServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = -6408020004521559531L;
	private static final Logger log = LoggerFactory.getLogger(OoyalaSuggestionServlet.class);
	private static final String LABEL = "label";
	private static final String META = "meta";
	private static final int DEFAULT_LIMIT = 5;
	private static final String DEFAULT_PATH = "/content";
	private static final String NO_RESULTS = "No Suggestions";
	
	@Reference
	private OoyalaService ooyalaService;
	
	/**
	 * Returns suggestions based on the query and type parameters in the request.
	 */
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		
		final String requestPath = DEFAULT_PATH;
		final String query = request.getParameter("query");
		final String type = request.getParameter("type");
		if(log.isDebugEnabled()){
			log.debug("PATH = " + requestPath);
			log.debug("QUERY = " + query);
			log.debug("TYPE = " + type);
		}
		
		if (query == null || query.trim().length() < 3) return;
		if (type == null || type.trim().length() == 0) return;

		try {
			response.setContentType("application/json");
			if (LABEL.equals(type)) {
				final String rawResults = ooyalaService.getLabels(requestPath, DEFAULT_LIMIT);
				if (rawResults == null || "".equals(rawResults))
					writeEmptyJSONResponse(response.getWriter());
				else writeLabelJSONResponse(response.getWriter(), query, rawResults);
			}
			else if (META.equals(type)) {
				final List<String> resultList = ooyalaService.getMetaKeys();
				if (resultList == null || resultList.size() == 0)
					writeEmptyJSONResponse(response.getWriter());
				else writeMetaJSONResponse(response.getWriter(), query, resultList, DEFAULT_LIMIT);
			}
		} catch (Exception e){
			e.printStackTrace();
			try {
				writeEmptyJSONResponse(response.getWriter());
			} catch (Exception e2) {
				response.sendError(500, e.getMessage());
			}
		}
	}
	
	private void writeEmptyJSONResponse(PrintWriter writer) throws JSONException, IOException {
		TidyJSONWriter jsonWriter = new TidyJSONWriter(writer);
		jsonWriter.object();
		jsonWriter.key("suggestions");
		jsonWriter.array();
		jsonWriter.object()
			.key("name").value(NO_RESULTS)
			.key("value").value("")
			.key("title").value("")
			.endObject();
		jsonWriter.endArray();
		jsonWriter.key("results").value(1);
		jsonWriter.endObject();
	}

	/**
	 * Writes the matching metadata keys into a JSON for display in CQ's content finder.
	 *
	 * @param writer The response writer where the JSON should be written.
	 * @param query The query to filter the metadata keys.
	 * @param resultList The List of metadata keys to filter and write.
	 * @param limit The maximum number of results to return.
	 * @throws JSONException
	 * @throws IOException
	 */
	private void writeMetaJSONResponse(final PrintWriter writer, final String query, final List<String> resultList, int limit) throws JSONException, IOException {
		TidyJSONWriter jsonWriter = new TidyJSONWriter(writer);
		jsonWriter.object();
		jsonWriter.key("suggestions");
		jsonWriter.array();
		
		int i = 0;
		for (String str : resultList) {
			while (i < limit) {
				if(str != null && str.toLowerCase().contains(query.toLowerCase())){
					jsonWriter.object()
						.key("name").value("Metadata Key")
						.key("title").value(str)
						.key("value").value(str+":")
						.endObject();
					i++;
				}
			}
		}
		jsonWriter.endArray();
		jsonWriter.key("results").value(limit);
		jsonWriter.endObject();
	}

	/**
	 * Manipulates the JSON response from Ooyala to fit the needs of CQ.
	 *
	 * @param writer The response writer where the JSON should be written.
	 * @param query The query to filter the label names.
	 * @param rawResults The JSON response from Ooyala's API.
	 * @throws JSONException
	 * @throws IOException
	 */
	private void writeLabelJSONResponse(final PrintWriter writer, final String query, final String rawResults) throws JSONException, IOException {
		JSONObject jsonResults = new JSONObject(rawResults);
		JSONArray labels = jsonResults.getJSONArray("items");
		TidyJSONWriter jsonWriter = new TidyJSONWriter(writer);
		jsonWriter.object();
		jsonWriter.key("suggestions");
		jsonWriter.array();
		
		for (int i = 0; i < labels.length(); i++) {
			JSONObject obj = new JSONObject(labels.get(i).toString());
			final String name = (String) obj.get("name");
			if(name != null && name.toLowerCase().contains(query.toLowerCase())){
				jsonWriter.object()
					.key("name").value(obj.get("name"))
					.key("title").value(obj.get("full_name"))
					.key("value").value(obj.get("name"))
					.endObject();
			}
		}
		
		jsonWriter.endArray();
		jsonWriter.key("results").value(labels.length());
		jsonWriter.endObject();
	}
}
