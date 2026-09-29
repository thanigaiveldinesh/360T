package com.player.sameprocess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

class GameCoordinatorTest {

    @Test @Timeout(10)
    void fullGameShouldCompleteWithoutDeadlock() throws InterruptedException {
        new GameCoordinator().start();
    }

    @Test @Timeout(10)
    void gameCoordinatorShouldBeInstantiableMultipleTimes() throws InterruptedException {
        new GameCoordinator().start();
        new GameCoordinator().start();
    }

    @Test @Timeout(10)
    void gameShouldFinishWellWithinTimeout() throws InterruptedException {
        long start = System.currentTimeMillis();
        new GameCoordinator().start();
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(elapsed < 8000,
                "Game took too long — possible stop condition bug. Elapsed: " + elapsed + "ms");
    }
}