package com.mcprohosting.plugins.dynamicbungee.server;

import com.mcprohosting.plugins.dynamicbungee.DynamicBungee;
import com.mcprohosting.plugins.dynamicbungee.config.MainConfig;
import com.mcprohosting.plugins.dynamicbungee.data.NetTaskSubscribe;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;

public class ServerHandler {

    @NetTaskSubscribe(name = "heartbeat", args = {"name", "ip", "port", "players"})
    public void onHeartbeat(HashMap<String, Object> args) {
        Object i = args.get("ip");
        Object n = args.get("name");
        Object p = args.get("port");
        Object pl = args.get("players");

        if ((i instanceof String) == false
                || (n instanceof String) == false
                || (p instanceof Integer) == false
                || (pl instanceof List) == false) {
            return;
        }
        String ip = (String) i;
        String name = (String) n;
        Integer port = (Integer) p;
        List list = (List) pl;

        InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(name);
        if (serverInfo != null) {
            if (serverInfo.getAddress().equals(socketAddress)) {
                DynamicBungee.getPlugin().getBeatHandler().heartbeatReceived(serverInfo, list);
                return;
            }
            disconnectAll(serverInfo);
        }
        ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, socketAddress, DynamicBungee.getPlugin().getConf().settings_motd, false);
        ProxyServer.getInstance().getServers().put(name, info);
        DynamicBungee.getPlugin().getBeatHandler().heartbeatReceived(info, list);
    }

    @NetTaskSubscribe(name = "disconnect", args = {"name"})
    public void onDisconnect(HashMap<String, Object> args) {
        Object n = args.get("name");
        if ((n instanceof String) == false) {
            return;
        }
        String name = (String) n;
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(name);
        if (serverInfo == null) {
            return;
        }
        disconnectAll(serverInfo);
        ProxyServer.getInstance().getServers().remove(name);
    }

    /**
     * Disconnect all players from the server.
     * @param info The server to disconnect players from.
     */
    public static void disconnectAll(ServerInfo info) {
        for (ProxiedPlayer player : info.getPlayers()) {
            player.disconnect("The server is currently unavailable, please try again soon!");
        }
    }

}
