package org.apache.servicemix.wsn.router.router;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-5.
 */
public class Switch {
    public String id;
    private Map<String, WSNHost> wsnHostMap = new ConcurrentHashMap<String, WSNHost>();
}
