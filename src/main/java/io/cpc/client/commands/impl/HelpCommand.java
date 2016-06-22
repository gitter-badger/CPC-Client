package io.cpc.client.commands.impl;


import io.cpc.client.CommandManager;
import io.cpc.client.commands.Command;

public class HelpCommand implements Command {
    @Override
    public void execute(String[] args) {
        CommandManager.showHelp();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"help", "h"};
    }

    @Override
    public String getDescription() {
        return "Show all commands in a tree";
    }
}
