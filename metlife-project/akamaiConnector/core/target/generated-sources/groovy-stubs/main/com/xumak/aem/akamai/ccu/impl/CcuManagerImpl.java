package com.xumak.aem.akamai.ccu.impl;

import org.apache.felix.scr.annotations.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@org.apache.felix.scr.annotations.Component(immediate=true, description="Manage calls to the Akamai CCU REST API", metatype=true, label="Akamai CCU REST API Manager", policy=org.apache.felix.scr.annotations.ConfigurationPolicy.REQUIRE) @org.apache.felix.scr.annotations.Service(value={com.xumak.aem.akamai.ccu.CcuManager.class}) public class CcuManagerImpl
  extends java.lang.Object  implements
    com.xumak.aem.akamai.ccu.CcuManager,    groovy.lang.GroovyObject {
public static final com.xumak.aem.akamai.ccu.PurgeAction DEFAULT_PURGE_ACTION = null;
public static final com.xumak.aem.akamai.ccu.PurgeDomain DEFAULT_PURGE_DOMAIN = null;
public static final java.lang.String CONTENT_TYPE = "application/json";
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
@java.lang.Override() public  com.xumak.aem.akamai.ccu.PurgeResponse purgeByUrl(java.lang.String url) { return (com.xumak.aem.akamai.ccu.PurgeResponse)null;}
@java.lang.Override() public  com.xumak.aem.akamai.ccu.PurgeResponse purgeByUrls(java.util.Collection<java.lang.String> urls) { return (com.xumak.aem.akamai.ccu.PurgeResponse)null;}
@java.lang.Override() public  com.xumak.aem.akamai.ccu.PurgeResponse purgeByCpCode(java.lang.String cpCode) { return (com.xumak.aem.akamai.ccu.PurgeResponse)null;}
@java.lang.Override() public  com.xumak.aem.akamai.ccu.PurgeResponse purgeByCpCodes(java.util.Collection<java.lang.String> cpCodes) { return (com.xumak.aem.akamai.ccu.PurgeResponse)null;}
@java.lang.Override() public  com.xumak.aem.akamai.ccu.PurgeResponse purge(java.util.Collection<java.lang.String> objects, com.xumak.aem.akamai.ccu.PurgeType purgeType, com.xumak.aem.akamai.ccu.PurgeAction purgeAction, com.xumak.aem.akamai.ccu.PurgeDomain purgeDomain) { return (com.xumak.aem.akamai.ccu.PurgeResponse)null;}
@java.lang.Override() public  com.xumak.aem.akamai.ccu.PurgeStatus getPurgeStatus(java.lang.String progressUri) { return (com.xumak.aem.akamai.ccu.PurgeStatus)null;}
@java.lang.Override() public  com.xumak.aem.akamai.ccu.QueueStatus getQueueStatus() { return (com.xumak.aem.akamai.ccu.QueueStatus)null;}
@org.apache.felix.scr.annotations.Activate() protected  void activate(org.osgi.service.component.ComponentContext context) { }
@org.apache.felix.scr.annotations.Deactivate() protected  void deactivate() { }
}
