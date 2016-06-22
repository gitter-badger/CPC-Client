package io.cpc.client.protocol;


import java.net.Socket;

public class DoubleDirectionExchanger {
    private Exchanger s1Tos2;
    private Exchanger s2Tos1;

    public DoubleDirectionExchanger(Socket s1, Socket s2) {
        s1Tos2 = new Exchanger(s1, s2);
        s2Tos1 = new Exchanger(s2, s1);
    }

    public void start() {
        s1Tos2.start();
        s2Tos1.start();
    }
}
