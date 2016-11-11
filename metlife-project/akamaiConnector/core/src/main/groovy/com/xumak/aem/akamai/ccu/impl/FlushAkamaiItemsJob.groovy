package com.xumak.aem.akamai.ccu.impl

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.commons.osgi.PropertiesUtil
import org.apache.sling.event.jobs.Job
import org.apache.sling.event.jobs.consumer.JobConsumer
import org.osgi.service.component.ComponentContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Pattern

//Sling Imports
/**
 * FlushAkamaiItemsJob -
 *
 * @author Sebastien Bernard
 */
@Component(label = "Akamai Flush Job", description = "Job that execute Akamai flush using Akamai CCU manager", metatype = true, immediate = true)
@Service(value = [FlushAkamaiItemsJob.class, JobConsumer.class])
@org.apache.felix.scr.annotations.Properties(value = [
		@Property(name = JobConsumer.PROPERTY_TOPICS, value = FlushAkamaiItemsJob.JOB_TOPIC),
		@Property(name = "mapping", value = "", cardinality = Integer.MAX_VALUE, label = "Mapping", description = "Using the format <domain>|<path>, list all the mapping between domain (e.g. http://www.latam.com) and the path from AEM (e.g. /content/latam). So all content that starts with this path is going to be appended to the corresponding domain."),
		@Property(name = "extensions", value = "", cardinality = Integer.MAX_VALUE, label = "Extensions", description = "This values will be used to determine if the path needs a .html at the end, for example .jpg and .png, so if the path to purge doesn't ends with .jpg neither .png it will add .html at the end."),
		@Property(name = "extensionsToRefresh", value = "", cardinality = Integer.MAX_VALUE, label = "Extensions to refresh", description = "This values will be use to refresh every configured path in Akamai for the same page when it is activated."),
		@Property(name = "agentsPath", value = "/etc/replication/agents.author", label = "Dispatcher Agents Path", description = "This services will look for the dispatchers URLs on this path."),
		@Property(name = "childLimit", value = "100", label = "Child Limit", description = "Sets the limit for child pages to handle."),
		@Property(name = "waitTime", value = "1000", label = "Wait Time", description = "Wait time in ms before purging Akamai Cache. E.g. 1000"),
		@Property(name = "templatePathsChildren", value = "", cardinality = Integer.MAX_VALUE, label = "Template Paths Children Nodes", description = "List all template paths that have configurations structure nodes"),
		@Property(name = "rewritePaths", value = "", cardinality = Integer.MAX_VALUE, label = "Rewrite Paths", description = "Should have the same values as the URL Mappings in Resource Resolver Factory in the Publish Enviroments")
])
class FlushAkamaiItemsJob implements JobConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(FlushAkamaiItemsJob.class);
	public static final String JOB_TOPIC = "com/xumak/aem/akamai/ccu/impl/FlushAkamaiItemsJob";
	public static final String PATHS = "paths";

    public static final String MAPPING_PROPERTY = "mapping";
    private static final String EXTENSIONS_PROPERTY = "extensions";
	private static final String EXTENSIONS_TO_REFRESH = "extensionsToRefresh";
    private static final String AGENTS_PATH_PROPERTY = "agentsPath";
    private static final String CHILD_LIMIT_PROPERTY = "childLimit";
    private static final String WAIT_TIME_PROPERTY = "waitTime";
	private static final String TEMPLATE_CHILDREN_PATHS_PROPERTY = "templatePathsChildren";

    private static final String SERIALIZATION_TYPE_NODE_PROPERTY = "serializationType";
    private static final String TRANSPORT_URI_NODE_PROPERTY = "transportUri";
    private static final String ENABLED_NODE_PROPERTY = "enabled";

    private static final String AGENTS_PATH_DEFAULT_VALUE = "/etc/replication/agents.author";
    private static final String CHILD_LIMIT_DEFAULT_VALUE = "100";
    private static final String WAIT_TIME_DEFAULT_VALUE = "1000";

	private static final String REWRITRE_PATHS = "rewritePaths";

	@org.apache.felix.scr.annotations.Reference
	private com.xumak.aem.akamai.ccu.CcuManager ccuManager;

	@org.apache.felix.scr.annotations.Reference
	private ResourceResolverFactory resolverFactory;

    private LinkedList<String> dispatchers = new LinkedList<String>();

	private Hashtable<String,List<String>> mapping = new Hashtable<String, List<String>>();
	private Hashtable<String,List<String>> mappingInverse = new Hashtable<String, List<String>>();
	private String[] extensions;
	private String[] extensionsToRefresh;
	private String agentsPath;
	private int waitTime = 0;
	private int childLimit = 0;
	private String[] templatePaths;
	private Map<String,List<String>> templatePathsMap = new HashMap<String,List<String>>();
	private Map<String,String> rewriteMap = new HashMap<>();

	private static final String SLASH_SEPARATOR = "/";
	private static final String JCR_CONTENT = "jcr:content";
	private static final String CQ_TEMPLATE = "cq:template"
	private static final String VALUE_MAP_PROPERTY = "valueMap";
	private static final String HTML_EXTENSION = ".html";

	@Override
	public JobConsumer.JobResult process(Job job) {
		LOG.debug("++++++++++++++++ start at public JobConsumer.JobResult process(Job job)");

		Set<String> pathsToPurge = getPathsToPurge(job);
		LOG.debug("Start processing job to purge Akamai cache");
		if (pathsToPurge.isEmpty()) {
			LOG.warn("No path to process, canceling...");
			return JobConsumer.JobResult.CANCEL;
		}

        com.xumak.aem.akamai.ccu.PurgeResponse response;

		// add structure template nodes BEGIN
		ResourceResolver resourceResolverT = null;
		try {
			resourceResolverT = resolverFactory.getAdministrativeResourceResolver(null);
			List<String> values = new LinkedList<>();
			for( String pathToPurge : pathsToPurge ) {
				Resource r = resourceResolverT.getResource(pathToPurge);
				if (r != null){
					Resource child = r.getChild(JCR_CONTENT);
					if (child != null) {
						Map<String, String> propertiesMap = child.getProperties();
						Map<String, Object> jcrPropertiesMap = propertiesMap.get( VALUE_MAP_PROPERTY );

						if (jcrPropertiesMap.containsKey(CQ_TEMPLATE)) {
							String templateProp = (String)jcrPropertiesMap.get(CQ_TEMPLATE);
							if (isTemplatePathInConfTemplates(templateProp)) {
								List<String> nodesPath = getFullListPathsForTemplatePathInConfTemplates(templateProp, child.getPath(), true,false, "");
								for (String nodePath : nodesPath) {
									values.add(nodePath);
								}
							}
						}

					}
				}
			}
			for (String val : values){
				pathsToPurge.add(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( null != resourceResolverT ){
				try {
					resourceResolverT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// add structure template nodes END
		for( String pathToPurge : pathsToPurge ) {

            boolean urlInConf = false;
            boolean urlInAEM = false;

            // PRE Checking that is a valid URL within configuration

            Enumeration<String> mappingKeys = mapping.keys();

            for( String key : mappingKeys ) {
                if( pathToPurge.startsWith( key ) ) {
                    urlInConf = true;
                }
            }

            // End PRE checking valid URL within configuration


			List<String> rootSiteUrl = getRootUrl( pathToPurge );
			String pathToPurgeMapped = null;
			ResourceResolver resourceResolver = null;
			try {
				resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
				pathToPurgeMapped = resourceResolver.map( pathToPurge );

                urlInAEM = true;

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if( null != resourceResolver ){
					try {
						resourceResolver.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			Set<String> pathSet = new HashSet<String>();
			if( null != pathToPurgeMapped ) {
				pathSet.add( pathToPurgeMapped );
			} else {
				pathSet.add( pathToPurge );
			}

            // if pre check and before before 2 too ... BOOM continue if doesn't match

            if( urlInAEM ) {
                if( ! urlInConf ) {
                    continue; // This means that the URL exists in AEM but is not part of the conf.
                }
            }

            // Waiting

            LOG.info("++++++++++++++++ Starting Thread.sleep( " +  waitTime + " ) " );
            Thread.sleep( waitTime );
            LOG.info("++++++++++++++++ Done Thread.sleep( " +  waitTime + " ) " );

            // End Waiting
			List<Set<String>> absoluteUrls = new LinkedList<>();

			if (rootSiteUrl != null && rootSiteUrl.size()>0){
				for ( String rootSiteUrlValue : rootSiteUrl){
					absoluteUrls.add(prependPathWithRootUrl(pathSet, rootSiteUrlValue));
				}
			}else{
				absoluteUrls.add( prependPathWithRootUrl(pathSet, rootSiteUrl) );
			}

			LOG.debug("Specific Path => "+ absoluteUrls);

			for ( Set<String> absoluteUrlsValue : absoluteUrls) {
				logUrls(absoluteUrlsValue);

				for (String s : absoluteUrlsValue) {
					// Do the REWRITE to emulate the Resolver Factory URL Mapping
					s = this.rewriteInternalLink(s);

					LOG.info("Purge Akamai cache for " + s);
					Set<String> set = new HashSet<String>(Arrays.asList(s));
					try {
						response = ccuManager.purgeByUrls(set);
					} catch (Exception e) {
						LOG.error("Forbidden URL = " + s);
					}
				}
			}
		}

		LOG.debug("---------------- end at public JobConsumer.JobResult process(Job job)");
		return convertToJobResult(response);
	}


	public boolean isTemplatePathInConfTemplates(String templatePath){
		if (templatePathsMap.containsKey(templatePath)){
			return true;
		}else{
			return false;
		}
	}

	public List<String> getPathsForTemplatePathInConfTemplates(String templatePath){
		return templatePathsMap.get(templatePath);
	}

	public List<String> getFullListPathsForTemplatePathInConfTemplates(String templatePath, String path,
																	   boolean addExtension, boolean addJcrContent, String extension){
		List<String> templatesValues = templatePathsMap.get(templatePath);
		List<String> newValues = new LinkedList<>();
		for (String value : templatesValues){
			String newPath;
			if (addJcrContent){
				newPath = path + SLASH_SEPARATOR + JCR_CONTENT + SLASH_SEPARATOR + value;
			}else{
				newPath = path + SLASH_SEPARATOR + value;
			}
            /** MODIFIED FOR HOTFIX US **/
			if (addExtension){
				newPath += extension;
			}
            /** END MODIFIED FOR HOTFIX US **/
			newValues.add(newPath);
		}
		return newValues;
	}


	public boolean isRootUrlInConf( String rootUrl ) {
		return mappingInverse.containsKey( rootUrl );
	}

	/**
	 * This function will calculate all the possible URLs based on the configured prefix
	 *
	 * @param url resource URL
	 * @return List of URL combinations
	 * */
	public List<String> getAllURLCombinations( final String url ){

		final List<String> urlValues = new LinkedList<>();
		String suffix = "";

		if (url.endsWith(SLASH_SEPARATOR)) {
			// url ends with a slash symbol
			suffix = SLASH_SEPARATOR
		} else {
			String urlLastPart = url.substring(url.lastIndexOf(SLASH_SEPARATOR));
			if (urlLastPart.contains(".")) {
				// url has and extension point
				suffix = urlLastPart.substring(urlLastPart.lastIndexOf("."));
			} else {
				// url has no suffix
				suffix = "";
			}
		}

		String urlBaseParth = "";
		if ("".equals(suffix)) {
			urlBaseParth = url;
		} else if(SLASH_SEPARATOR.equals(suffix)){
			urlBaseParth = url.substring(0, url.lastIndexOf(SLASH_SEPARATOR));
		} else {
			urlBaseParth = url.substring(0, url.lastIndexOf("."));
		}

		for (String extension : extensionsToRefresh) {
			LOG.debug("+++++++++++++Configured '$extension' as URL to be refreshed")
			urlValues.add(urlBaseParth + extension)
		}

		return urlValues;
	}

	public List<String> getPathFromMapping( String rootUrl ) {
		List<String> list = mappingInverse.get( rootUrl ); //fix
		return list;
	}

	public Hashtable<String, List<String>> getMappedDomains() {
		return mapping;
	}
	public List<String> getRootUrl( String paramPath ) {

		Enumeration<String> paths = mapping.keys();
		String key = null;
		while(paths.hasMoreElements()){
			String path = (String) paths.nextElement();
			if (paramPath.startsWith(path)) {
				key = path;
				break;
			}
		}

		if( null != key ) {
			List<String> list = mapping.get(key); //fix
			return list;
		}

		return null;
	}

	public boolean isExtensionInConf( String path ) {
		for( String extension : extensions ) {
			LOG.debug("++++++++++++++++ testing extension = '$extension'")
			if( path.endsWith( extension ) ) {
				LOG.debug( "++++++++++++++++ Extension '$extension' in configuation")
				return true
			}
		}

		return false;
	}

	private logUrls(Set<String> pathsToPurge) {
		LOG.debug("++++++++++++++++ start at private logUrls(Set<String> pathsToPurge)")
		LOG.debug("Path(s) to purge:")
		for (path in pathsToPurge) {
			LOG.debug(path)
		}
		LOG.debug("---------------- end at private logUrls(Set<String> pathsToPurge)")
	}

	public Set<String> prependPathWithRootUrl(Collection<String> paths, String rootSiteUrl) {
		LOG.debug("++++++++++++++++ start at private Set<String> prependPathWithRootUrl(Collection<String> paths)")
		LOG.debug("rootSiteUrl: $rootSiteUrl")
		if (!rootSiteUrl) {
			return paths;
		}

		Set<String> urls = new LinkedHashSet<String>(paths.size())
		for (path in paths) {
			if (!path.startsWith(rootSiteUrl)) {
				String pathWithRootSiteUrl = rootSiteUrl.concat(path)

				LOG.debug("++++++++++++++++ pathWithRootSiteUrl = "+pathWithRootSiteUrl)
				// hotfix US
				if( !isExtensionInConf( pathWithRootSiteUrl ) ){
					urls.addAll(getAllURLCombinations(pathWithRootSiteUrl));
				} else {
					urls.add( pathWithRootSiteUrl );
				}
				LOG.debug("++++++++++++++++ pathWithRootSiteUrl = "+pathWithRootSiteUrl)

			}
		}

		LOG.debug("---------------- end at private Set<String> prependPathWithRootUrl(Collection<String> paths)")
		return urls
	}

	private static Set getPathsToPurge(Job job) {
		LOG.debug("++++++++++++++++ start at private static Set getPathsToPurge(Job job)")
		String[] pathsToInvalidate = PropertiesUtil.toStringArray(job.getProperty(PATHS));
		if (pathsToInvalidate == null) {
			LOG.error("Not a valid path to execute the job")
			return Collections.emptySet()
		}
		Set results = new HashSet()
		results.addAll(pathsToInvalidate)

		LOG.debug("---------------- end at private static Set getPathsToPurge(Job job)")
		return results;
	}

	static JobConsumer.JobResult convertToJobResult(com.xumak.aem.akamai.ccu.PurgeResponse response) {
		LOG.debug("++++++++++++++++ start/end at static JobConsumer.JobResult convertToJobResult(PurgeResponse response)")

        if( response == null ) return JobConsumer.JobResult.OK;

		return response.isSuccess() ? JobConsumer.JobResult.OK : JobConsumer.JobResult.FAILED;
	}

	public void activate(ComponentContext context) {
		LOG.info("++++++++++++++++ start at public void activate(ComponentContext context)")
		String[] mappingArray = PropertiesUtil.toStringArray(context.getProperties().get( MAPPING_PROPERTY ));
		extensions = PropertiesUtil.toStringArray(context.getProperties().get( EXTENSIONS_PROPERTY ));
		extensionsToRefresh = PropertiesUtil.toStringArray(context.getProperties().get( EXTENSIONS_TO_REFRESH ));

		String waitTimeStr = PropertiesUtil.toString(context.getProperties().get( WAIT_TIME_PROPERTY ), WAIT_TIME_DEFAULT_VALUE);
		try {
			waitTime = Integer.parseInt(waitTimeStr);
		} catch (NumberFormatException e) {
			waitTime = 1000;
		}

        String childTimeStr = PropertiesUtil.toString(context.getProperties().get( CHILD_LIMIT_PROPERTY ), CHILD_LIMIT_DEFAULT_VALUE);
        try {
            childLimit = Integer.parseInt(childTimeStr);
        } catch (NumberFormatException e) {
            childLimit = 100;
        }

		for( String mappingItem : mappingArray ) {
			LOG.info("++++++++++++++++ mappingItem = "+mappingItem);
			if( mappingItem.indexOf("|") > -1 ) {
				String[] mappingItemSplit = mappingItem.split( Pattern.quote("|") );
				if( mappingItemSplit.length >= 2 ) {
					// Added more context to the String extraction we are getting Domain and Path
					String confDomainValue = mappingItemSplit[0];
					String confPathValue = mappingItemSplit[1];

					// Map that will hold a list of domains for every path
					mapping.put( confPathValue, this.addValueToListForMap(mapping, confPathValue, confDomainValue) );
					// Map that will hold a list of paths for every domain
					mappingInverse.put( confDomainValue, this.addValueToListForMap(mappingInverse, confDomainValue, confPathValue) );

					LOG.info("++++++++++++++++ mapping.put( \""+confDomainValue+"\", \""+confPathValue+"\" )");
				}
			}
		}

		// Rewrite Paths
		String[] mappingRewritesArray = PropertiesUtil.toStringArray(context.getProperties().get( REWRITRE_PATHS ));
		for( String mappingRewriteItem : mappingRewritesArray ) {
			LOG.info("++++++++++++++++ mappingRewriteItem = "+mappingRewriteItem);
			if( mappingRewriteItem.indexOf(":") > -1 ) {
				String[] mappingItemSplit = mappingRewriteItem.split( Pattern.quote(":") );
				if( mappingItemSplit.length >= 2 ) {
					// Added more context to the String extraction we are getting Domain and Path
					String prefixRewrite = mappingItemSplit[0];
					String newPrefixRewrite = mappingItemSplit[1];

					rewriteMap.put( prefixRewrite, newPrefixRewrite );

					LOG.info("++++++++++++++++ RewriterMap.put( \""+prefixRewrite+"\", \""+newPrefixRewrite+"\" )");
				}
			}
		}

		agentsPath = PropertiesUtil.toString(context.getProperties().get( AGENTS_PATH_PROPERTY ), AGENTS_PATH_DEFAULT_VALUE);
		templatePaths = PropertiesUtil.toStringArray(context.getProperties().get( TEMPLATE_CHILDREN_PATHS_PROPERTY ));
        ResourceResolver resourceResolver = null;

        try {
            resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
            Resource agentsResource = resourceResolver.getResource( agentsPath );

			// add all template configuration nodes to the Map
			if (templatePaths != null && templatePaths.size()>0) {
				for (String templatePath : templatePaths){
					Resource templatePathResource = resourceResolver.getResource( templatePath + SLASH_SEPARATOR + JCR_CONTENT);
					if (templatePathResource != null){
						Iterator<Resource> iteratorT = templatePathResource.listChildren();
						if (iteratorT.hasNext()){
							List<String> templateList = new LinkedList<>();
							while (iteratorT.hasNext()){
								Resource nodeStructure = iteratorT.next();
								List<String> childrenList = new LinkedList<>();
								childrenList.add(nodeStructure.getName());
								List<String> list = getChildrenNodeForTemplates(childrenList, nodeStructure, nodeStructure.getName());
								for (String s : list){
									templateList.add( s );
								}
							}
							LOG.info("++++++++++++++++ templatePathsMap.put( \""+templatePath+"\", \""+templateList+"\" )");
							templatePathsMap.put(templatePath, templateList);
						}
					}else{
						LOG.debug("Resource is NULL: "+templatePath + SLASH_SEPARATOR + JCR_CONTENT);
					}
				}
			}

		    getChildResources( resourceResolver, agentsResource, dispatchers );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if( null != resourceResolver ){
                try {
                    resourceResolver.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

		LOG.debug("---------------- end at public void activate(ComponentContext context)")
	}

    public List<Object> mappingToList() {
        List<Object> mappingList = new ArrayList<>();

        Enumeration<String> mappingKeys = mapping.keys();

        for( String key : mappingKeys ) {
			List<String> list = mapping.get(key);
			String value = list.get(0); // FIX
            mappingList.add( value + " <=> " + key );
        }

        return mappingList;
    }

    public List<Object> dispatchersToList() {
        return dispatchers;
    }

    public int getChildLimit() {
        return childLimit;
    }

    private void getChildResources(final ResourceResolver resolver, final Resource resource, LinkedList<String> dispatchers) {
        Iterator<Resource> iter = resolver.listChildren(resource);
        while (iter.hasNext()) {
            Resource childResource = iter.next();
            LOG.debug("resource  " + childResource.getName() + " - " + childResource.getResourceType());
            if ( "cq/replication/components/agent".equals( childResource.getResourceType() ) ) {

                Map<String, String> propertiesMap = childResource.getProperties();
                Map<String, Object> jcrPropertiesMap = propertiesMap.get( "valueMap" );

                if( jcrPropertiesMap.containsKey( SERIALIZATION_TYPE_NODE_PROPERTY ) &&
                        jcrPropertiesMap.containsKey( TRANSPORT_URI_NODE_PROPERTY ) &&
                        jcrPropertiesMap.containsKey( ENABLED_NODE_PROPERTY ) ) {

                    String serializationType = (String)jcrPropertiesMap.get( SERIALIZATION_TYPE_NODE_PROPERTY );
                    String transportUri = (String)jcrPropertiesMap.get( TRANSPORT_URI_NODE_PROPERTY );

                    if( null != serializationType && null != transportUri ) {

                        if( "flush".equals( serializationType ) && transportUri.endsWith( "/dispatcher/invalidate.cache" ) ) {
                            LOG.info("adding dispatcher with Transport URI  " + transportUri);
                            dispatchers.add( transportUri );
                        }

                    }

                }
            }

            getChildResources( resolver, childResource, dispatchers );
        }
    }

	public List<String> addValueToListForMap(Map map, String key, String value){
		List<String> list = new ArrayList<String>();
		if (map.containsKey(key)){
			list = map.get(key);
		}
		if (list.size()>0){
			for (String text : list){
				if (text.equals(value)){
					return list;
				}
			}
			list.add(value);
		}else{
			list.add(value);
		}
		return list;
	}

	private List<String> getChildrenNodeForTemplates(List<String> pathList, Resource resource, String rootPath){
		Iterator<Resource> iterator = resource.listChildren();
		if (iterator.hasNext()){
			while (iterator.hasNext()) {
				Resource childResource = iterator.next();
				String newRootPath = rootPath +"/"+ childResource.getName()
				pathList.add(newRootPath);

				Iterator<Resource> iteratorChild = childResource.listChildren();
				if (iteratorChild != null && iteratorChild.hasNext()){
					pathList = getChildrenNodeForTemplates(pathList, childResource, newRootPath);
				}
			}

		}
		return pathList;
	}

	public String rewriteInternalLink(String link){
		String newLink = link;
		for (String key : rewriteMap.keySet() ){
			if (link.contains(link)){
				newLink = link.replace(key, rewriteMap.get(key));
			}
		}
		return newLink;
	}
}
