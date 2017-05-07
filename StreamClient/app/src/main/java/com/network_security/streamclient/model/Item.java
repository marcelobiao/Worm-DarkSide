package com.network_security.streamclient.model;

import android.support.annotation.DrawableRes;

import java.io.File;

/**
 * Created by João Paulo on 06/05/2017.
 */

public class Item {
    private File file;
    private String name;
    @DrawableRes private int drawable;

    public Item(@DrawableRes int drawable, File location) {
        this.name = location.getName();
        this.drawable = drawable;
        this.file = location;
    }

    public File getFile() {
        return file;
    }

    public int getDrawable() {
        return drawable;
    }

    public String getName() {
        return name;
    }
}
