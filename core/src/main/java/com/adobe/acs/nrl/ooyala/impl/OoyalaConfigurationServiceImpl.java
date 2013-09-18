package com.adobe.acs.nrl.ooyala.impl;

import com.adobe.acs.nrl.ooyala.OoyalaConfigurationService;
import com.day.cq.wcm.api.Page;
import com.siteworx.cq5.ooyala.client.OoyalaApiCredential;
import com.siteworx.cq5.ooyala.service.OoyalaConfigurationException;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 *         Date: 17/9/13
 *         Time: 5:07 PM
 *         To change this template use File | Settings | File Templates.
 */

@Component(immediate = true,metatype = false)
@Service(value=OoyalaConfigurationService.class)

public class OoyalaConfigurationServiceImpl implements OoyalaConfigurationService {

    private static Logger logger = LoggerFactory.getLogger(OoyalaConfigurationServiceImpl.class);

    private static Map<String, OoyalaApiCredential>  credentialsMap  = new HashMap<String, OoyalaApiCredential>();



    public static final String OOYALA_CONFIG_PATH = "/etc/cloudservices/ooyala-configuration";
    private static final String API_KEY = "apiKey";
    private static final String API_SECRET = "apiSecret";
    private static final String ROOT_PATH = "path";
    private static final String META_PATH = "metakey";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public synchronized void updateApiKeyCache() throws OoyalaConfigurationException {

        logger.info("***inside ooyala configuration updateAPIKey() service for multiple sites..");

        OoyalaApiCredential credentials = null;


        if(logger.isDebugEnabled()) logger.debug("Updating Ooyala Api Key Cache.");
        ResourceResolver resourceResolver = null;

        try {
            resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);


            final Page parentConfigPage = resourceResolver.resolve(OOYALA_CONFIG_PATH).adaptTo(Page.class);

            if(parentConfigPage==null) {
                throw new OoyalaConfigurationException("The Ooyala configuration node "+OOYALA_CONFIG_PATH+" is missing.");
            }

            Iterator<Page> childPageIterator = parentConfigPage.listChildren();

            Page childPage = null;
            ValueMap valueMap=null;
            String apiKey = null, apiSecret = null;

            while(childPageIterator.hasNext())
            {
                childPage = childPageIterator.next();
                valueMap = childPage.getProperties();
                apiKey = valueMap.get(API_KEY,null);
                apiSecret = valueMap.get(API_SECRET, null);

                credentials = new OoyalaApiCredential(apiKey, apiSecret);

                if(credentials!=null)
                {
                    credentialsMap.put(childPage.getPath(), credentials);
                }

            }



            if(logger.isDebugEnabled())
                logger.debug("Ooyala Config Updated.\n");
        } catch (LoginException e) {
            e.printStackTrace();
            logger.error("Unable to log into JCR.");
        } finally {
            if (resourceResolver != null) resourceResolver.close();
        }
        

    }


    @Override
    public OoyalaApiCredential getCredentialsForPath(String path) throws OoyalaConfigurationException{

        if(path==null) return null;

        if(!credentialsMap.containsKey(path)){


            logger.info("*** Ooyala API key not found in cache, checking config nodes ..");

            OoyalaApiCredential credentials = null;


            if(logger.isDebugEnabled()) logger.debug("Updating Ooyala Api Key Cache.");
            ResourceResolver resourceResolver = null;

            try {
                resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);


                final Page configPage = resourceResolver.resolve(path).adaptTo(Page.class);

                if(configPage==null) {
                    throw new OoyalaConfigurationException("The Ooyala configuration node "+OOYALA_CONFIG_PATH+" is missing.");
                }

                ValueMap valueMap=null;
                String apiKey = null, apiSecret = null;

                    valueMap = configPage.getProperties();
                    apiKey = valueMap.get(API_KEY,null);
                    apiSecret = valueMap.get(API_SECRET, null);

                    credentials = new OoyalaApiCredential(apiKey, apiSecret);

                    if(credentials!=null)
                    {
                        credentialsMap.put(configPage.getPath(), credentials);
                        logger.info("Updated the Key cache for path="+path);
                    }


                if(logger.isDebugEnabled())
                    logger.debug("Ooyala Config Updated.\n");
            } catch (LoginException e) {
                e.printStackTrace();
                logger.error("Unable to log into JCR.");
            } finally {
                if (resourceResolver != null) resourceResolver.close();
            }




        }


        return credentialsMap.get(path);

    }

    @Override
    public OoyalaApiCredential getCredentialsForSite(String siteName) throws OoyalaConfigurationException
    {

        if(siteName==null)  throw new OoyalaConfigurationException("The Ooyala configuration node for "+siteName+" is missing.");

        String configPagePath = OOYALA_CONFIG_PATH+"/"+siteName;

        OoyalaApiCredential credentials =  getCredentialsForPath(configPagePath);


        return credentials;
    }




    @Activate
    public void activate()
    {
        logger.info("Activate method for custom ooyala configuration for multiple sites..");

        try
        {
        updateApiKeyCache();

        }
        catch(OoyalaConfigurationException ce)
        {
            logger.error("Ooyala Configuration Exception "+ ce);
        }
    }


}
