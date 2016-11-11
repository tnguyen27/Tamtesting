package com.metlife.commons.sso.saml;

import com.google.common.collect.Sets;
import org.apache.jackrabbit.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * PostBinding.
 *
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 06/23/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
public class PostBinding {
    /** Response parameter. */
    public static final String SAML_RESPONSE_PARAM = "SAMLResponse";
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(PostBinding.class);

    /**
     * .
     * @param request .
     * @return .
     * @throws IOException .
     */
    public Map<String, String> receive(final HttpServletRequest request) throws IOException {
        final String samlResponse = request.getParameter(SAML_RESPONSE_PARAM);
        final Map<String, String> samlInfo = (Map<String, String>) Sets.newHashSet();
        log.error(samlResponse);
        if (samlResponse != null || samlResponse.length() > 0) {
            final String inputStream = Base64.decode(samlResponse);
            log.error(inputStream);
        }
        return samlInfo;
    }
}
