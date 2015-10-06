package edu.bupt.wangfu.sdn.info;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-5.
 */
public class Controller {
    public String name;
    public Controller(String controllerAddr){this.name = controllerAddr;}
    private Map<String, Switch> switchMap = new ConcurrentHashMap<String, Switch>();
}
