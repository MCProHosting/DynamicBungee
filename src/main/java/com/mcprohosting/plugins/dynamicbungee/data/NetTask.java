package com.mcprohosting.plugins.dynamicbungee.data;

import com.mcprohosting.plugins.dynamicbungee.DynamicBungee;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

public class NetTask {

    @Getter
    private HashMap<String, Object> args;

    @Getter private String name;

    private NetTask(String name) {
        this.name = name;
        this.args = new HashMap<String, Object>();
    }

    public static NetTask withName(String name) {
        return new NetTask(name);
    }

    public NetTask withArg(String arg, Object o) {
        this.args.put(arg, o);
        return this;
    }

    public boolean send(String channel) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("task", name);
            JSONObject argsObject = new JSONObject(args);
            jsonObject.put("data", argsObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        Jedis jedis = DynamicBungee.getPlugin().getJedisResource();
        jedis.publish(channel, jsonObject.toString());
        DynamicBungee.getPlugin().returnJedisResource(jedis);

        return true;
    }
}
