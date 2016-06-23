package io.cpc.client;

import java.awt.*;
import java.io.File;

import io.cpc.client.gui.MainWindow;
import io.cpc.client.logging.Logger;
import io.cpc.client.plugin.PluginManager;
import io.cpc.client.protocol.ServerManager;
import io.cpc.client.protocol.tcp.TCPConnection;

public class Application {
    private static final String VERSION = "0.0.1";
    private static final Logger log = new Logger("Main");
    public static boolean startGUI = true;
    private static boolean debug = false;
    private static TCPConnection def = new TCPConnection();
    private static boolean autoConnect = false;

    public static void main(String[] args) {

        useArgs(args);
        // See if the app is started from cli or from gui
        if (!GraphicsEnvironment.isHeadless() && startGUI) {
            MainWindow.wrapper();
        } else if (System.console() != null) {
            CommandManager.start();
            log.info("Use 'help' for a list of commands");
        } else {
            log.severe("FATAL ERROR: Could not found a console or a graphical environment");
            quit(1);
        }

        log.info("Started Close Ports Connector v." + VERSION);

        // Scan for plugins
        PluginManager.scanForPlugin();

        // If autoConnect == true, start server
        if (autoConnect)
            ServerManager.initialize(def);

        // Start command manager


    }

    public static void quit(int state) {
        log.raw("Shutting down");
        ServerManager.closeConnection();
        log.raw(".");
        log.raw(".");
        log.raw(".\n");
        System.exit(state);

    }

    public static boolean isDebug() {
        return debug;
    }

    private static void useArgs(String[] args) {

        if (args.length < 3)
            return;
        for (String arg : args) {
            if (arg.matches("-\\w+=\\w+")) {
                if (arg.startsWith("-")) {
                    arg = arg.replaceFirst("-", "");
                    String[] split = arg.split("=");
                    identifyArg(split[0], split[1]);

                }
            } else {
                throw new IllegalArgumentException("Illegal argument: " + arg);
            }
        }
    }

    private static void identifyArg(String command, String value) {
        switch (command) {
            case "noGui":
                startGUI = false;
            case "pluginFolder":
                PluginManager.scanForPlugin(new File(value));
                break;
            case "serverIP":
                def.setServerIP(value);
                break;
            case "serverPort":
                if (!value.matches("^[0-9]{5}$"))
                    throw new IllegalArgumentException("Please insert a valid server port!");
                def.setServerPort(Integer.parseInt(value));
                break;
            case "localPort":
                if (!value.matches("^[0-9]{0,5}$"))
                    throw new IllegalArgumentException("Please insert a valid local port!");
                def.setLocalPort(Integer.parseInt(value));
                break;
            case "autoConnect":
                if (!value.matches("true|false"))
                    throw new IllegalArgumentException("Possibles values of command 'autoConnect': true, false");
                autoConnect = Boolean.parseBoolean(value);
                break;
        }
    }
}
