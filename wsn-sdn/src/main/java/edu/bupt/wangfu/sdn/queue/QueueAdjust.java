package edu.bupt.wangfu.sdn.queue;

//import com.sdn.OvsOperation.OvsOperate;

import edu.bupt.wangfu.sdn.info.Controller;
import edu.bupt.wangfu.sdn.info.Switch;
import edu.bupt.wangfu.sdn.sflow.SflowAPI;
import org.apache.servicemix.wsn.router.router.GlobleUtil;

import java.util.Map;

/**
 * Created by root on 15-7-9.
 */
public class QueueAdjust extends Thread{

    @Override
   public void run(){

        for(Map.Entry<String, Controller> entry: GlobleUtil.getInstance().controllers.entrySet()){
            adjustController(entry.getValue());
        }


    }

    private void adjustController(Controller controller){
        Map<String, Switch> switchs = controller.getSwitchMap();

        for(Switch sw: switchs.values()){
            Map<Integer,Integer> ports = sw.getPortList();
            for(Integer port: ports.keySet()){
                double speed = SflowAPI.getSpeed(sw.getIpAddr(), "" + port + ".ifinpkts");
                double bandWidth = ports.get(port);
                if(speed > bandWidth / 2 && speed <= bandWidth * 2 / 3){//weak
                    QueueManagerment.enQueue(controller, sw.getDPID(), port, "30003","1.1");

                }else if(speed > bandWidth * 2 / 3){
                    QueueManagerment.enQueue(controller, sw.getDPID(), port, "30002","1.1");
                    QueueManagerment.enQueue(controller,sw.getDPID(), port, "30003","1.2");
                }
            }
        }
    }

    public static void main(String[] args){

    }

}
