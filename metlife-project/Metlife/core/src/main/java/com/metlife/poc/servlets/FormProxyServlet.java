package com.metlife.poc.servlets;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.metlife.poc.util.MetLifeConstants;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import com.xumak.base.Constants;

/**
 * Form Proxy Servlet.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-
 * Change History
 * ----------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 2016/01/04 | Jorge Hernandez       | Initial Creation
 * ----------------------------------------------------------------------------
 */
@Service(value = FormProxyServlet.class)
@SlingServlet(
        paths = {FormProxyServlet.SERVLET_PATH},
        methods = {"POST"},
        metatype = true,
        extensions = {MetLifeConstants.JSON}
)
@Properties({
        @Property(name = "service.description", value = "MetLife Forms Proxy "
                + "Servlet"),
        @Property(name = FormProxyServlet.METLIFE_SERVICE_URL_PN,
                label = "Authorization Server URL",
                value = "https://test.salesforce.com/servlet/servlet"
                        + ".WebToLead?encoding=UTF-8"),

})
public class FormProxyServlet extends SlingAllMethodsServlet {

    /**
     * metlifeServiceUrl.
     */
    private String metlifeServiceUrl;
    /**
     * SERVLET_PATH.
     */
    protected static final String SERVLET_PATH = "/bin/forms/formService";
    /**
     * METLIFE_SERVICE_URL_PN.
     */
    protected static final String METLIFE_SERVICE_URL_PN = "web2LeadUrl";
    /**
     * Logger.
     */
    private final Logger lOG = LoggerFactory.getLogger(FormProxyServlet.class);
    /**
     * TIMEOUT.
     */
    private static final int TIMEOUT = 3000;

    /**
     * CONTENT_TYPE.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * CONTENT_LENGTH.
     */
    public static final String CONTENT_LENGTH = "Content-Length";
    /**
     * ACCEPT_LANGUAGE.
     */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    /**
     * ACCEPT_ENCODING.
     */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    /**
     * ACCEPT.
     */
    public static final String ACCEPT = "Accept";

    /**
     * Activate.
     * @param context ComponentContext.
     */
    @Activate
    protected final void activate(final ComponentContext context) {

        metlifeServiceUrl = getProperty(METLIFE_SERVICE_URL_PN, context);
    }

    /**
     * Get property.
     * @param propertyName String.
     * @param context ComponentContext.
     * @return String.
     */
    private String getProperty(final String propertyName,
                               final ComponentContext context) {
        final Dictionary properties = context.getProperties();
        return PropertiesUtil.toString(properties.get(propertyName),
                Constants.BLANK);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void doPost(final SlingHttpServletRequest request, final
    SlingHttpServletResponse response)
            throws ServletException, IOException {

        final Enumeration<String> headerNames = request.getHeaderNames();
        final Map<String, String> headers = Maps.newHashMap();
        final int three = 3;
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }


        final String resourceURL = metlifeServiceUrl;

        String responseString = Constants.BLANK;

        // Set a socket timeout
        final HttpClientParams httpClientParams = new HttpClientParams();
        httpClientParams.setSoTimeout(TIMEOUT);

        // Create an instance of HttpClient.
        final HttpClient client = new HttpClient(httpClientParams);

        // Create a method instance.
        final PostMethod method = new PostMethod(resourceURL);

        method.setRequestHeader(CONTENT_TYPE,
                "application/x-www-form-urlencoded; charset=UTF-8");
        method.setRequestHeader(CONTENT_LENGTH, request.getHeader(
                CONTENT_LENGTH));
        //method.setRequestHeader(ACCEPT_LANGUAGE, request.getHeader
        // (ACCEPT_LANGUAGE));
        //method.setRequestHeader(ACCEPT_ENCODING, request.getHeader
        // (ACCEPT_ENCODING));
        //method.setRequestHeader(ACCEPT, request.getHeader(ACCEPT));

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(three, false));


        final InputStreamRequestEntity body = new InputStreamRequestEntity(new
                ReaderInputStream(request.getReader()));
        method.setRequestEntity(body);


        try {
            // Execute the method.
            final int statusCode = client.executeMethod(method);
            lOG.debug("The request to {} returned a {} status code.",
                    resourceURL, statusCode);

            if (statusCode != HttpStatus.SC_OK) {
                lOG.debug("Method failed: " + method.getStatusLine());
            }
            final byte[] responseBody = method.getResponseBody();
            responseString = new String(responseBody);

        } catch (SocketTimeoutException e) {
            lOG.error("The request to {} exceeded the {} miliseconds it had "
                    + "to respond.", resourceURL, TIMEOUT);
        } catch (HttpException e) {
            lOG.error("Fatal protocol violation: " + e.getMessage());
        } catch (IOException e) {
            lOG.error("Fatal transport error: " + e.getMessage());
        } finally {
            method.releaseConnection();
        }
        final Map<String, Object> wrapper = Maps.newHashMap();


        if (!responseString.equals(Constants.BLANK)) {
            wrapper.put(MetLifeConstants.RESPONSE, "success");
        } else {
            wrapper.put(MetLifeConstants.RESPONSE, "fail");
        }

        //set application/json as the response content type
        response.setContentType(MetLifeConstants.JSON_CONTENT_TYPE);

        //set UTF-8 as the character encoding
        response.setCharacterEncoding(MetLifeConstants.UTF8_CHARSET);

        response.getWriter().write(new Gson().toJson(wrapper));

    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void doGet(final SlingHttpServletRequest request, final
    SlingHttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
