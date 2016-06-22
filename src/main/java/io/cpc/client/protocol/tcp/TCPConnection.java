package io.cpc.client.protocol.tcp;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.cpc.client.protocol.DoubleDirectionExchanger;

/**
 * Represents a connection to a remote relay server
 */
public class TCPConnection {

    private boolean connected = false;
    private String serverIP = null;
    private int serverPort = 0;
    private int serverUsedPort;
    private Socket serviceConnection;
    private int localPort;
    private List<Socket> openedSockets = new ArrayList<>();

    /**
     * Initialize a connection to the specified relay server
     *
     * @param serverIP   the remote host ip
     * @param remotePort the remote host port
     * @param localPort  the local port to share. Can be changed.
     * @see TCPConnection#TCPConnection(URL, int)
     */
    public TCPConnection(String serverIP, int remotePort, int localPort) {
        this.serverIP = serverIP;
        this.serverPort = remotePort;
        this.localPort = localPort;
    }

    /**
     * Initialize a connection to the specified url
     *
     * @param serverIPANDPort {@link URL} representing ip and port to connect
     * @param localPort       the local port to share. Can be changed.
     * @see TCPConnection#TCPConnection(String, int, int)
     */
    public TCPConnection(URL serverIPANDPort, int localPort) {
        this(serverIPANDPort.getHost(), serverIPANDPort.getPort(), localPort);
    }

    public TCPConnection() {
    }

    /**
     * @return a String representing remote server ip
     */
    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        if (this.serverIP != null) throw new IllegalStateException("Server ip already set");
        this.serverIP = serverIP;
    }

    /**
     * @return an int representing remote server port
     */
    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        if (this.serverPort != 0) throw new IllegalStateException("Server port already set");
        this.serverPort = serverPort;
    }

    public Socket getServiceConnection() {
        return serviceConnection;
    }

    /**
     * @return local shared port
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Set local shared ports for new connections. Connections will not be ended.
     *
     * @param localPort an int from 1 to 65535. Cannot be null.
     */
    public void setLocalPort(int localPort) {
        if (localPort < 1 || localPort > 65535)
            throw new IllegalArgumentException("Not valid port number");
        this.localPort = localPort;
    }

    public int getServerUsedPort() {
        if (!connected)
            throw new IllegalStateException("Can't retrieve server used port while not connected");
        return serverUsedPort;
    }

    /**
     * Open a service connection to the current server
     *
     * @return a number representing the remote open port
     * @throws IOException           if it can't connect or if it can't retrieve the stream
     * @throws IllegalStateException if already connected
     */
    public int connect() throws IOException {
        if (connected) throw new IllegalStateException("Already connected");

        serviceConnection = new Socket(serverIP, serverPort);

        DataInputStream in = new DataInputStream(serviceConnection.getInputStream());

        // Wait for port message
        int available;
        do {
            available = serviceConnection.getInputStream().available();
        }
        while (available < 4);

        // Read port
        serverUsedPort = in.readInt();
        connected = true;
        return serverUsedPort;
    }

    /**
     * Sends new socket request to server to specified temp port
     *
     * @param remoteTempPort temp port that remote relay server create to exchange sockets
     */
    public void sendNewSocket(int remoteTempPort) {
        Socket connectionToRelay = null;
        Socket connectionToLocal = null;
        try {
            connectionToRelay = new Socket(serverIP, remoteTempPort);
            connectionToLocal = new Socket(InetAddress.getLoopbackAddress(), localPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket finalConnectionToRelay = connectionToRelay;
        Socket finalConnectionToLocal = connectionToLocal;


        // Start data streaming
        new DoubleDirectionExchanger(finalConnectionToLocal, finalConnectionToRelay).start();

    }

    public void closeConnection() {
        for (Socket e : openedSockets) {
            try {
                e.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
