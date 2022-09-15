package com.catpudding.pudding_keep_account.utils;

public class MyIcon {
    private int source;
    private String name;
    private int type;

    public MyIcon(int source, String name, int type) {
        this.source = source;
        this.name = name;
        this.type = type;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
