package com.mcprohosting.plugins.dynamicbungee.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(of = ("name"))
public class NetRegisteredTask {
    @Getter
    private List<String> args;

    @Getter
    private String name;

    private Map<Object, Method> handlers;

    public NetRegisteredTask(String name, List<String> args, Map<Object, Method> handlers) {
        this.args = args;
        this.name = name;
        this.handlers = handlers;
    }

    public void registerHandler(Object o, Method m) {
        this.handlers.put(o, m);
    }

    public void callHandlers(Map<String, Object> data) {
        for (Map.Entry<Object, Method> objectMethodEntry : this.handlers.entrySet()) {
            try {
                objectMethodEntry.getValue().invoke(objectMethodEntry.getKey(), data);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // Something went wrong? Null data perhaps.
            }
        }
    }
}
