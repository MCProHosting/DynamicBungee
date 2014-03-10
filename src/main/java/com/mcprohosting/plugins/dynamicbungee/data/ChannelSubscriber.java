package com.mcprohosting.plugins.dynamicbungee.data;

import com.mcprohosting.plugins.dynamicbungee.DynamicBungee;
import redis.clients.jedis.Jedis;

public class ChannelSubscriber implements Runnable {

    final String channel;

    public ChannelSubscriber(final String channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        subscribe();
    }

    public void subscribe() {
        NetDelegate.channels.add(channel);

        Jedis jedis = DynamicBungee.getPlugin().getJedisResource();
        jedis.subscribe(NetDelegate.instance, channel);
        DynamicBungee.getPlugin().returnJedisResource(jedis);
    }
}
