package com.mcprohosting.plugins.dynamicbungee.entities;

import net.md_5.bungee.api.connection.PendingConnection;

import java.util.HashMap;
import java.util.Map;

public class User {

    private static Map<String, User> users;

    static {
        users = new HashMap<>();
    }

    private String name;
    private String uuid;
    private String currentServer;
    private String destinationServer;

    public User(PendingConnection connection) {
        name = connection.getName();
        uuid = connection.getUniqueId().toString();
        currentServer = "";
        destinationServer = "";

        addUser(this);
    }

    public static void addUser(User user) {
        users.put(user.getName().toLowerCase(), user);
    }

    public static void removeUser(String user) {
        users.remove(user.toLowerCase());
    }

    public static User getUser(String user) {
        return users.get(user.toLowerCase());
    }

    public String getName() {
        return name;
    }

    public String getUUID() {
        return uuid;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public String getDestinationServer() {
        return destinationServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }

    public void setDestinationServer(String destinationServer) {
        this.destinationServer = destinationServer;
    }

}
