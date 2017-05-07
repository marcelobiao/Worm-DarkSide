package com.network_security.streamclient.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.network_security.streamclient.R;
import com.network_security.streamclient.StreamApplication;
import com.network_security.streamclient.adapters.ItemsAdapter;
import com.network_security.streamclient.assync_tasks.SendExecuteTask;
import com.network_security.streamclient.mock.MockData;
import com.network_security.streamclient.model.Item;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SendActivity extends AppCompatActivity {
    @Bind(R.id.items_recycler_view)
    RecyclerView itemsRecyclerView;

    LinearLayoutManager layoutManager;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        ButterKnife.bind(this);

        setupList();
    }

    private void setupList() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemsRecyclerView.setLayoutManager(layoutManager);
        itemsRecyclerView.setHasFixedSize(true);

        itemsAdapter = new ItemsAdapter(this, MockData.generateMock());
        itemsAdapter.setOnItemClickListener(itemClickListener);
        itemsRecyclerView.setAdapter(itemsAdapter);
    }

    private ItemsAdapter.ItemClickListener itemClickListener = new ItemsAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, Item item, int position) {
            StreamApplication application = (StreamApplication) getApplication();
            String ip = application.getIp();
            int port = application.getPort();

            if (position == 1) {
                new SendExecuteTask().execute(item.getFile().getAbsolutePath(), ip, Integer.toString(port));
            }
            //TODO continue...
        }
    };
}
