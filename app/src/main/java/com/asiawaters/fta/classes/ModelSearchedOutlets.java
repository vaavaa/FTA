package com.asiawaters.fta.classes;

public class ModelSearchedOutlets {
    String GUID;
    String Name;
    String Address;

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    @Override
    public String toString() {
        return Name+ "/" + Address;
    }
}
