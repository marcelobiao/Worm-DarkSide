package com.network_security.streamclient.mock;

import android.os.Environment;

import com.network_security.streamclient.R;
import com.network_security.streamclient.model.Item;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by João Paulo on 06/05/2017.
 */

public class MockData {

    public static ArrayList<Item> generateMock() {
        String sdcard = Environment.getExternalStorageDirectory().getPath();
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item(R.drawable.ic_movie_better, new File(sdcard + "/security/movie.mkv")));
        items.add(new Item(R.drawable.ic_music, new File(sdcard + "/security/music.mp3")));
        items.add(new Item(R.drawable.ic_text, new File(sdcard + "/security/text.txt")));
        return items;
    }
}
