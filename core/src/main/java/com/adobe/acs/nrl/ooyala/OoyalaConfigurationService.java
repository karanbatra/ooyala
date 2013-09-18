package com.adobe.acs.nrl.ooyala;

import com.siteworx.cq5.ooyala.client.OoyalaApiCredential;
import com.siteworx.cq5.ooyala.service.OoyalaConfigurationException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author prajesh
 *         Date: 17/9/13
 *         Time: 5:04 PM
 *         To change this template use File | Settings | File Templates.
 */
public interface OoyalaConfigurationService {

    public void updateApiKeyCache() throws OoyalaConfigurationException;


    public OoyalaApiCredential getCredentialsForPath(String path) throws OoyalaConfigurationException;

    public OoyalaApiCredential getCredentialsForSite(String siteName) throws OoyalaConfigurationException;
}
