package com.player.sameprocess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void shouldStoreSenderAndContentCorrectly() {
        Message msg = new Message("Initiator", "Hello");
        assertEquals("Initiator", msg.getSender());
        assertEquals("Hello", msg.getContent());
    }

    @Test
    void shouldFormatToStringCorrectly() {
        Message msg = new Message("Responder", "Hello_1");
        assertTrue(msg.toString().contains("Responder"));
        assertTrue(msg.toString().contains("Hello_1"));
    }

    @Test
    void shouldHandleEmptyContent() {
        Message msg = new Message("Player1", "");
        assertEquals("", msg.getContent());
    }

    @Test
    void shouldHandleCounterSuffixInContent() {
        Message msg = new Message("Initiator", "Hello_1_2_3");
        assertEquals("Hello_1_2_3", msg.getContent());
    }

    @Test
    void shouldAcceptNullSenderWithoutThrowing() {
        assertDoesNotThrow(() -> new Message(null, "Hello"));
    }

    @Test
    void shouldAcceptNullContentWithoutThrowing() {
        assertDoesNotThrow(() -> new Message("Player1", null));
    }

    @Test
    void twoMessagesWithSameDataShouldBeIndependent() {
        Message m1 = new Message("P1", "Hello");
        Message m2 = new Message("P1", "Hello");
        assertNotSame(m1, m2);
    }
}