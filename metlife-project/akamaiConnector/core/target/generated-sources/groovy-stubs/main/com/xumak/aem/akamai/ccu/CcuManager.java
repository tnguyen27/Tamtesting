package com.xumak.aem.akamai.ccu;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

public interface CcuManager
 {
 com.xumak.aem.akamai.ccu.PurgeResponse purgeByUrl(java.lang.String url);
 com.xumak.aem.akamai.ccu.PurgeResponse purgeByUrls(java.util.Collection<java.lang.String> urls);
 com.xumak.aem.akamai.ccu.PurgeResponse purgeByCpCode(java.lang.String cpCode);
 com.xumak.aem.akamai.ccu.PurgeResponse purgeByCpCodes(java.util.Collection<java.lang.String> cpCodes);
 com.xumak.aem.akamai.ccu.PurgeResponse purge(java.util.Collection<java.lang.String> objets, com.xumak.aem.akamai.ccu.PurgeType purgeType, com.xumak.aem.akamai.ccu.PurgeAction purgeAction, com.xumak.aem.akamai.ccu.PurgeDomain purgeDomain);
 com.xumak.aem.akamai.ccu.PurgeStatus getPurgeStatus(java.lang.String progressUri);
 com.xumak.aem.akamai.ccu.QueueStatus getQueueStatus();
}
