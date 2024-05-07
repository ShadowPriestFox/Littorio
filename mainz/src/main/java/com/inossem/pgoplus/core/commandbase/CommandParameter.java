package com.inossem.pgoplus.core.commandbase;

public class CommandParameter {
    private ParamType type;
    private String name;

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
