package com.metlife.commons.sso.saml;

import com.xumak.base.Constants;
import org.apache.jackrabbit.api.security.authentication.token.TokenCredentials;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * TokenUtil .
 * Utils used for get and set token information.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 06/24/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */

public final class TokenUtil {
    /** Authorization type. */
    private static final String AUTH_TYPE = "TOKEN";
    /** Token attribute. */
    private static final String TOKEN_ATTRIBUTE = ".token";
    /** JCR credentials. */
    private static final String JCR_CREDENTIALS_ATTRIBUTE = "user.jcr.credentials";
    /** Repository ID. */
    private static final String REPO_DESC_ID = "crx.repository.systemid";
    /** Repository Cluster ID. */
    private static final String REPO_DESC_CLUSTER_ID = "crx.cluster.id";
    /** Log. */
    private static final Logger LOG = LoggerFactory.getLogger(TokenUtil.class);
    /**
     * Constructor.
     */
    private TokenUtil() {

    }

    /**
     * Creates credentials if the user exists then use it otherwise creates a new user with this information.
     * @param request request object.
     * @param response response object.
     * @param repository JCR repository.
     * @param userId User ID.
     * @param httpOnly Is http only.
     * @param domain Domain.
     * @return Authentication information.
     * @throws RepositoryException If the user doesn't have access to connect with the repository.
     */
    public static AuthenticationInfo createCredentials(final HttpServletRequest request,
            final HttpServletResponse response, final SlingRepository repository, final String userId,
            final boolean httpOnly, final String domain)
            throws RepositoryException {
        Session adminSession = null;
        Session userSession = null;
        AuthenticationInfo authInfo = null;
        try {
            adminSession = repository.loginAdministrative(null);
            final SimpleCredentials sc = new SimpleCredentials(userId, new char[0]);
            sc.setAttribute(TOKEN_ATTRIBUTE, Constants.BLANK);
            userSession = adminSession.impersonate(sc);

            final TokenCredentials tokenCredentials = new TokenCredentials((String) sc.getAttribute(TOKEN_ATTRIBUTE));
            authInfo = new AuthenticationInfo(AUTH_TYPE, userId);
            authInfo.put(JCR_CREDENTIALS_ATTRIBUTE, tokenCredentials);

            String repositoryId = repository.getDescriptor(REPO_DESC_CLUSTER_ID);
            if (repositoryId == null) {
                repositoryId = repository.getDescriptor(REPO_DESC_ID);
            }
            if (repositoryId == null) {
                repositoryId = UUID.randomUUID().toString();
            }
            final TokenCookie tokenCookie = new TokenCookie(request, response);
            tokenCookie.update(repositoryId, tokenCredentials.getToken(),
                    adminSession.getWorkspace().getName(), httpOnly, domain);
        } catch (RepositoryException e) {
            LOG.error("Failed to generate login-token: Could not access Repository"
                    + e.getMessage());
        } finally {
            if (userSession != null) {
                userSession.logout();
            }
            if (adminSession != null) {
                adminSession.logout();
            }
        }
        return authInfo;
    }
}
