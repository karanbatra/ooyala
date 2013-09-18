<%--

Copyright (c) 2012, Ooyala, Inc.
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
•    Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
•    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

--%>

<%@page import="com.siteworx.cq5.ooyala.service.OoyalaService"%>
<%@page import="com.siteworx.cq5.ooyala.service.OoyalaConfigurationService"%>
<%@page import="org.apache.sling.commons.json.JSONArray"%>
<%@page import="org.apache.sling.commons.json.JSONObject"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<sling:defineObjects />

<%
    response.setContentType("application/json");

    final OoyalaService ooyalaService = sling.getService(OoyalaService.class);
    final String playersString = ooyalaService.getAllPlayers(OoyalaConfigurationService.OOYALA_CONFIG_PATH);
    final JSONObject playersJSON = new JSONObject(playersString);

    final JSONArray responseJSON = new JSONArray();
    
    final JSONArray items = (JSONArray) playersJSON.get("items");
    for(int i=0; i < items.length(); i++){
        final JSONObject currItem = (JSONObject) items.get(i);
        final JSONObject jobject=new JSONObject();
        jobject.put("value",currItem.get("id"));
        jobject.put("text",currItem.get("name"));
        responseJSON.put(jobject);
    }
    out.println(responseJSON.toString());
%>