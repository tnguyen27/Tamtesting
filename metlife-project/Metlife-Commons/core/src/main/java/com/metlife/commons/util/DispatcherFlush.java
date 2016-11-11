package com.metlife.commons.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatcher Flush.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer     | Changes
 * 1.0     | 2015/09/15 | Chepe         | Initial Creation
 * 1.1     | 2016/07/28 | Lesly Quinonez| Adapt to Metlife.
 * ----------------------------------------------------------------------------
 */
public class DispatcherFlush {
    /** Dispatcher URL. **/
    private String url;
    private String path;
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherFlush.class);

    /**
     * Constructor for dispatcher flush object.
     * @param url dispatcher URL
     * @param path resouce path
     */
    public DispatcherFlush(final String url, final String path) {
        this.url = url;
        this.path = path;
    }

    /**
     * Sends the flush trigger to dispatcher.
     * @return dispatcher response.
     */
    public String flush() {
        final HttpClient client = new HttpClient();

        final PostMethod method = new PostMethod(this.url);
        method.setRequestHeader("CQ-Action", "Activate");
        method.setRequestHeader("CQ-Handle", this.path);
        method.setRequestHeader("Content-length", String.valueOf(0));
        method.setRequestHeader("Content-Type", "application/octet-stream");

        try {
            client.executeMethod(method);
        } catch (Exception e) {
            LOGGER.error("There was a problem in the flush process.", e);
        } finally {
            method.releaseConnection();
        }

        // response = get.getResponseBodyAsString();
        final String response = method.getStatusText();
        LOGGER.info("Response status: {}", response);

        return response;
    }

}
