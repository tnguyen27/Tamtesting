package com.xumak.aem.akamai.ccu.impl

import com.day.cq.replication.ReplicationAction
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service
import org.apache.felix.scr.annotations.Property
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.commons.osgi.PropertiesUtil
import org.apache.sling.event.EventUtil
import org.apache.sling.event.jobs.JobManager
import org.osgi.service.component.ComponentContext
import org.osgi.service.event.Event
import org.osgi.service.event.EventConstants
import org.osgi.service.event.EventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * AkamaiEventHandler - Listen to repository replication notification to invalidate Akamai cache when it is needed
 *
 * @author Sebastien Bernard
 */
@Component(label = "Akamai Event Handler",
		description = "Listen to repository replication notification to invalidate Akamai cache when it is needed",
		metatype = true, immediate = true)
@Service(value = [AkamaiEventHandler.class, EventHandler.class])
@Properties(value = [
	@Property(name = EventConstants.EVENT_TOPIC, value = ReplicationAction.EVENT_TOPIC, label = "Event topic")
])
class AkamaiEventHandler implements EventHandler {
	private final static Logger LOG = LoggerFactory.getLogger(AkamaiEventHandler.class)

	@Reference
	private JobManager jobManager;

	@Reference
	private ResourceResolverFactory resolverFactory;

	private Set<String> pathsHandled;

	@Override
	public void handleEvent(Event event) {
		LOG.info("++++++++++++++++ start at public void handleEvent(Event event)")

		if (EventUtil.isLocal(event)) {
			LOG.info("Start handling event to add Akamai job")
			String[] paths = PropertiesUtil.toStringArray(event.getProperty(com.xumak.aem.akamai.ccu.impl.FlushAkamaiItemsJob.PATHS))
            LOG.info("PATHS => "+ Arrays.toString(paths));
			Set<String> validPaths = filterValidPath(paths);

			if (!validPaths.isEmpty()) {
				jobManager.addJob(com.xumak.aem.akamai.ccu.impl.FlushAkamaiItemsJob.JOB_TOPIC, buildJobProperties(validPaths));
				LOG.info("Akamai job Added")
			} else {
				LOG.info("{} has no valid path(s) to purge. No Job added", paths)
			}
		}
		LOG.info("---------------- end at public void handleEvent(Event event)")
	}

	private Set<String> filterValidPath(String[] paths) {
		LOG.info("++++++++++++++++ start at private Set<String> filterValidPath(String[] paths)")
		Set<String> validPaths = new HashSet<>();
		for (String path : paths) {
            LOG.info(" 1 path => " + path)
            validPaths.add(path)
		}
		LOG.info("---------------- end at private Set<String> filterValidPath(String[] paths)")
		return validPaths
	}

	private static Map<String, Object> buildJobProperties(Set<String> paths) {
		LOG.info("++++++++++++++++ start at private static Map<String, Object> buildJobProperties(Set<String> paths)")
		Map<String, Object> jobProperties = new HashMap();
		jobProperties.put(com.xumak.aem.akamai.ccu.impl.FlushAkamaiItemsJob.PATHS, paths);
		LOG.info("---------------- end at private static Map<String, Object> buildJobProperties(Set<String> paths)")
		return jobProperties;
	}

	@SuppressWarnings("GroovyUnusedDeclaration")
	protected void activate(ComponentContext context) {
		LOG.info("++++++++++++++++ start at protected void activate(ComponentContext context)")
		pathsHandled = new HashSet<String>()
        if( null != context.getProperties().get("pathsHandled") ) {
            pathsHandled.addAll(PropertiesUtil.toStringArray(context.getProperties().get("pathsHandled")));
        }
		LOG.info("---------------- end at protected void activate(ComponentContext context)")
	}

	@SuppressWarnings("GroovyUnusedDeclaration")
	protected void deactivate() {
		LOG.info("++++++++++++++++ start at protected void deactivate()")
		pathsHandled = Collections.emptySet();
		LOG.info("---------------- end at protected void deactivate()")
	}
}
