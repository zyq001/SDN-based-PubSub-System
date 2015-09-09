package com.sdn.queue;

//import com.sdn.OvsOperation.OvsOperate;
import com.sdn.info.DevInfo;
import com.sdn.info.Switch;
import com.sdn.sflow.SflowAPI;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 15-7-9.
 */
public class QueueAdjust extends Thread{

    @Override
   public void run(){

        Map<String, Switch> switchs = DevInfo.getINSTANCE().getSwitchs();

        for(Switch sw: switchs.values()){
            Map<Integer,Integer> ports = sw.getPortList();
            for(Integer port: ports.keySet()){
                double speed = SflowAPI.getSpeed(sw.getIpAddr(), "" + port +".ifinpkts");
                double bandWidth = ports.get(port);
                if(speed > bandWidth / 2 && speed <= bandWidth * 2 / 3){//weak
                    QueueManagerment.enQueue(sw.getDPID(), port, "30003","1.1");
                }else if(speed > bandWidth * 2 / 3){
                    QueueManagerment.enQueue(sw.getDPID(), port, "30002","1.1");
                    QueueManagerment.enQueue(sw.getDPID(), port, "30003","1.2");
                }
            }
        }

    }

    public static void main(String[] args){

    }

}
