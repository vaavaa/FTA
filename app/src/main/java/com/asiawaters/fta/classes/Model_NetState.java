package com.asiawaters.fta.classes;


import android.content.Context;

public class Model_NetState {
    private boolean isOnline = false;
    private boolean isConnected = false;
    private Context context;
private String url;


    public boolean isConnected() {
        return isConnected;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
