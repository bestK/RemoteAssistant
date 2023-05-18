package com.github.bestk.ra.model;

import com.github.bestk.ra.enums.CommandType;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 命令
 */
public class RemoteCommand implements Serializable {

    private CommandType commandType;
    private int x;
    private int y;
    private String viewId;
    private String pkgName;

    private String activityName;

    private String text;

    private String cronExpression;

    private LinkedList<RemoteCommand> subCommands;

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public LinkedList<RemoteCommand> getSubCommands() {
        return subCommands;
    }

    public void setSubCommands(LinkedList<RemoteCommand> subCommands) {
        this.subCommands = subCommands;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
