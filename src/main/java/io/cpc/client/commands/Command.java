package io.cpc.client.commands;


import java.util.List;

public interface Command {

    void execute(String[] args);
    String[] getAliases();
    String getDescription();
}
