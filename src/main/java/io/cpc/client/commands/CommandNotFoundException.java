package io.cpc.client.commands;


public class CommandNotFoundException extends Exception {
    private String command;

    public CommandNotFoundException(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

}
