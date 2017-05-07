package com.network_security.streamclient.transport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SendPackage implements Serializable {
    private static final long serialVersionUID = -8550762267102734409L;
    private int code;
    private List<Serializable> items;

    public SendPackage(int code) {
        this.code = code;
        items = new ArrayList<>();
    }

    public void addItem(Serializable item) {
        items.add(item);
    }

    public List<Serializable> getItems() {
        return items;
    }

    public int getCode() {
        return code;
    }
}