package io.cpc.client.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.cpc.client.Application;

public class Logger {

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
    private static boolean initialized;
    private static PrintWriter fileWriter;
    private String invoker;

    public Logger(Class invoker) {
        this(invoker.getSimpleName());
    }

    public Logger(String invoker) {
        initializeIfNot();
        this.invoker = invoker;
    }

    private static void initializeIfNot() {
        final DateFormat log = new SimpleDateFormat("dd-MM-yy_HH-mm", Locale.getDefault());
        try {
            if (initialized) return;
            File logFile = new File(new File("./logs"), log.format(new Date(System.currentTimeMillis())) + ".log");
            logFile.getParentFile().mkdirs();
            logFile.createNewFile();
            fileWriter = new PrintWriter(new FileWriter(logFile));
            initialized = true;

        } catch (IOException e) {
            System.err.println("Cannot save log to file!");
        }
    }

    public void log(Level level, String message) {
        if (level == Level.ERROR)
            System.err.printf("[%s][%s] %s: %s\n", getTime(), invoker, level.toString(), message);
        else
            System.out.printf("[%s][%s] %s: %s\n", getTime(), invoker, level.toString(), message);
        fileWriter.write(String.format("[%s][%s] %s: %s\n", getTime(), invoker, level.toString(), message));
        fileWriter.flush();

    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void severe(String message) {
        log(Level.SEVERE, message);
    }

    public void fatal(String message) {
        log(Level.ERROR, message);
    }

    public void debug(String message) {
        if (Application.isDebug()) {
            log(Level.DEBUG, message);
        }
    }

    public void command(String command) {
        fileWriter.write("Executed command from console: " + command + "\n");
    }

    String getTime() {
        return df.format(new Date(System.currentTimeMillis()));
    }

    public void raw(String message) {
        System.out.print(message);
        fileWriter.write(message);
        fileWriter.flush();
    }

    enum Level {INFO, DEBUG, SEVERE, ERROR}

}
