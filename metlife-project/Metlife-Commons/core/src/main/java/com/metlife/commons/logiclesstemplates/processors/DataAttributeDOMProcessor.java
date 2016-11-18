package com.metlife.commons.logiclesstemplates.processors;

import com.xumak.base.templatingsupport.DOMProcessor;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static com.xumak.base.Constants.HIGHEST_PRIORITY;

/**
 * DataDOMProcessor
 * This processor adds the prefix "data-" in the attributes.
 * -­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-­‐-‐
 * Change History
 * --------------------------------------------------------------------------------------
 * Version | Date       | Developer             | Changes
 * 1.0     | 11/17/2016 | Lesly Quiñonez        | Initial Creation
 * --------------------------------------------------------------------------------------
 */
@Component
@Service
public class DataAttributeDOMProcessor implements DOMProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataAttributeDOMProcessor.class);
	private static final String XUMAK_PREFIX = "xk-";
	private static final String DATA_PREFIX = "data-";
	@Override
	public int priority() {
		return HIGHEST_PRIORITY;
	}

	@Override
	public void process(SlingHttpServletRequest slingHttpServletRequest, Document document) throws Exception {
		//Gets all the elements that contains an attribute that begin with xk
		Elements elements = document.select("[^xk]");
		Iterator<Element> elementIterator = elements.iterator();
		while (elementIterator.hasNext()) {
			Element element = elementIterator.next();
			Attributes attributes = element.attributes();
			Iterator<Attribute> attributesIterator = attributes.iterator();
			while (attributesIterator.hasNext()) {
				Attribute attribute = attributesIterator.next();
				//Adds the prefix "data-" to the attributes that contains "xk-"
				if (attribute.getKey().contains(XUMAK_PREFIX)) {
					element.removeAttr(attribute.getKey());
					element.attr(DATA_PREFIX + attribute.getKey(), attribute.getValue());
				}
			}
		}
	}
}
