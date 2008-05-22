package com.fcg.style3.domain;

import java.io.Serializable;

public class Log implements Serializable {
    private static final long serialVersionUID = -933549873811304721L;

    private Long id;
    private String log;

    public Log() {
        // empty constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
