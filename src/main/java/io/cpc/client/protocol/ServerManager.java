package io.cpc.client.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ConnectException;

import io.cpc.client.Application;
import io.cpc.client.logging.Logger;
import io.cpc.client.protocol.tcp.TCPConnection;

public class ServerManager {
    private static final Logger LOG = new Logger("Server Manager");
    private static TCPConnection connectedServer;

    public static TCPConnection getConnectedServer() {
        return connectedServer;
    }

    /**
     * Connect to the specified server and return the remote opened port
     *
     * @param toConnect server to connect
     * @return port that remote server opened to receive incoming connection
     */
    public static int connectTo(TCPConnection toConnect) {
        LOG.info("Connecting to a relay server (" + toConnect.getServerIP() + ") on local port " + toConnect.getLocalPort());
        try {
            int remotePort = toConnect.connect();
            LOG.info("Connected to relay server "
                    + toConnect.getServerIP()
                    + " on port "
                    + toConnect.getServerUsedPort() + "!");
            connectedServer = toConnect;
            return remotePort;
        } catch (ConnectException e) {
            LOG.severe("Cannot connect to " + toConnect.getServerIP() + ":" + toConnect.getServerUsedPort() + ", aborting...");
            Application.quit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Create a new thread that wait for server socket requests
     */
    public static void waitForRequests() {
        Thread requestThread = new Thread(() -> {
            LOG.info("Waiting for requests...");
            while (!Thread.currentThread().isInterrupted()) {
                DataInputStream in = null;
                try {
                    in = new DataInputStream(connectedServer.getServiceConnection().getInputStream());

                    // Wait for port message
                    int available;
                    do {
                        available = in.available();
                    }
                    while (available < 4);
                    LOG.info("A user connected!");
                    int portToConnect;
                    portToConnect = in.readInt();
                    connectedServer.sendNewSocket(portToConnect);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        );
        requestThread.setName("Request Wait");
        requestThread.start();


    }

    /**
     * Connect to the given server and {@link ServerManager#waitForRequests()}
     *
     * @param server A {@link TCPConnection} instance representing the server to connect to
     */
    public static void initialize(TCPConnection server) {
        connectTo(server);
        waitForRequests();
    }

    public static void closeConnection() {
        if (connectedServer != null)
            connectedServer.closeConnection();
    }
}
