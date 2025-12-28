package com.luoye.usercenter.model.enums;


public enum TeamStatusEnum {
    Public(0, "公开"),

    Private(1, "私有"),

    Protected(2, "受保护");


    private int value;
    private String text;
    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
