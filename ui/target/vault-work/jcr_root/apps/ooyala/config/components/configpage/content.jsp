<%@page session="false" contentType="text/html"
            pageEncoding="utf-8"
            import="org.apache.sling.api.resource.ResourceResolver,
            com.day.cq.i18n.I18n"
            %>
<%@include file="/libs/foundation/global.jsp"%> 
<%@include file="/libs/cq/cloudserviceconfigs/components/configpage/init.jsp"%>

<%
        I18n i18n = new I18n(request);
%>

<div>
    <h3><%= i18n.get("Ooyala Settings") %></h3>
	<div>
        <ul style="float: left; margin: 0px;">

            <li><div class="li-bullet"><strong><%= i18n.get("Api Key ") %>: </strong><%= xssAPI.encodeForHTML(properties.get("apiKey","")) %></div></li>
            <li><div class="li-bullet"><strong><%= i18n.get("Api Secret ") %>: </strong><%= xssAPI.encodeForHTML(properties.get("apiSecret","")) %></div></li>
            <li><div class="li-bullet"><strong><%= i18n.get("Path ") %>: </strong><%= xssAPI.encodeForHTML(properties.get("path","")) %></div></li>
        </ul>
    </div>

</div>

