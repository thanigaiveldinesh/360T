package com.player.multiprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SocketPlayer {

    public  static final String STOP_TOKEN   = "__DONE__";
    protected static final int  MAX_MESSAGES = 10;
    protected static final int  PORT         = 9090;

    protected final String  name;
    protected final boolean isInitiator;
    protected final AtomicInteger sentCount  = new AtomicInteger(0);

    protected SocketPlayer(String name, boolean isInitiator) {
        this.name        = name;
        this.isInitiator = isInitiator;
    }

    protected abstract Socket connect() throws IOException;

    public void run() {
        System.out.println("[" + name + "] Starting — isInitiator=" + isInitiator);

        try (Socket socket  = connect();
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            System.out.println("[" + name + "] Connected. PID=" +
                    ProcessHandle.current().pid());

            if (isInitiator) {
                send(out, "Hello");
            }

            String line;
            while ((line = in.readLine()) != null) {

                if (STOP_TOKEN.equals(line)) {
                    System.out.println("[" + name + "] Received stop signal. Exiting.");
                    break;
                }

                System.out.println("[" + name + "] Received: \"" + line + "\"");

                if (isInitiator && sentCount.get() >= MAX_MESSAGES) {
                    System.out.println("[" + name + "] All " + MAX_MESSAGES +
                            " round-trips complete. Sending stop signal.");
                    out.println(STOP_TOKEN);
                    break;
                }

                send(out, line + "_" + (sentCount.get() + 1));
            }

        } catch (IOException e) {
            System.err.println("[" + name + "] I/O error: " + e.getMessage());
        }

        System.out.println("[" + name + "] Stopped. Total sent: " + sentCount.get());
    }

    private void send(PrintWriter out, String content) {
        int count = sentCount.incrementAndGet();
        System.out.println("[" + name + "] Sending (#" + count + "): \"" + content + "\"");
        out.println(content);
    }
}