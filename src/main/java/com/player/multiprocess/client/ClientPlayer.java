package com.player.multiprocess.client;

import com.player.multiprocess.SocketPlayer;

import java.io.IOException;
import java.net.Socket;


public class ClientPlayer extends SocketPlayer {

    private static final String HOST           = "localhost";
    private static final int    RETRY_LIMIT    = 10;
    private static final long   RETRY_DELAY_MS = 500;

    private final int connectPort;

    public ClientPlayer()           { this(PORT); }
    public ClientPlayer(int port)   { super("Initiator", true); this.connectPort = port; }

    @Override
    protected Socket connect() throws IOException {
        for (int attempt = 1; attempt <= RETRY_LIMIT; attempt++) {
            try {
                Socket s = new Socket(HOST, connectPort);
                System.out.println("[" + name + "] Connected to server.");
                return s;
            } catch (IOException e) {
                System.out.println("[" + name + "] Not ready. Attempt "
                        + attempt + "/" + RETRY_LIMIT + ". Retrying...");
                try { Thread.sleep(RETRY_DELAY_MS); }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted during retry", ie);
                }
            }
        }
        throw new IOException("Could not connect after " + RETRY_LIMIT + " attempts.");
    }
}