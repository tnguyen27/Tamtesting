package com.metlife.commons.sso.saml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Message.
 * Parses the SAML message and stores the special information.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 06/23/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
public class Message {
    /**  */
    private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    /** Log. */
    private final Logger log = LoggerFactory.getLogger(Message.class);
    private MessageHandler messageHandler;

    /**
     * Parses the SAML message.
     * @param message SAML response.
     */
    public Message(final String message) {
        try {
            final SAXParser parser = saxParserFactory.newSAXParser();
            messageHandler = new MessageHandler();
            parser.parse(new ByteArrayInputStream(message.getBytes()), messageHandler);
        } catch (ParserConfigurationException e) {
            log.error("[Message] : Assertion can't be parsed. " + e.getMessage());
        } catch (SAXException e) {
            log.error("[Message] : " + e.getMessage());
        } catch (IOException e) {
            log.error("[Message] : " + e.getMessage());
        }

    }

    /**
     * Checks if the message is valid.
     * @return true, if the message is valid.
     */
    public boolean isValidMessage() {
        final Map<String, String> message = messageHandler.getAttrs();
        return message.containsKey("UserId") && message.containsKey("FirstName") && message.containsKey("LastName")
                && message.containsKey("Email");
    }

    /**
     * Returns the attribute's value.
     * @param attr Attribute's name.
     * @return Attribute's value.
     */
    public String getValue(final String attr) {
        return messageHandler.getAttrs().get(attr);
    }
}
