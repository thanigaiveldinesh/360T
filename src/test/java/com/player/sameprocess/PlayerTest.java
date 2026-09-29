package com.player.sameprocess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Thread[] wirePlayers(Player initiator, Player responder) {
        initiator.setPeerInbox(responder.getInbox());
        responder.setPeerInbox(initiator.getInbox());
        return new Thread[]{
                new Thread(initiator, "t-initiator"),
                new Thread(responder, "t-responder")
        };
    }

    @Test @Timeout(5)
    void initiatorShouldSendFirstMessageWithoutWaiting() throws InterruptedException {
        Player initiator = new Player("Initiator", true, 10);
        Player responder = new Player("Responder", false, 10);
        wirePlayers(initiator, responder)[0].start();

        Message first = responder.getInbox().poll(3, TimeUnit.SECONDS);

        assertNotNull(first);
        assertEquals("Hello", first.getContent());

        initiator.stop();
    }

    @Test @Timeout(5)
    void replyShouldContainReceivedContentPlusCounter() throws InterruptedException {
        Player responder = new Player("Responder", false, 10);
        BlockingQueue<Message> capture = new LinkedBlockingQueue<>();
        responder.setPeerInbox(capture);
        responder.getInbox().offer(new Message("Initiator", "Hello"));

        Thread t = new Thread(responder);
        t.start();

        Message reply = capture.poll(3, TimeUnit.SECONDS);

        assertNotNull(reply);
        assertEquals("Hello_1", reply.getContent());

        responder.stop();
        t.join(2000);
    }

    @Test @Timeout(10)
    void fullGameShouldComplete10RoundTrips() throws InterruptedException {
        Player initiator = new Player("Initiator", true, 10);
        Player responder = new Player("Responder", false, 10);
        Thread[] threads = wirePlayers(initiator, responder);

        threads[0].start();
        threads[1].start();
        threads[0].join();
        responder.stop();
        threads[1].join();

        assertFalse(threads[0].isAlive());
        assertFalse(threads[1].isAlive());
    }

    @Test @Timeout(5)
    void responderShouldIncrementCounterOnEachReply() throws InterruptedException {
        Player responder = new Player("Responder", false, 10);
        BlockingQueue<Message> capture = new LinkedBlockingQueue<>();
        responder.setPeerInbox(capture);

        responder.getInbox().offer(new Message("I", "Msg1"));
        responder.getInbox().offer(new Message("I", "Msg2"));
        responder.getInbox().offer(new Message("I", "Msg3"));

        Thread t = new Thread(responder);
        t.start();

        Message r1 = capture.poll(3, TimeUnit.SECONDS);
        Message r2 = capture.poll(3, TimeUnit.SECONDS);
        Message r3 = capture.poll(3, TimeUnit.SECONDS);

        assertTrue(r1.getContent().endsWith("_1"));
        assertTrue(r2.getContent().endsWith("_2"));
        assertTrue(r3.getContent().endsWith("_3"));

        responder.stop();
        t.join(2000);
    }

    @Test @Timeout(5)
    void stopShouldUnblockPlayerWaitingOnEmptyInbox() throws InterruptedException {
        Player responder = new Player("Responder", false, 10);
        responder.setPeerInbox(new LinkedBlockingQueue<>());

        Thread t = new Thread(responder);
        t.start();

        assertTrue(t.isAlive());
        responder.stop();
        t.join(3000);

        assertFalse(t.isAlive());
    }

    @Test @Timeout(5)
    void initiatorShouldNotSendMoreThanMaxMessages() throws InterruptedException {
        Player initiator = new Player("Initiator", true, 3);
        Player responder = new Player("Responder", false, 3);
        Thread[] threads = wirePlayers(initiator, responder);

        threads[0].start();
        threads[1].start();
        threads[0].join();
        responder.stop();
        threads[1].join();

        assertFalse(threads[0].isAlive());
    }

    @Test @Timeout(5)
    void playerShouldHandleInterruptionGracefully() throws InterruptedException {
        Player responder = new Player("Responder", false, 10);
        responder.setPeerInbox(new LinkedBlockingQueue<>());

        Thread t = new Thread(responder);
        t.start();
        t.interrupt();
        t.join(3000);

        assertFalse(t.isAlive());
    }

    @Test @Timeout(5)
    void stopTokenShouldNotProduceAReply() throws InterruptedException {
        Player responder = new Player("Responder", false, 10);
        BlockingQueue<Message> capture = new LinkedBlockingQueue<>();
        responder.setPeerInbox(capture);

        responder.getInbox().offer(new Message("I", "Hello"));
        responder.getInbox().offer(new Message("SYSTEM", "__STOP__"));

        Thread t = new Thread(responder);
        t.start();
        t.join(3000);

        assertEquals(1, capture.size());
    }
}