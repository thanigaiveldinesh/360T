package com.player.multiprocess.client;


public class ClientMain {
    public static void main(String[] args) {
        System.out.println("=== Player Communication — Multi-Process [CLIENT] ===\n");
        new ClientPlayer().run();
    }
}