package com.mcprohosting.plugins.dynamicbungee.config;

import net.cubespace.Yamler.Config.Config;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.util.ArrayList;

public class MainConfig extends Config {

    public MainConfig(Plugin plugin) {
        CONFIG_HEADER = new String[] {"Dynamic Bungee Configuration!"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");

        invoke();
    }

    public void invoke() {
        try {
            this.init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private String jedis_host = "127.0.0.1";
    private int jedis_port = 6379;
    private int jedis_timeout = 10000;
    private String jedis_password = "";

    public JedisPool getJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);

        if (jedis_password.equals("") || jedis_password == null) {
            return new JedisPool(config, jedis_host, jedis_port, jedis_timeout);
        } else {
            return new JedisPool(config, jedis_host, jedis_port, jedis_timeout, jedis_password);
        }
    }

}
