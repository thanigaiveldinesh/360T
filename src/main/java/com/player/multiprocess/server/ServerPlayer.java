package com.player.multiprocess.server;

import com.player.multiprocess.SocketPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerPlayer extends SocketPlayer {

    private final int bindPort;

    public ServerPlayer()           { this(PORT); }
    public ServerPlayer(int port)   { super("Responder", false); this.bindPort = port; }

    @Override
    protected Socket connect() throws IOException {
        System.out.println("[" + name + "] Listening on port " + bindPort + " ...");
        ServerSocket ss = new ServerSocket(bindPort);
        Socket client   = ss.accept();
        ss.close();
        System.out.println("[" + name + "] Client connected from " + client.getInetAddress());
        return client;
    }
}