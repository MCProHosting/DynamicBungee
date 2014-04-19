package com.mcprohosting.plugins.dynamicbungee;

import com.mcprohosting.plugins.dynamicbungee.config.MainConfig;
import com.mcprohosting.plugins.dynamicbungee.data.NetHandler;
import com.mcprohosting.plugins.dynamicbungee.listeners.PlayerListener;
import com.mcprohosting.plugins.dynamicbungee.server.BaseReceiver;
import com.mcprohosting.plugins.dynamicbungee.server.ServerHandler;
import com.mcprohosting.plugins.dynamicbungee.server.ServerHeartbeatHandler;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DynamicBungee extends Plugin {

    private static DynamicBungee plugin;
    private MainConfig config;
    private JedisPool jedis;
    private NetHandler dispatch;
    private ServerHeartbeatHandler beatHandler;

    private DynamicPluginLoader pluginLoader;

    public void onEnable() {
        plugin = this;
        config = new MainConfig(this);

        initJedis();

        pluginLoader = new DynamicPluginLoader();

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener());
    }

    public static DynamicBungee getPlugin() {
        return plugin;
    }

    public void initJedis() {
        jedis = config.getJedisPool();

        Jedis connection = getJedisResource();
        if (connection == null) {
            return;
        } else {
            getLogger().info("Connected to Jedis Server!");
            returnJedisResource(connection);
        }

        dispatch = new NetHandler();
        getDispatch().registerTasks(new ServerHandler());
        getDispatch().registerTasks(new BaseReceiver());
        beatHandler = new ServerHeartbeatHandler();
    }

    public Jedis getJedisResource() {
        try {
            return jedis.getResource();
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            getLogger().warning("Unable to acquire connection from Redis server!");
        }

        return null;
    }

    public void returnJedisResource(Jedis jedis) {
        this.jedis.returnResource(jedis);
    }

    public NetHandler getDispatch() {
        return dispatch;
    }

    public MainConfig getConf() {
        return config;
    }

    public ConcurrentMap<String, ServerInfo> getServerInfo() {
        return new ConcurrentHashMap<>(ProxyServer.getInstance().getServers());
    }

    public ServerHeartbeatHandler getBeatHandler() {
        return beatHandler;
    }
}
