package com.xumak.aem.akamai.ccu.impl;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@org.apache.felix.scr.annotations.Component(immediate=true, description="Job that execute Akamai flush using Akamai CCU manager", metatype=true, label="Akamai Flush Job") @org.apache.felix.scr.annotations.Service(value={com.xumak.aem.akamai.ccu.impl.FlushAkamaiItemsJob.class,org.apache.sling.event.jobs.consumer.JobConsumer.class}) @org.apache.felix.scr.annotations.Properties(value={@org.apache.felix.scr.annotations.Property(name=org.apache.sling.event.jobs.consumer.JobConsumer.PROPERTY_TOPICS, value="com/xumak/aem/akamai/ccu/impl/FlushAkamaiItemsJob") ,@org.apache.felix.scr.annotations.Property(name="mapping", description="Using the format <domain>|<path>, list all the mapping between domain (e.g. http://www.latam.com) and the path from AEM (e.g. /content/latam). So all content that starts with this path is going to be appended to the corresponding domain.", label="Mapping", value="", cardinality=java.lang.Integer.MAX_VALUE) ,@org.apache.felix.scr.annotations.Property(name="extensions", description="This values will be used to determine if the path needs a .html at the end, for example .jpg and .png, so if the path to purge doesn't ends with .jpg neither .png it will add .html at the end.", label="Extensions", value="", cardinality=java.lang.Integer.MAX_VALUE) ,@org.apache.felix.scr.annotations.Property(name="extensionsToRefresh", description="This values will be use to refresh every configured path in Akamai for the same page when it is activated.", label="Extensions to refresh", value="", cardinality=java.lang.Integer.MAX_VALUE) ,@org.apache.felix.scr.annotations.Property(name="agentsPath", description="This services will look for the dispatchers URLs on this path.", label="Dispatcher Agents Path", value="/etc/replication/agents.author") ,@org.apache.felix.scr.annotations.Property(name="childLimit", description="Sets the limit for child pages to handle.", label="Child Limit", value="100") ,@org.apache.felix.scr.annotations.Property(name="waitTime", description="Wait time in ms before purging Akamai Cache. E.g. 1000", label="Wait Time", value="1000") ,@org.apache.felix.scr.annotations.Property(name="templatePathsChildren", description="List all template paths that have configurations structure nodes", label="Template Paths Children Nodes", value="", cardinality=java.lang.Integer.MAX_VALUE) ,@org.apache.felix.scr.annotations.Property(name="rewritePaths", description="Should have the same values as the URL Mappings in Resource Resolver Factory in the Publish Enviroments", label="Rewrite Paths", value="", cardinality=java.lang.Integer.MAX_VALUE) }) public class FlushAkamaiItemsJob
  extends java.lang.Object  implements
    org.apache.sling.event.jobs.consumer.JobConsumer,    groovy.lang.GroovyObject {
public static final java.lang.String JOB_TOPIC = "com/xumak/aem/akamai/ccu/impl/FlushAkamaiItemsJob";
public static final java.lang.String PATHS = "paths";
public static final java.lang.String MAPPING_PROPERTY = "mapping";
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
@java.lang.Override() public  org.apache.sling.event.jobs.consumer.JobConsumer.JobResult process(org.apache.sling.event.jobs.Job job) { return (org.apache.sling.event.jobs.consumer.JobConsumer.JobResult)null;}
public  boolean isTemplatePathInConfTemplates(java.lang.String templatePath) { return false;}
public  java.util.List<java.lang.String> getPathsForTemplatePathInConfTemplates(java.lang.String templatePath) { return (java.util.List<java.lang.String>)null;}
public  java.util.List<java.lang.String> getFullListPathsForTemplatePathInConfTemplates(java.lang.String templatePath, java.lang.String path, boolean addExtension, boolean addJcrContent, java.lang.String extension) { return (java.util.List<java.lang.String>)null;}
public  boolean isRootUrlInConf(java.lang.String rootUrl) { return false;}
public  java.util.List<java.lang.String> getAllURLCombinations(java.lang.String url) { return (java.util.List<java.lang.String>)null;}
public  java.util.List<java.lang.String> getPathFromMapping(java.lang.String rootUrl) { return (java.util.List<java.lang.String>)null;}
public  java.util.Hashtable<java.lang.String, java.util.List<java.lang.String>> getMappedDomains() { return (java.util.Hashtable<java.lang.String, java.util.List<java.lang.String>>)null;}
public  java.util.List<java.lang.String> getRootUrl(java.lang.String paramPath) { return (java.util.List<java.lang.String>)null;}
public  boolean isExtensionInConf(java.lang.String path) { return false;}
public  java.util.Set<java.lang.String> prependPathWithRootUrl(java.util.Collection<java.lang.String> paths, java.lang.String rootSiteUrl) { return (java.util.Set<java.lang.String>)null;}
public static  org.apache.sling.event.jobs.consumer.JobConsumer.JobResult convertToJobResult(com.xumak.aem.akamai.ccu.PurgeResponse response) { return (org.apache.sling.event.jobs.consumer.JobConsumer.JobResult)null;}
public  void activate(org.osgi.service.component.ComponentContext context) { }
public  java.util.List<java.lang.Object> mappingToList() { return (java.util.List<java.lang.Object>)null;}
public  java.util.List<java.lang.Object> dispatchersToList() { return (java.util.List<java.lang.Object>)null;}
public  int getChildLimit() { return (int)0;}
public  java.util.List<java.lang.String> addValueToListForMap(java.util.Map map, java.lang.String key, java.lang.String value) { return (java.util.List<java.lang.String>)null;}
public  java.lang.String rewriteInternalLink(java.lang.String link) { return (java.lang.String)null;}
}
