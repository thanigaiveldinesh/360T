package com.player.sameprocess;


public class GameCoordinator {

    private static final int MAX_MESSAGES = 10;

    private final Player initiator;
    private final Player responder;

    public GameCoordinator() {
        initiator = new Player("Initiator", true,  MAX_MESSAGES);
        responder = new Player("Responder", false, MAX_MESSAGES);
    }

    public void start() throws InterruptedException {
        initiator.setPeerInbox(responder.getInbox());
        responder.setPeerInbox(initiator.getInbox());

        Thread initiatorThread = new Thread(initiator, "Thread-" + initiator.getName());
        Thread responderThread = new Thread(responder, "Thread-" + responder.getName());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            initiator.stop();
            responder.stop();
        }));

        initiatorThread.start();
        responderThread.start();

        initiatorThread.join();

        responder.stop();
        responderThread.join();

        System.out.println("\n=== Game Over — both players stopped cleanly. ===");
    }
}