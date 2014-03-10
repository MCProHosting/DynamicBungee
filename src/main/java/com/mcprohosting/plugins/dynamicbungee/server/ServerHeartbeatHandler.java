package com.mcprohosting.plugins.dynamicbungee.server;

import com.mcprohosting.plugins.dynamicbungee.DynamicBungee;
import com.mcprohosting.plugins.dynamicbungee.data.ChannelSubscriber;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ServerHeartbeatHandler implements Runnable {

    /**
     * The amount of time that a server has to send a heartbeat before it's removed.
     */
    private static final Long TIME_EXPIRE = TimeUnit.SECONDS.toMillis(30);

    /**
     * Stores previous heartbeats.
     */
    private Map<ServerInfo, Heartbeat> heartbeats;

    /**
     * Creates a new handler, and schedules it in the BungeeCord scheduler.
     */
    public ServerHeartbeatHandler() {
        this.heartbeats = new HashMap<>();
        schedule();
    }

    /**
     * Call this method when a server sends a heartbeat.
     * @param info The server that a heartbeat was received for.
     */
    public void heartbeatReceived(ServerInfo info, List playerList) {
        ArrayList<String> players = new ArrayList<>();
        for (Object p : playerList) {
            if ((p instanceof String) == false) {
                continue;
            }
            players.add((String) p);
        }
        this.heartbeats.put(info, new Heartbeat(info, Calendar.getInstance().getTimeInMillis(), players));
    }

    @Override
    public void run() {
        ConcurrentMap<String, ServerInfo> allServerInfo = DynamicBungee.getPlugin().getServerInfo();
        for (ServerInfo info : allServerInfo.values()) {
            Heartbeat heartbeat = heartbeats.get(info);
            if (heartbeat == null ||
                    Calendar.getInstance().getTimeInMillis() - heartbeat.getTimeHeartbeat()
                            > ServerHeartbeatHandler.TIME_EXPIRE) {
                ProxyServer.getInstance().getServers().remove(info.getName());
                ServerHandler.disconnectAll(info);
                this.heartbeats.remove(info);
            }
        }
        schedule();
    }

    /**
     * Reschedule this in the scheduler for execution.
     */
    public void schedule() {
        ProxyServer.getInstance().getScheduler().schedule(DynamicBungee.getPlugin(), this, 30, TimeUnit.SECONDS);
    }

}
