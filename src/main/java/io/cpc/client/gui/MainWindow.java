package io.cpc.client.gui;

import java.awt.*;
import java.io.PrintStream;

import javax.swing.*;

import io.cpc.client.logging.Logger;
import io.cpc.client.protocol.ServerManager;
import io.cpc.client.protocol.tcp.TCPConnection;

public class MainWindow {

    private final Logger LOG = new Logger("GUI");
    private JFrame window;
    private JTextField localPortField;
    private JTextField customIP;
    private boolean isCustom = false;
    private JLabel customIPlbl;

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    public static void wrapper() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setStatus(String message) {
        window.setTitle("ClosePortConnector - " + message);
    }

    /**
     * Initialize the contents of the window.
     */
    private void initialize() {

        window = new JFrame();
        window.setResizable(false);
        window.setBounds(100, 100, 500, 300);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().setLayout(null);

        setStatus("Disconnected");

        String[] serverNames = new String[ServerManager.DEFAULT_SERVERS.length + 1];
        for (int i = 0; i < ServerManager.DEFAULT_SERVERS.length; i++) {
            serverNames[i] = ServerManager.DEFAULT_SERVERS[i].getServerIP();
        }
        serverNames[ServerManager.DEFAULT_SERVERS.length] = "Custom";

        JComboBox serverSelectionBox = new JComboBox(serverNames);
        serverSelectionBox.addItemListener(gotE -> {
            if (gotE.getItem().equals("Custom")) {
                isCustom = true;
                customIPlbl.setEnabled(true);
                customIP.setEnabled(true);
            } else {
                isCustom = false;
                customIPlbl.setEnabled(false);
                customIP.setEnabled(false);
            }

        });
        serverSelectionBox.setBounds(60, 190, 124, 22);
        window.getContentPane().add(serverSelectionBox);

        JLabel serverlbl = new JLabel("Server");
        serverlbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
        serverlbl.setBounds(12, 186, 56, 28);
        window.getContentPane().add(serverlbl);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(ignore -> {
            if (isReady()) {
                if (isCustom) {
                    String serverIPANDPort = customIP.getText();
                    String serverIP = serverIPANDPort.split(":")[0];
                    String serverPortString = serverIPANDPort.split(":")[1];
                    try {
                        int serverPort = Integer.parseInt(serverPortString);
                        TCPConnection.validatePort(serverPort);
                        ServerManager.connectTo(new TCPConnection(serverIP, serverPort, Integer.parseInt(localPortField.getText())));
                    } catch (NumberFormatException e) {
                        LOG.severe("Insert a valid port");
                    }

                } else {
                    ServerManager.connectTo(
                            new TCPConnection(ServerManager.DEFAULT_SERVERS[serverSelectionBox.getSelectedIndex()].getServerIP(),
                                    ServerManager.DEFAULT_SERVERS[serverSelectionBox.getSelectedIndex()].getServerPort(),
                                    Integer.parseInt(localPortField.getText())));
                }
            }
        });
        connectButton.setBounds(385, 227, 97, 25);
        window.getContentPane().add(connectButton);

        localPortField = new JTextField();
        localPortField.setToolTipText("Port");
        localPortField.setBounds(287, 228, 86, 22);
        window.getContentPane().add(localPortField);
        localPortField.setColumns(10);

        JLabel lblLocalPort = new JLabel("Local Port");
        lblLocalPort.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblLocalPort.setBounds(217, 224, 71, 28);
        window.getContentPane().add(lblLocalPort);

        JTextArea consoleTextArea = new JTextArea(6, 16);
        JScrollPane consoleTextAreaWrapper = new JScrollPane(consoleTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        consoleTextAreaWrapper.setBounds(214, 13, 268, 201);
        consoleTextArea.setEditable(false);
        window.getContentPane().add(consoleTextAreaWrapper);

        TextAreaOutputStream taOutputStream = new TextAreaOutputStream(consoleTextArea);
        System.setOut(new PrintStream(taOutputStream));
        consoleTextArea.setLineWrap(true);

        customIP = new JTextField();
        customIP.setEnabled(false);
        customIP.setBounds(89, 225, 116, 22);
        window.getContentPane().add(customIP);
        customIP.setColumns(10);


        customIPlbl = new JLabel("Custom IP");
        customIPlbl.setEnabled(false);
        customIPlbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
        customIPlbl.setBounds(12, 230, 77, 16);
        window.getContentPane().add(customIPlbl);
    }

    boolean isReady() {
        if (!localPortField.getText().equals("")) {
            try {
                TCPConnection.validatePort(Integer.parseInt(localPortField.getText()));
                return true;
            } catch (IllegalArgumentException e) {
                LOG.severe("Insert a valid port");
            }
        }
        return false;
    }
}
