package com.asiawaters.fta.classes;

public class Model_TaskListFields {
    private String key;
    private String value;

    @Override
    public String toString() {
        return "Model_TaskListFields{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
