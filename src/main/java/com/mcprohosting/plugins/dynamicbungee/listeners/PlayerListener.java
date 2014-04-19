package com.mcprohosting.plugins.dynamicbungee.listeners;

import com.mcprohosting.plugins.dynamicbungee.entities.User;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(LoginEvent event) {
        User.addUser(new User(event.getConnection()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        User.removeUser(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerSwitch(ServerSwitchEvent event) {
        User.getUser(event.getPlayer().getName()).setCurrentServer(event.getPlayer().getServer().getInfo().getName());
    }

}
