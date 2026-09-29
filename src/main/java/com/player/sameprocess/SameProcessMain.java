package com.player.sameprocess;


public class SameProcessMain {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Player Communication — Same Process Mode ===\n");
        new GameCoordinator().start();
    }
}