package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.info.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-6.
 */
public class GlobleUtil {
    private static GlobleUtil INSTANCE;
    private GlobleUtil(){};
    public Map<String, Controller> controllers = new ConcurrentHashMap<String, Controller>();

    public boolean reflashGlobleInfo(){

        //Traversal controllers, GET all info to reflash
        
        return true;
    }

    public static GlobleUtil getInstance(){
        if(INSTANCE == null) INSTANCE = new GlobleUtil();
        return INSTANCE;
    }
}
