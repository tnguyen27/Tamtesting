<%@page session="false"%><%@page import="com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.DropTarget, com.day.cq.wcm.foundation.Placeholder" %><%
  %><%@include file="/libs/foundation/global.jsp" %><%


WCMMode mode = WCMMode.DISABLED.toRequest(request);

// Remember the mode on the original page before any reference started
String originalModeKey = "com.day.cq.wcm.components.reference.mode";
WCMMode originalMode = (WCMMode)request.getAttribute(originalModeKey);
if (originalMode == null) {
	originalMode = mode;
    request.setAttribute(originalModeKey, originalMode);
}


boolean needToCloseDiv = false;
try {
    // Use request attributes to guard against reference loops
    String path = resource.getPath();
    String key = "com.day.cq.wcm.components.reference:" + path;
    if (request.getAttribute(key) == null) {
        request.setAttribute(key, Boolean.TRUE);
    } else {
        throw new IllegalStateException("Reference loop: " + path);
    }

    //drop target css class = dd prefix + name of the drop target in the edit config
    String ddClassName = DropTarget.CSS_CLASS_PREFIX + "paragraph";

    // Include the target paragraph by path
    String target = properties.get("path", String.class);
    if (target != null) {
        target+="/jcr:content/container";
        %><div class="<%= ddClassName %>"><% needToCloseDiv = true; %><sling:include path="<%= target %>"/></div><% needToCloseDiv = false; %><%
    } else if (mode == WCMMode.EDIT) {
        String classicPlaceholder =
                "<p><img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-reference-placeholder " + ddClassName + "\" alt=\"\"></p>";
        %><%= classicPlaceholder %><%
    }
} catch (Exception e) {
    // Log errors always
    log.error("Reference component error", e);
    // Display errors only in edit mode
    if (originalMode == WCMMode.EDIT) {
        %><p>Reference error: <%= xssAPI.encodeForHTML(e.toString()) %></p><%
    }
} finally {
    if (needToCloseDiv) {
        %></div><%
    }
    mode.toRequest(request);
}
%>
