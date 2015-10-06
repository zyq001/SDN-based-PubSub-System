package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.info.Controller;
import edu.bupt.wangfu.sdn.info.Flow;

import java.util.List;
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

    public boolean downFlow(Controller controller, List<Flow> flows){
        
        for(Flow flow: flows)
        return true;
    }

    public boolean downFlow(Controller controller, Flow){

        return true;
    }

    public static GlobleUtil getInstance(){
        if(INSTANCE == null) INSTANCE = new GlobleUtil();
        return INSTANCE;
    }
}
