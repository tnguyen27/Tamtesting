package com.metlife.commons.sso;

import com.metlife.commons.sso.saml.Message;
import com.metlife.commons.sso.saml.TokenCookie;
import com.metlife.commons.sso.saml.TokenUtil;
import com.xumak.base.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.util.Base64;
import org.apache.sling.auth.core.spi.AbstractAuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;

/**
 * SSOAuthenticationHandler.
 * This authentication handler allows the authentication with Metlife SAML and AEM authentication.
 * If the user exists then only modify the information obtained from Metlife SAML otherwise creates
 * a new user.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 06/21/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
@Component(
        label = "MetLife - SAML Authentication Handler",
        description = "Service in charge to authenticate users with MetLife service.",
        metatype = true, immediate = true)
@Service({AuthenticationHandler.class})
@Properties({
        @Property(name = "path", value = {"/"}, cardinality = SSOAuthenticationHandler.PATH_CARDINALITY),
        @Property(name = "sso.url", value = "/sso"),
        @Property(name = "sso.enabled", value = "false"),
        @Property(name = "service.ranking", intValue = {SSOAuthenticationHandler.RANKING}),
        @Property(name = "idpUrl",
        value = "https://ssofed-qa.metlife.com/affwebservices/public/saml2sso?SPID=https://author.metlife.xumak.com"),
        @Property(name = "idpUrls", value = {""}, cardinality = SSOAuthenticationHandler.PATH_CARDINALITY),
        @Property(name = "sso.redirect", value = "/siteadmin"),
        @Property(name = "authtype", value = {"SAML"}),
        @Property(name = "userIDAttribute", value = "Email"),
        @Property(name = "sso.cookieDomain", value = ""),
        @Property(name = "userAttributes", value = {""}, cardinality = SSOAuthenticationHandler.PATH_CARDINALITY),
        @Property(name = "defaultLogin", value = "/libs/granite/core/content/login.html"),
        @Property(name = "sso.logoutRedirect", value = "", cardinality = SSOAuthenticationHandler.PATH_CARDINALITY)
        })
public class SSOAuthenticationHandler extends AbstractAuthenticationHandler
        implements AuthenticationHandler {
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(SSOAuthenticationHandler.class);
    /** Path Property. */
    public static final String PATH = "path";
    /** Ranking Property. */
    public static final String RANKING_ATTR = "service.ranking";
    /** Auth type property. */
    public static final String AUTH_TYPE = "authtype";
    /** IDP Url property. */
    public static final String IDP_URL = "idpUrls";
    /** user id property. */
    public static final String USER_ID_ATTR = "userIDAttribute";
    /** user attributes property. */
    public static final String USER_ATTRS = "userAttributes";
    /** SSO path property. */
    public static final String SSO_PATH = "sso.url";
    /** SSO redirect property. */
    public static final String SSO_REDIRECT_PATH = "sso.redirect";
    /** SSO domain property. */
    public static final String SSO_DOMAIN_ATTR = "sso.cookieDomain";
    /** SSO logout redirect property. */
    public static final String SSO_LOGOUT_ATTR = "sso.logoutRedirect";
    /** SSO enabled service property. */
    public static final String SSO_ENABLED = "sso.enabled";
    /** Default Login. */
    public static final String DEFAULT_LOGIN = "defaultLogin";
    /** Number of path allowed.  */
    public static final int PATH_CARDINALITY = 100;
    /** Service's ranking. */
    public static final int RANKING = 5002;
    /** Attribute which will be used as the user id. */
    private String userIDAttribute = "Email";
    /** Attributes which have other information. */
    private String[] userAttributes;
    /** SSO default path. */
    private String ssoPath = "/sso";
    /** SSO redirect path. */
    private String ssoRedirectPath = "/siteadmin";
    /** Saml request path. */
    private static final String SAML_PATH = "saml_request_path";
    /** SSO cookie domain. */
    private String ssoCookieDomain;
    /** SSO logout redirect. */
    private String[] ssoLogout;
    /** IDP Url. */
    private String[] idpUrls;
    /** Separator. */
    private static final String SEPARATOR = "::";
    /** Enabled service. */
    private boolean isEnabled;
    /** default Login. */
    private String defaultLogin = "/libs/granite/core/content/login.html";

    @Reference
    private SlingRepository repository;

    /**
     * Constructor.
     */
    public SSOAuthenticationHandler() {
        //super();
    }

    /**
     * Activate method.
     * @param context component context.
     * @throws Exception .
     */
    @Activate
    public void activate(final ComponentContext context) throws Exception {
        this.userIDAttribute = PropertiesUtil.toString(context.getProperties().get(USER_ID_ATTR), userIDAttribute);
        this.userAttributes =
                PropertiesUtil.toStringArray(context.getProperties().get(USER_ATTRS));
        this.ssoPath = PropertiesUtil.toString(context.getProperties().get(SSO_PATH), ssoPath);
        this.ssoRedirectPath = PropertiesUtil.toString(context.getProperties().get(SSO_REDIRECT_PATH), ssoRedirectPath);
        this.ssoCookieDomain = PropertiesUtil.toString(context.getProperties().get(SSO_DOMAIN_ATTR), ssoCookieDomain);
        this.defaultLogin = PropertiesUtil.toString(context.getProperties().get(DEFAULT_LOGIN), defaultLogin);
        this.ssoLogout = PropertiesUtil.toStringArray(context.getProperties().get(SSO_LOGOUT_ATTR));
        this.idpUrls = PropertiesUtil.toStringArray(context.getProperties().get(IDP_URL));
        this.isEnabled = PropertiesUtil.toBoolean(context.getProperties().get(SSO_ENABLED), false);
    }

    @Override
    public AuthenticationInfo extractCredentials(final HttpServletRequest request,
            final HttpServletResponse response) {
        AuthenticationInfo authenticationInfo = null;
        if (!isDisabled() && StringUtils.isNotEmpty(request.getPathInfo())
                && request.getPathInfo().endsWith(this.ssoPath)) {
            authenticationInfo = handleLogin(request, response);
        }
        return authenticationInfo;
    }

    /**
     * Checks if the service is enabled.
     * @return false if the service is enabled.
     */
    public boolean isDisabled() {
        return !this.isEnabled;
    }

    /**
     * Obtains the SAML information and gets the user information.
     * @param request request object.
     * @param response  response object.
     * @return User information.
     * @throws java.io.IOException When the user can't be created by the service.
     */
    public AuthenticationInfo handleLogin(final HttpServletRequest request, final HttpServletResponse response) {
        AuthenticationInfo authenticationInfo = null;
        Session session = null;
        try {
            final Enumeration parameters = request.getParameterNames();
            while (parameters.hasMoreElements()) {
                final String parameter = (String) parameters.nextElement();
                final String inputStream = Base64.decode(request.getParameter(parameter));
                final Message message = new Message(inputStream);
                if (message.isValidMessage()) {
                    session = repository.loginAdministrative(null);
                    session.save();
                    authenticateUser(session, message);
                    session.save();
                    authenticationInfo = TokenUtil.createCredentials(request, response, repository,
                            message.getValue(this.userIDAttribute), true, ssoCookieDomain);
                }
            }
        } catch (RepositoryException e) {
            log.error("[Handle Login] : Problem creating the user node. " + e.getMessage());
            authenticationInfo = AuthenticationInfo.FAIL_AUTH;
        } catch (Exception e) {
            log.error("[Handle Login] : " + e.getMessage());
            authenticationInfo = AuthenticationInfo.FAIL_AUTH;
        } finally {
            if (session != null) {
                session.logout();
            }
        }
        if (authenticationInfo == null) {
            authenticationInfo = AuthenticationInfo.FAIL_AUTH;
        } else {
            authenticationInfo.put("$$auth.info.login$$", new Object());
        }
        return authenticationInfo;
    }

    /**
     * Sets other attributes related to the user.
     * @param node User node.
     * @param message User information obtained from SAML configurations..
     * @throws RepositoryException Problems to set properties on the expected node.
     */
    private void setAttributes(final Node node, final Message message)
            throws RepositoryException {
        final Iterator attrIterator = Arrays.asList(userAttributes).iterator();
        while (attrIterator.hasNext()) {
            final String[] attributes = ((String) attrIterator.next()).split("=");
            if (attributes.length >= 2) {
                if (attributes[1].contains(Constants.SLASH)) {
                    final String relPath = attributes[1].substring(0, attributes[1].lastIndexOf(Constants.SLASH));
                    final String propertyName = attributes[1].substring(attributes[1].lastIndexOf(Constants.SLASH) + 1);
                    final Node currNode = node.getNode(relPath);

                    currNode.setProperty(propertyName, message.getValue(attributes[0]));
                } else {
                    node.setProperty(attributes[1], message.getValue(attributes[0]));
                }
            }
        }
    }

    /**
     * Authenticates the user.
     * @param session session object.
     * @param message SAML message with user information.
     * @return .
     */
    public Authorizable authenticateUser(final Session session, final Message message) {
        Authorizable authorizable = null;
        try {
            final UserManager userManager = ((JackrabbitSession) session).getUserManager();
            authorizable = userManager.getAuthorizable(message.getValue(this.userIDAttribute));
            if (authorizable == null) {
                final int keySize = 16;
                final byte[] key = new byte[keySize];
                new Random().nextBytes(key);
                authorizable = userManager.createUser(message.getValue(this.userIDAttribute),
                        Arrays.toString(key));
                log.error(Arrays.toString(key));
            }

            if (authorizable != null) {
                final Node principalNode = session.getNode(authorizable.getPath());
                setAttributes(principalNode, message);
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return authorizable;
    }

    @Override
    public boolean requestCredentials(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final Cookie[] cookies = request.getCookies();
        boolean requestPathCookieSet = false;
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(SAML_PATH) && !cookie.getValue().equals(Constants.BLANK)) {
                    requestPathCookieSet = true;
                }
            }
        }
        final String queryString = request.getQueryString();
        final String requestPath = request.getRequestURI();
        final int maxAge = 300;
        if (!requestPathCookieSet) {
            Cookie cookie = null;
            if (queryString == null) {
                cookie = new Cookie(SAML_PATH, requestPath);
            } else {
                cookie = new Cookie(SAML_PATH, requestPath + queryString);
            }
            cookie.setMaxAge(maxAge);
            cookie.setPath(Constants.SLASH);
            response.addCookie(cookie);
            requestPathCookieSet = true;
        }
        if (this.idpUrls != null) {
            final Iterator<String> idpUrlsIterator = Arrays.asList(this.idpUrls).iterator();
            while (idpUrlsIterator.hasNext()) {
                final String idpUrl = idpUrlsIterator.next();
                if (idpUrl.contains(SEPARATOR)) {
                    final String[] idpInfo = idpUrl.split(SEPARATOR);
                    final int idpLength = 2;
                    if (idpInfo.length == idpLength) {
                        if (request.getServerName().equalsIgnoreCase(idpInfo[1])) {
                            response.sendRedirect(idpInfo[0]);
                            break;
                        }
                    }
                } else {
                    log.error("The IDP list has problem with the configuration.");
                }
            }
            response.sendRedirect(defaultLogin);
        }
        return requestPathCookieSet;
    }

    @Override
    public void dropCredentials(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        clearRequestPathCookie(request, response);
        if (ssoLogout != null) {
            final Iterator<String> ssoLogoutIterator = Arrays.asList(this.ssoLogout).iterator();
            while (ssoLogoutIterator.hasNext()) {
                final String ssoLogoutString = ssoLogoutIterator.next();
                if (ssoLogoutString.contains(SEPARATOR)) {
                    final String[] ssoLogoutInfo = ssoLogoutString.split(SEPARATOR);
                    final int ssoLogoutLength = 2;
                    if (ssoLogoutInfo.length == ssoLogoutLength) {
                        if (request.getServerName().equalsIgnoreCase(ssoLogoutInfo[1])) {
                            response.sendRedirect(ssoLogoutInfo[0]);
                        }
                    }
                } else {
                    log.error("The Logout list has problem with the configuration.");
                }
            }
        }
    }

    /**
     * Clears the authentication cookies.
     * @param request request object.
     * @param response response object.
     */
    protected void clearRequestPathCookie(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        final int minAge = 1;
        if (cookies != null) {
            for (final Cookie cookie: cookies) {
                if (cookie.getName().equals(SAML_PATH) || cookie.getName().equals(TokenCookie.NAME)) {
                    cookie.setValue(Constants.BLANK);
                }
            }
        }
        final Cookie cookie = new Cookie(SAML_PATH, Constants.BLANK);
        cookie.setMaxAge(minAge);
        cookie.setPath(Constants.SLASH);
        response.addCookie(cookie);
    }

    /**
     * Authentication Succeeded.
     * @param request request object.
     * @param response response object.
     * @param authenticationInfo authentication information.
     * @return
     */
    @Override
    public boolean authenticationSucceeded(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationInfo authenticationInfo) {
        boolean isAuthenticated = false;
        if (request.getPathInfo().endsWith(ssoPath)) {
            try {
                isAuthenticated = true;
                response.sendRedirect(this.ssoRedirectPath);
            } catch (IOException e) {
                log.error("[Authentication Succeeded] : Could not read request. " + e.getMessage());
            }
        }
        return isAuthenticated;
    }


}
