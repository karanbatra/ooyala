package com.adobe.acs.nrl.ooyala;

import com.siteworx.cq5.ooyala.client.OoyalaApiCredential;
import com.siteworx.cq5.ooyala.service.OoyalaConfigurationException;
import com.siteworx.cq5.ooyala.service.OoyalaService;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author prajesh
 *         Date: 17/9/13
 *         Time: 12:55 PM
 *         To change this template use File | Settings | File Templates.
 */


@Component(immediate=true, metatype=false, description="Ooyala Query Servlet")
@Service
@Properties({
        @Property(name="sling.servlet.paths", value="/bin/nrl/ooyala")
})


public class OoyalaNRLServlet extends SlingSafeMethodsServlet {

    private Logger logger = LoggerFactory.getLogger(OoyalaNRLServlet.class);

    private static final int DEFAULT_LIMIT = 10;


    @Reference
    private OoyalaConfigurationService ooyalaConfigurationService;

    @Reference
    private OoyalaService ooyalaService;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        String path = request.getParameter("path");

        path = path.substring(1);

        logger.info("Path = " +path);
        int siteNameIndex = path.indexOf("/content/");

        String siteNameStart = path.substring(siteNameIndex+9);

        int indexOfSlash = siteNameStart.indexOf("/");

        String siteName = siteNameStart.substring(0,indexOfSlash);



        final String queryString = request.getParameter("query");
        final String searchBy = request.getParameter("searchBy");
        final int[] offsetAndLimit = getLimit(request);

        if(logger.isDebugEnabled()){
            logger.debug("PATH = " + path);
            logger.debug("QUERY = " + queryString);
            logger.debug("TYPE = " + searchBy);
        }


        OoyalaApiCredential oolayaCredentials = null;
        try {
            oolayaCredentials = ooyalaConfigurationService.getCredentialsForSite(siteName);

            logger.info("key = "+ oolayaCredentials.getApiKey());
            logger.info("secret = "+ oolayaCredentials.getApiSecret());

        } catch (OoyalaConfigurationException e) {
            logger.error("Ooyala Configuration Exception occured for site = "+siteName);
        }


        logger.info("**** Oolaya NRL servlet ****");
        logger.info("configuration site Path = "+path);
        logger.info("siteName = "+siteName);



        //section 2 getting videos


        try {
            String jsonResponse = "";
            if (queryString == null || "".equals(queryString.trim())) {
                jsonResponse = getJSON(ooyalaService.getAllVideos(oolayaCredentials, offsetAndLimit[0], offsetAndLimit[1]));
            }
            else if (queryString != null && queryString.trim().length() > 2) {
                jsonResponse = getJSON(ooyalaService.getVideos(oolayaCredentials , searchBy, queryString, offsetAndLimit[0], offsetAndLimit[1]));
            } else if (queryString.trim().length() < 2) {
                jsonResponse = "{}";
            }
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }

    }


    /**
     * Manipulates the response JSON from Ooyala to fit the needs of built-in CQ functionality.
     *
     * @param rawResponse The raw JSON response from Ooyala.
     * @return A well formed JSON response.
     * @throws org.apache.sling.commons.json.JSONException
     */
    private String getJSON(String rawResponse) throws JSONException {
        // move embedCode into path to use existing extjs drag-drop
        JSONObject jsonFixed = new JSONObject(rawResponse);
        JSONArray items = (JSONArray) jsonFixed.get("items");
        if (logger.isDebugEnabled()) {
            logger.debug("Response length = " + items.length());
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
                logger.error("embedCode is null");
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


}
