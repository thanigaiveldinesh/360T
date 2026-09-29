package com.player.multiprocess;

import com.player.multiprocess.client.ClientPlayer;
import com.player.multiprocess.server.ServerPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SocketPlayerTest {

    @Test @Timeout(15)
    void fullTcpGameShouldComplete10RoundTrips() throws InterruptedException {
        Thread server = new Thread(() -> new ServerPlayer(9091).run());
        Thread client = new Thread(() -> new ClientPlayer(9091).run());

        server.start();
        Thread.sleep(200);
        client.start();

        client.join(12000);
        server.join(3000);

        assertFalse(server.isAlive());
        assertFalse(client.isAlive());
    }

    @Test @Timeout(10)
    void stopTokenShouldCauseCleanExit() throws Exception {
        Thread fakeServer = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(9092);
                 Socket s = ss.accept();
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                in.readLine();
                out.println(SocketPlayer.STOP_TOKEN);
            } catch (IOException ignored) {}
        });

        fakeServer.start();
        Thread.sleep(200);

        Thread client = new Thread(() -> new ClientPlayer(9092).run());
        client.start();
        client.join(5000);

        assertFalse(client.isAlive());
        fakeServer.join(2000);
    }

    @Test @Timeout(10)
    void clientShouldRetryWhenServerStartsLate() throws InterruptedException {
        Thread client = new Thread(() -> new ClientPlayer(9094).run());
        client.start();

        Thread.sleep(1000);
        Thread server = new Thread(() -> new ServerPlayer(9094).run());
        server.start();

        client.join(10000);
        server.join(3000);

        assertFalse(client.isAlive());
    }

    @Test @Timeout(10)
    void stopTokenShouldNotBeEchoedBack() throws Exception {
        List<String> received = new ArrayList<>();

        Thread fakeServer = new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(9095);
                 Socket s = ss.accept();
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                received.add(in.readLine());
                out.println(SocketPlayer.STOP_TOKEN);
                String extra = in.readLine();
                if (extra != null) received.add(extra);
            } catch (IOException ignored) {}
        });

        fakeServer.start();
        Thread.sleep(200);

        new Thread(() -> new ClientPlayer(9095).run()).start();
        fakeServer.join(5000);

        assertEquals(1, received.size());
        assertEquals("Hello", received.get(0));
    }
}