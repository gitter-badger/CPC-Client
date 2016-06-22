package io.cpc.client.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Exchanger extends Thread {
    private Socket s1;
    private Socket s2;

    public Exchanger(Socket s1, Socket s2) {
        this.s1 = s1;
        this.s2 = s2;
        this.setName("Exchanger between " + s1.getInetAddress() + " and " + s1.getInetAddress());

    }

    public void run() {
        try (InputStream in = s1.getInputStream(); OutputStream out = s2.getOutputStream()) {
            for (int i; (i = in.read()) != -1; ) {
                out.write(i);
            }
        } catch (IOException ignore) {
        }
    }
}

