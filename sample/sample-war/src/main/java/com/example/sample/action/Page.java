package com.example.sample.action;

public class Page {

    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String message;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String hello() {
        message = "Hello, " + name + "!";
        return "success";
    }
}
