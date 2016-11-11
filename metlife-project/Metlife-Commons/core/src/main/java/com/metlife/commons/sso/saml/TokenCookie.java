package com.metlife.commons.sso.saml;

import com.day.text.Text;
import com.xumak.base.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;

/**
 * TokenCookie .
 * <description> .
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 06/24/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
public class TokenCookie {
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(TokenCookie.class);
    /** Token cookie name. */
    public static final String NAME = "login-token";
    /** parameter's name. */
    public static final String PARAM_NAME = "j_login_token";
    /** Attribute's name. */
    public static final String ATTR_NAME = TokenCookie.class.getName();
    /** Token information. */
    private final Map<String, Info> infos;
    /** Request object. */
    private final HttpServletRequest request;
    /** response object. */
    private final HttpServletResponse response;

    /**
     * Token constructor.
     * @param request request object.
     * @param response response object.
     */
    public TokenCookie(final HttpServletRequest request, final HttpServletResponse response) {
        this.infos = new TreeMap();
        this.request = request;
        this.response = response;
    }

    /**
     * Token information.
     * @return token information.
     */
    public Map<String, Info> getInfos() {
        return this.infos;
    }

    /**
     * Checks if the request has token then gets the token information.
     * @return token cookie with the information collectend from the request.
     */
    public TokenCookie fromRequest() {
        TokenCookie tokenCookie = (TokenCookie) this.request.getAttribute(ATTR_NAME);
        if (tokenCookie == null) {
            String tokenString = getCookie(NAME);
            if (tokenString == null || tokenString.length() == 0) {
                tokenString = request.getParameter(PARAM_NAME);
            }
            tokenCookie = fromString(tokenString);
            this.request.setAttribute(ATTR_NAME, tokenCookie);
        }
        return tokenCookie;
    }

    /**
     * Gets the token information.
     * @param repoId namespace where is stored the information.
     * @return token information.
     */
    public Info getTokenInfo(final String repoId) {
        Info info = (Info) request.getAttribute(Info.ATTR_NAME);
        if (info == null) {
            final TokenCookie t = fromRequest();
            info = (Info) t.getInfos().get(repoId);
            if (info == null) {
                info = Info.INVALID;
            }
            request.setAttribute(Info.ATTR_NAME, info);
        }
        return info;
    }

    /**
     * Gets the port from the request.
     * @return request's port.
     */
    public String getPort() {
        final String host = request.getHeader("Host");
        String port = "";
        if (host == null || host.length() == 0) {
            LOG.warn("Request to {} does not include a host header. Using default port.", request.getRequestURI());
        } else {
            final int idx = host.indexOf(':');
            if (idx > 0) {
                port = host.substring(idx + 1);
            }
        }
        if (port.length() == 0) {
            if (request.isSecure()) {
                port = "443";
            } else {
                port = "80";
            }
        }
        return port;
    }

    /**
     * Sets the cookie with the information collected from the request.
     * @param repoId JCR namespace.
     * @param token token information.
     * @param wsp workspace.
     * @param isHttpOnly is HTTP.
     * @param domain domain.
     */
    public void update(final String repoId, final String token, final String wsp,
            final boolean isHttpOnly, final String domain) {
        final TokenCookie tokenCookie = fromRequest();
        Info newInfo = Info.INVALID;
        if (token == null) {
            tokenCookie.getInfos().remove(repoId);
        } else {
            newInfo = new Info(token, wsp);
            tokenCookie.getInfos().put(repoId, newInfo);
        }
        request.setAttribute(Info.ATTR_NAME, newInfo);
        String path = request.getContextPath();
        if (path == null || path.length() == 0) {
            path = Constants.SLASH;
        }
        final String tokenCookieString = tokenCookie.toString();
        if (tokenCookieString.length() == 0) {
            setCookie(NAME, tokenCookieString, 0, path, domain, isHttpOnly, request.isSecure());
        } else {
            setCookie(NAME, tokenCookieString, -1, path, domain, isHttpOnly, request.isSecure());
        }
    }

    /**
     * Gets token information if it exists.
     * @param value cookie.
     * @return token cookie.
     */
    public TokenCookie fromString(final String value) {
        final TokenCookie tokenCookie = new TokenCookie(request, response);

        if (value != null) {
            final String escapeValue = Text.unescape(value);
            final String[] information = Text.explode(escapeValue.trim(), ';');
            for (final String info : information) {
                final String[] parts = Text.explode(info.trim(), ':', true);
                final int partLength = 3;
                if (parts.length != partLength) {
                    LOG.warn("invalid value in cookie: {}", info);
                } else {
                    tokenCookie.infos.put(parts[0].trim(),
                            new Info(Text.unescape(parts[1].trim()), Text.unescape(parts[2].trim())));
                }
            }
        }
        return tokenCookie;
    }

    /**
     * Removes the repo ID from the token information.
     * @param repoId Repository ID.
     * @return .
     */
    public boolean remove(final String repoId) {
        return this.infos.remove(repoId) != null;
    }

    /**
     * Converts the token cookie to string.
     * @return token cookie as string.
     */
    public String toString() {
        final StringBuilder b = new StringBuilder();
        String delim = "";
        for (final Map.Entry<String, Info> e : this.infos.entrySet()) {
            b.append(delim);
            if (((String) e.getKey()).length() > 0) {
                b.append((String) e.getKey());
                b.append(':');
            }
            b.append(e.getValue());
            delim = ";";
        }
        return Text.escape(b.toString());
    }

    /**
     * Gets the token value.
     * @param name cookie name.
     * @return token value.
     */
    public  String getCookie(final String name) {
        final Cookie[] cookies = request.getCookies();
        String cookieValue = null;
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(name) && !cookie.getValue().equals("")) {
                    cookieValue = cookie.getValue();
                }
            }
        }
        return cookieValue;
    }

    /**
     * Sets cookie on the response.
     * @param name cookie name.
     * @param value cookie value.
     * @param maxAge max age.
     * @param path path.
     * @param domain domain.
     * @param isHttpOnly is http.
     * @param isSecure  is secure.
     */
    public void setCookie(final String name, final String value, final int maxAge, final String path,
            final String domain, final boolean isHttpOnly, final boolean isSecure) {
        final StringBuilder header = new StringBuilder(name);

        header.append('=').append(value).append("; Path=").append(path);
        if (isHttpOnly) {
            header.append("; HttpOnly");
        }
        if (domain != null) {
            header.append("; Domain=");
            LOG.error("Domain....... " + domain);
            header.append(domain);
        }
        if (maxAge >= 0) {
            header.append("; Max-Age=");
            header.append(maxAge);
        }
        if (isSecure) {
            header.append("; Secure");
        }
        response.addHeader("Set-Cookie", header.toString());
    }

    public static class Info {
        /** Invalid status. */
        public static final Info INVALID = new Info(null, null);
        /** Attribute name. */
        public static final String ATTR_NAME = Info.class.getName();
        /** token. */
        private final String token;
        /** Workspace. */
        private final String workspace;

        /**
         * Constructor.
         * @param token token information.
         * @param workspace Workspace.
         */
        public Info(final String token, final String workspace) {
            this.workspace = workspace;
            this.token = token;
        }

        /**
         * Checks if the token information is valid.
         * @return true, if the token information is valid.
         */
        public boolean isValid() {
            return this.token != null && this.token.length() > 0;
        }

        /**
         * Converts the token information to string.
         * @return token information as string.
         */
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            if (token != null) {
                sb.append(Text.escape(this.token)).append(':');
                sb.append(Text.escape(this.workspace));
            }
            return sb.toString();
        }

        /**
         * Checks if a token information is equals to other.
         * @param o token information.
         * @return true, if the token information is equals.
         */
        public boolean equals(final Object o) {
            boolean isEquals = false;
            if (this == o) {
                isEquals = true;
            }
            if (o == null || getClass() != o.getClass()) {
                isEquals = false;
            }
            final Info info = (Info) o;
            if (this.token != null && !this.token.equals(info.token) || info.token != null) {
                isEquals = false;
            }
            if (this.token != null && !this.token.equals(info.token) || info.token != null) {
                isEquals = false;
            }
            if (this.workspace != null && !this.workspace.equals(info.workspace) || info.workspace != null) {
                isEquals = false;
            }
            return isEquals;
        }

        /**
         * Gets a hashcode from token information.
         * @return hashcode.
         */
        public int hashCode() {
            int result = 0;
            if (this.token != null) {
                this.token.hashCode();
            }
            final int resVal = 31;
            result *= resVal;
            if (workspace != null) {
                result += workspace.hashCode();
            }
            return result;
        }
    }
}
