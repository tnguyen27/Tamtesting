package com.xumak.aem.akamai.ccu;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.transform.ToString() public class PurgeResponse
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
public int httpStatus;
public java.lang.String detail;
public long estimatedSeconds;
public java.lang.String progressUri;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.String getPurgeId() { return (java.lang.String)null;}
public  void setPurgeId(java.lang.String value) { }
public  long getPingAfterSeconds() { return (long)0;}
public  void setPingAfterSeconds(long value) { }
public  java.lang.String getSupportId() { return (java.lang.String)null;}
public  void setSupportId(java.lang.String value) { }
public static  com.xumak.aem.akamai.ccu.PurgeResponse noResponse() { return (com.xumak.aem.akamai.ccu.PurgeResponse)null;}
public  boolean isSuccess() { return false;}
}
