package com.mcprohosting.plugins.dynamicbungee.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

@AllArgsConstructor
@ToString(of = {"info", "players"})
public class Heartbeat {
    /**
     * The Server that sent the ping.
     */
    @Getter @NonNull private ServerInfo info;

    /**
     * The time the heartbeat was sent.
     */
    @Getter @NonNull private Long timeHeartbeat;

    /**
     * Players.
     */
    @Getter @NonNull private List<String> players;
}
