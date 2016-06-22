package io.cpc.client.commands;


public interface Command {

    void execute(String[] args);

    String[] getAliases();

    String getDescription();
}
