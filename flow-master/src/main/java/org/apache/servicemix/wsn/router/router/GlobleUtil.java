package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.floodlight.RestProcess;
import edu.bupt.wangfu.sdn.info.Controller;
import edu.bupt.wangfu.sdn.info.DevInfo;
import edu.bupt.wangfu.sdn.info.Flow;
import edu.bupt.wangfu.sdn.info.Switch;
import edu.bupt.wangfu.sdn.queue.QueueManagerment;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-6.
 */
public class GlobleUtil {
    private static GlobleUtil INSTANCE;
    public Map<String, Controller> controllers = new ConcurrentHashMap<String, Controller>();
    public static List<Flow> initFlows = new ArrayList<Flow>();

    private static Timer timer = new Timer();

    private GlobleUtil(){
        //init static initFlows{queueFlow, topics}
        Flow flow = new Flow("queue");
        initFlows.add(flow);

        // start timer to recaclateRoute
        timer.schedule(new GlobalTimerTask(), 2000, 5 * 60 * 1000);
    };

    public void init(){
        //get realtime global info
        reflashGlobleInfo();

        //init all switchs
        for(Map.Entry<String, Controller> entry: controllers.entrySet()){
            Controller controller = entry.getValue();
            initSwitchs(controller);
        }


    }
    public boolean initSwitchs(Controller controller){
        boolean success = false;

        //down init flows
        downFlow(controller, initFlows);

        return success;
    }
    public boolean reflashGlobleInfo(){

        //Traversal controllers, GET global realtime status
        for(Map.Entry<String, Controller> entry: controllers.entrySet()){
            Controller controller = entry.getValue();
            if(!controller.isAlive()){
                controllers.remove(entry.getKey());
                continue;
            }
            controller.reflashSwitchMap();
        }
        return true;
    }

    public static Map<String, Switch> getRealtimeSwitchs(Controller controller){

        Map<String, Switch> switches = new HashMap<String, Switch>();


        return switches;
    }

    public synchronized void addController(String controllerAddr){

        Controller newController = new Controller(controllerAddr);

        newController.reflashSwitchMap();

        controllers.put(controllerAddr, newController);

    }

    public static boolean downFlow(String url, JSONObject content){
        boolean success = false;
        return RestProcess.doClientPost(url, content).get(0).equals("200");

    }

    public static boolean downFlow(Controller controller, List<Flow> flows){
        boolean success = false;
        for(Flow flow: flows){
            if(downFlow(controller, flow)) success = true;
        }
        return true;
    }

    public static boolean downFlow(Controller controller, Flow flow){
        boolean success = false;
        return RestProcess.doClientPost(controller.url, flow.getContent()).get(0).equals("200");

    }

    public static GlobleUtil getInstance(){
        if(INSTANCE == null) INSTANCE = new GlobleUtil();
        return INSTANCE;
    }

    class GlobalTimerTask extends TimerTask{

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {

            //whether to adjust queue
            QueueManagerment.qosStart();

            //whether to adjust route

        }
    }
}
