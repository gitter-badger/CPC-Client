package io.cpc.client.commands.impl;

import io.cpc.client.Application;
import io.cpc.client.commands.Command;

public class QuitCommand implements Command {

    @Override
    public void execute(String[] args) {
        Application.quit(0);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"quit", "q"};
    }

    @Override
    public String getDescription() {
        return "Exit the application";
    }
}
