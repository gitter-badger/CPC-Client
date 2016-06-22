package io.cpc.client;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import io.cpc.client.commands.AliasAlreadyTakenException;
import io.cpc.client.commands.Command;
import io.cpc.client.commands.CommandNotFoundException;
import io.cpc.client.commands.impl.HelpCommand;
import io.cpc.client.commands.impl.QuitCommand;
import io.cpc.client.logging.Logger;
import io.cpc.client.plugin.CPCPlugin;
import io.cpc.client.plugin.CorePlugin;

public class CommandManager {
    private final static Logger log = new Logger(CommandManager.class);
    private static Map<CPCPlugin, List<Command>> registeredCommands = new HashMap<>();

    public static void start() {
        registerDefaults();
        Thread commandListener = new Thread(() -> {

            Scanner sc = new Scanner(System.in);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    identifyCommand(sc.nextLine());
                } catch (CommandNotFoundException ignore) {
                    log.severe("Command " + ignore.getCommand() + " not found");
                }
            }

        });
        commandListener.setName("Command Listener");
        commandListener.start();
    }

    private static void identifyCommand(String command) throws CommandNotFoundException {
        List<String> argsList = new ArrayList<>(Arrays.asList(command.split(" ")));
        String effectiveCommand = argsList.remove(0);

        boolean executed = false;
        // Search for every plugin
        for (CPCPlugin cpcPlugin : registeredCommands.keySet()) {
            // For every command
            for (Command c : registeredCommands.get(cpcPlugin)) {
                // For every alias
                for (String s : c.getAliases()) {
                    if (effectiveCommand.equalsIgnoreCase(s)) {
                        String[] args = new String[argsList.size()];
                        args = argsList.toArray(args);
                        c.execute(args);
                        log.command(c.getClass().getSimpleName());
                        executed = true;
                    }
                }
            }
        }
        if (!executed) log.info("Command " + effectiveCommand + " not found");

    }

    private static void registerDefaults() {
        final CPCPlugin core = new CorePlugin();
        registerCommand(core, new QuitCommand());
        registerCommand(core, new HelpCommand());

    }

    public static void registerCommand(CPCPlugin invoker, Command command) {
        boolean alreadyUsed = false;
        List<Command> toAdd;

        if (registeredCommands.get(invoker) != null) {
            toAdd = registeredCommands.get(invoker);
        } else {
            toAdd = new ArrayList<>();
        }
        for (CPCPlugin plugin : registeredCommands.keySet()) {
            for (Command c : registeredCommands.get(plugin)) {
                for (String x : c.getAliases()) {
                    for (String y : command.getAliases()) {
                        if (y.equalsIgnoreCase(x)) alreadyUsed = true;
                    }

                }
            }
        }
        if (alreadyUsed) throw new AliasAlreadyTakenException(command);
        toAdd.add(command);
        registeredCommands.put(invoker, toAdd);
    }

    public static void showHelp() {
        for (CPCPlugin plugin : registeredCommands.keySet()) {
            System.out.println(plugin.getClass().getSimpleName() + ":\n");
            for (Command command : registeredCommands.get(plugin)) {
                log.raw("  " + command.getClass().getSimpleName() + ":\n");
                log.raw("    Description: "+command.getDescription()+"\n");
                log.raw("    Aliases:\n");
                for (String alias : command.getAliases()) {
                    System.out.println("      - "+alias);
                }
            }
        }
    }

}
