package com.mcprohosting.plugins.dynamicbungee.data;

import com.mcprohosting.plugins.dynamicbungee.DynamicBungee;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.*;

public class NetHandler {
    private Thread delegateThread;
    private Map<String, NetTaskSubscribe> tasks;
    private Map<NetTaskSubscribe, NetRegisteredTask> handlers;

    public NetHandler() {
        this.tasks = new HashMap<String, NetTaskSubscribe>();
        this.handlers = new HashMap<NetTaskSubscribe, NetRegisteredTask>();

        delegateThread = new Thread(new NetDelegate());
        delegateThread.start();
    }

    public void registerTasks(Object o) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(NetTaskSubscribe.class)) {
                continue;
            }

            if (method.getParameterTypes().length != 1) {
                continue;
            }

            if (method.getParameterTypes()[0].equals(HashMap.class) == false) {
                continue;
            }

            NetTaskSubscribe task = method.getAnnotation(NetTaskSubscribe.class);
            NetRegisteredTask handler;
            if (this.handlers.containsKey(task.name()) == false) {
                handler = new NetRegisteredTask(task.name(),
                        Arrays.asList(task.args()),
                        new HashMap<Object, Method>());
                this.tasks.put(task.name(), task);
                this.handlers.put(task, handler);
            } else {
                handler = this.handlers.get(task);
            }

            handler.registerHandler(o, method);
        }
    }

    public boolean handleMessage(JSONObject object) {
        String task;

        try {
            task = object.getString("task");
            NetTaskSubscribe netTask = this.tasks.get(task);
            if (netTask == null) {
                DynamicBungee.getPlugin().getLogger().warning("Unhandled task " + task + " " + object.toString());
                return false;
            }

            JSONObject data = object.getJSONObject("data");
            HashMap<String, Object> dataMap = objectToHashMap(data);
            Set<String> dataKeys = dataMap.keySet();

            if (dataKeys.containsAll(Arrays.asList(netTask.args())) == false) {
                return false;
            }

            NetRegisteredTask registeredTask = this.handlers.get(netTask);
            registeredTask.callHandlers(dataMap);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private HashMap<String, Object> objectToHashMap(JSONObject data) throws JSONException {
        HashMap<String, Object> returnVal = new HashMap<>();
        Iterator i = data.keys();
        while (i.hasNext()) {
            Object next = i.next();
            if (!(next instanceof String)) continue;
            String key = (String)next;
            Object o = data.get(key);
            returnVal.put(key, parseObject(o)); //OMG SO RECURSIVE
        }
        return returnVal;
    }

    private ArrayList<Object> objectToArrayList(JSONArray array) throws JSONException {
        ArrayList<Object> objects = new ArrayList<>();
        int index = 0;
        while (index < array.length()) {
            objects.add(parseObject(array.get(index))); //OMG SO RECURSIVE
            index++;
        }
        return objects;
    }

    private Object parseObject(Object obj) throws JSONException {
        Object o = obj;
        if (o instanceof JSONObject) {
            o = objectToHashMap((JSONObject) o);
        }
        if (o instanceof JSONArray) {
            o = objectToArrayList((JSONArray) o);
        }
        return o;
    }
}
