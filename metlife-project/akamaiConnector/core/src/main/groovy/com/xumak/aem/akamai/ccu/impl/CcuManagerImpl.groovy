package com.xumak.aem.akamai.ccu.impl

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HttpResponseException
import org.apache.felix.scr.annotations.*
import org.osgi.service.component.ComponentContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.InvalidParameterException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * CcuManagerImpl -
 *
 * @author Sebastien Bernard
 */
@Component(label = "Akamai CCU REST API Manager", description = "Manage calls to the Akamai CCU REST API", metatype = true, immediate = true, policy = ConfigurationPolicy.REQUIRE)
@Service(value = [com.xumak.aem.akamai.ccu.CcuManager.class])
class CcuManagerImpl implements com.xumak.aem.akamai.ccu.CcuManager {
	private static final Logger LOG = LoggerFactory.getLogger(CcuManagerImpl.class)
	private static final String DEFAULT_CCU_URL = "https://api.ccu.akamai.com"
	public static final com.xumak.aem.akamai.ccu.PurgeAction DEFAULT_PURGE_ACTION = com.xumak.aem.akamai.ccu.PurgeAction.REMOVE
	public static final com.xumak.aem.akamai.ccu.PurgeDomain DEFAULT_PURGE_DOMAIN = com.xumak.aem.akamai.ccu.PurgeDomain.PRODUCTION
	public static final String CONTENT_TYPE = "application/json"

	@Property(name = "rootCcuUrl", label = "Akamai CCU API URL", value = "https://api.ccu.akamai.com")
	private String rootCcuUrl;
	@Property(name = "userName", label = "Username")
	private String userName;
	@Property(name = "password", label = "Password")
	private String password;
	@Property(name = "defaultPurgeAction", label = "Default purge action", description = "Can be invalidate, remove (default)", value = "remove")
	private com.xumak.aem.akamai.ccu.PurgeAction defaultPurgeAction;
	@Property(name = "defaultPurgeDomain", label = "Default purge domain", description = "Can be staging, production (default)", value = "production")
	private com.xumak.aem.akamai.ccu.PurgeDomain defaultPurgeDomain;

	private AsyncHTTPBuilder httpBuilder;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.xumak.aem.akamai.ccu.PurgeResponse purgeByUrl(String url) {
		purgeByUrls([url]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.xumak.aem.akamai.ccu.PurgeResponse purgeByUrls(Collection<String> urls) {
		return purge(urls, com.xumak.aem.akamai.ccu.PurgeType.ARL, defaultPurgeAction, defaultPurgeDomain);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.xumak.aem.akamai.ccu.PurgeResponse purgeByCpCode(String cpCode) {
		return purgeByCpCodes([cpCode]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.xumak.aem.akamai.ccu.PurgeResponse purgeByCpCodes(Collection<String> cpCodes) {
		return purge(cpCodes, com.xumak.aem.akamai.ccu.PurgeType.CPCODE, defaultPurgeAction, defaultPurgeDomain);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.xumak.aem.akamai.ccu.PurgeResponse purge(Collection<String> objects, com.xumak.aem.akamai.ccu.PurgeType purgeType, com.xumak.aem.akamai.ccu.PurgeAction purgeAction, com.xumak.aem.akamai.ccu.PurgeDomain purgeDomain) {
		Collection<String> uniqueObjects = removeDuplicate(objects)
		if (!uniqueObjects) {
			LOG.warn("No objects to invalidate")
			return com.xumak.aem.akamai.ccu.PurgeResponse.noResponse();
		}

		logDebug(purgeType, purgeAction, purgeDomain, uniqueObjects)

		Future result = httpBuilder.post(
			path: "/ccu/v2/queues/default",
			requestContentType: CONTENT_TYPE,
			body: [
				type   : purgeType.name().toLowerCase(),
				action : purgeAction.name().toLowerCase(),
				domain : purgeDomain.name().toLowerCase(),
				objects: uniqueObjects,
			]) { resp, json -> return new com.xumak.aem.akamai.ccu.PurgeResponse(json) }


		def response = result.get()
		LOG.info("Response {}", response);
		return response;
	}

	private void logDebug(com.xumak.aem.akamai.ccu.PurgeType purgeType, com.xumak.aem.akamai.ccu.PurgeAction purgeAction, com.xumak.aem.akamai.ccu.PurgeDomain purgeDomain, Collection<String> uniqueObjects) {
		//if(LOG.isDebugEnabled()){
			LOG.info("Request:")
			LOG.info("Type: {}", purgeType)
			LOG.info("Action: {}", purgeAction)
			LOG.info("Domain: {}", purgeDomain)
			LOG.info("objects: {}", uniqueObjects)
		//}
	}

	/**
	 * Remove null and duplicates but keep given ordering
	 * @param objects the list of objects
	 * @return a ordered set of objects
	 */
	private Collection<String> removeDuplicate(Collection<String> objects) {
		objects.removeAll([null])
		return objects.unique()
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.xumak.aem.akamai.ccu.PurgeStatus getPurgeStatus(String progressUri) {
		if (!progressUri) {
			return com.xumak.aem.akamai.ccu.PurgeStatus.noStatus();
		}

		Future result = httpBuilder.get(
			path: progressUri,
			requestContentType: CONTENT_TYPE
		) { resp, json -> return new com.xumak.aem.akamai.ccu.PurgeStatus(json) }

		return result.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.xumak.aem.akamai.ccu.QueueStatus getQueueStatus() {
		Future result = httpBuilder.get(
			path: "/ccu/v2/queues/default",
			requestContentType: CONTENT_TYPE
		) { resp, json -> return new com.xumak.aem.akamai.ccu.QueueStatus(json) }

		return result.get();
	}

	@Activate
	protected void activate(ComponentContext context) {
		setRootCcuUrl(context.getProperties().get("rootCcuUrl"))
		setUserName(context.getProperties().get("userName"))
		setPassword(context.getProperties().get("password"))
		setDefaultPurgeAction(context.getProperties().get("defaultPurgeAction"))
		setDefaultPurgeDomain(context.getProperties().get("defaultPurgeDomain"))

		httpBuilder = new AsyncHTTPBuilder(
			timeout: TimeUnit.SECONDS.toMillis(5).toInteger(),
			poolSize: 5,
			uri: rootCcuUrl,
		)
		httpBuilder.setContentEncoding("utf-8")
		httpBuilder.auth.basic userName, password
		httpBuilder.handler.failure = { resp, json ->
			LOG.error("Error response : ${json}" )
			throw new HttpResponseException(resp)
		}
	}

	@Deactivate
	protected void deactivate() {
		httpBuilder = null
		userName = null
		password = null
		defaultPurgeAction = null
		defaultPurgeDomain = null
	}

	private void setRootCcuUrl(String rootCcuUrl) {
		if (rootCcuUrl) {
			this.rootCcuUrl = rootCcuUrl
		} else {
			this.rootCcuUrl = DEFAULT_CCU_URL;
		}
	}

	private void setUserName(String userName) {
		if (!userName) {
			throw InvalidParameterException("The username is mandatory");
		}
		this.userName = userName
	}

	private void setPassword(String password) {
		if (!password) {
			throw InvalidParameterException("The username is mandatory");
		}
		this.password = password
	}

	private void setDefaultPurgeAction(String purgeAction) {
		if (purgeAction) {
			this.defaultPurgeAction = com.xumak.aem.akamai.ccu.PurgeAction.valueOf(purgeAction.toUpperCase())
		} else {
			this.defaultPurgeAction = DEFAULT_PURGE_ACTION
		}

	}

	private void setDefaultPurgeDomain(String purgeDomain) {
		if (purgeDomain) {
			this.defaultPurgeDomain = com.xumak.aem.akamai.ccu.PurgeDomain.valueOf(purgeDomain.toUpperCase())
		} else {
			this.defaultPurgeDomain = DEFAULT_PURGE_DOMAIN;
		}
	}
}
