package io.cpc.client.commands;


public class AliasAlreadyTakenException extends RuntimeException {
    public AliasAlreadyTakenException(Command command) {
        super("An alias from command " + command + " is already taken");
    }
}
