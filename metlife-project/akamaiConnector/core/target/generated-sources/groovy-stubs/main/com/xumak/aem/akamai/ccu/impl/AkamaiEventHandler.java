package com.xumak.aem.akamai.ccu.impl;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@org.apache.felix.scr.annotations.Component(immediate=true, description="Listen to repository replication notification to invalidate Akamai cache when it is needed", metatype=true, label="Akamai Event Handler") @org.apache.felix.scr.annotations.Service(value={com.xumak.aem.akamai.ccu.impl.AkamaiEventHandler.class,org.osgi.service.event.EventHandler.class}) @org.apache.felix.scr.annotations.Properties(value={@org.apache.felix.scr.annotations.Property(name=org.osgi.service.event.EventConstants.EVENT_TOPIC, label="Event topic", value=com.day.cq.replication.ReplicationAction.EVENT_TOPIC) }) public class AkamaiEventHandler
  extends java.lang.Object  implements
    org.osgi.service.event.EventHandler,    groovy.lang.GroovyObject {
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
@java.lang.Override() public  void handleEvent(org.osgi.service.event.Event event) { }
@java.lang.SuppressWarnings(value="GroovyUnusedDeclaration") protected  void activate(org.osgi.service.component.ComponentContext context) { }
@java.lang.SuppressWarnings(value="GroovyUnusedDeclaration") protected  void deactivate() { }
}
