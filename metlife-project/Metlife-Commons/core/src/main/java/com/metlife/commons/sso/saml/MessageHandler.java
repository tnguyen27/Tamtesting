package com.metlife.commons.sso.saml;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * MessageHandler.
 * Parses the Assertion and fills a map with the assertion information.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 06/23/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
public class MessageHandler extends DefaultHandler {
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    /** . */
    public static final String ATTR_ELEMENT = "ns2:Attribute";
    /** . */
    public static final String ATTR_ELEMENT_VALUE = "ns2:AttributeValue";
    /** Assertion Domain. */
    public static final String ASSERTION_DOMAIN = "ns2:Audience";
    /** Assertion Status. */
    public static final String ASSERTION_STATUS = "StatusCode";
    /** Assertion Success Value. */
    public static final String SUCCESS_STATUS = "urn:oasis:names:tc:SAML:2.0:status:Success";
    /** . */
    private Map<String, String> attrs;
    /** . */
    private List<String> attrList;
    /** . */
    private boolean isAttr;
    /** True if is the assertion domain. */
    private boolean isAssertion;

    /**
     * .
     */
    public MessageHandler() {
        attrs = new HashMap<String, String>();
        attrList = Lists.newArrayList();
    }

    /**
     * Parse the SAML assertion.
     * @param uri Namespace URI.
     * @param localName Local name without prefix.
     * @param qName Qualified name.
     * @param attributes attributes related to the element.
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName,
            final Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase(ATTR_ELEMENT)) {
            final String attrName = attributes.getValue("Name");
            attrList.add(attrName);
        }

        if (qName.equalsIgnoreCase(ATTR_ELEMENT_VALUE)) {
            isAttr = true;
        } else {
            isAttr = false;
        }

        if (qName.equalsIgnoreCase(ASSERTION_DOMAIN)) {
            isAssertion = true;
        }

        if (qName.equalsIgnoreCase(ASSERTION_STATUS)) {
            attrs.put(ASSERTION_STATUS, attributes.getValue("Value"));
        }
    }

    /**
     * Gets the AttributeValue and Assertion domain.
     * @param ch Characters array.
     * @param start Start position in the character array.
     * @param length The number of characters to use from the chracter array.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (isAttr) {
            final String attrVal = new String(ch, start, length);
            attrList.add(attrVal);
            isAttr = false;
        }

        if (isAssertion) {
            attrs.put(ASSERTION_DOMAIN, new String(ch, start, length));
            isAssertion = false;
            log.info("Configure the assertion domain.");
        }
    }

    /**
     * Adds attributes on the attrs map.
     * @return attrs map.
     */
    public Map<String, String> getAttrs() {
        final Iterator<String> attrsIterator = attrList.iterator();
        while (attrsIterator.hasNext()) {
            attrs.put(attrsIterator.next(), attrsIterator.next());
        }
        return attrs;
    }
}
