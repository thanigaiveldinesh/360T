package com.player.sameprocess;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Player implements Runnable {

    private final String name;
    private final BlockingQueue<Message> inbox;
    private BlockingQueue<Message> peerInbox;

    private final AtomicInteger sentCount  = new AtomicInteger(0);
    private final int            maxMessages;
    private final boolean        isInitiator;
    private volatile boolean     running = true;

    public Player(String name, boolean isInitiator, int maxMessages) {
        this.name        = name;
        this.isInitiator = isInitiator;
        this.maxMessages = maxMessages;
        this.inbox       = new LinkedBlockingQueue<>();
    }

    public void setPeerInbox(BlockingQueue<Message> peerInbox) {
        this.peerInbox = peerInbox;
    }

    public BlockingQueue<Message> getInbox() { return inbox; }

    public void stop() {
        running = false;
        inbox.offer(new Message("SYSTEM", "__STOP__"));
    }

    @Override
    public void run() {
        log("Started. isInitiator=" + isInitiator);

        if (isInitiator) {
            sendToPeer("Hello");
        }

        while (running) {
            try {
                Message received = inbox.take();

                if ("__STOP__".equals(received.getContent())) {
                    break;
                }

                log("Received: " + received);

                if (isInitiator && sentCount.get() >= maxMessages) {
                    log("Reached " + maxMessages + " messages. Initiating shutdown.");
                    running = false;
                    break;
                }

                String replyContent = received.getContent() + "_" + (sentCount.get() + 1);
                sendToPeer(replyContent);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log("Interrupted — exiting.");
                break;
            }
        }

        log("Stopped. Total messages sent: " + sentCount.get());
    }

    private void sendToPeer(String content) {
        int count = sentCount.incrementAndGet();
        Message msg = new Message(name, content);
        log("Sending (#" + count + "): " + msg);
        peerInbox.offer(msg);
    }

    private void log(String text) {
        System.out.printf("[%-10s | Thread=%-25s] %s%n",
                name, Thread.currentThread().getName(), text);
    }

    public String getName() { return name; }
}