package com.github.bestk.ra.model;

import java.util.Properties;

public class CommandResult {

    public CommandResult(RemoteCommand command, Properties result) {
        this.command = command;
        this.result = result;
    }

    public CommandResult() {

    }

    private RemoteCommand command;
    private Properties result;

    public RemoteCommand getCommand() {
        return command;
    }

    public void setCommand(RemoteCommand command) {
        this.command = command;
    }

    public Properties getResult() {
        return result;
    }

    public void setResult(Properties result) {
        this.result = result;
    }

}
