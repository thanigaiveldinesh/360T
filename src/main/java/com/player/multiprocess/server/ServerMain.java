package com.player.multiprocess.server;


public class ServerMain {
    public static void main(String[] args) {
        System.out.println("=== Player Communication — Multi-Process [SERVER] ===\n");
        new ServerPlayer().run();
    }
}