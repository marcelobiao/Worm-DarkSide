package com.network_security.streamclient.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.network_security.streamclient.R;
import com.network_security.streamclient.StreamApplication;
import com.network_security.streamclient.assync_tasks.ServerConnectTask;

import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.server_ip_text)
    EditText ip_edit;
    @Bind(R.id.server_port_text)
    EditText port_edit;

    ProgressDialog progressDialog;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
            if (progressDialog != null) progressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public void connectToServer(View view) {
        boolean valid = true;
        String ip_text = ip_edit.getText().toString();
        String port_text = port_edit.getText().toString();

        if (ip_text.trim().isEmpty()) {
            ip_edit.setError("IP field can not be empty");
            valid = false;
        } else if (port_text.trim().isEmpty()) {
            port_edit.setError("Port field can not be empty");
            valid = false;
        }

        try {
            int port_int = Integer.parseInt(port_text);
            if (port_int <= 0)
                throw new NumberFormatException("invalid");
        } catch(NumberFormatException e) {
            port_edit.setError("Port must be a unsigned number");
            valid = false;
        }

        if (!valid)
            return;

        connect(ip_text, port_text);
    }

    private void connect(final String ip, final String port) {
        final ServerConnectTask serverConnectTask = new ServerConnectTask();
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Connecting...");
        progressDialog.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                serverConnectTask.execute(ip, port);
                try {
                    boolean result = serverConnectTask.get();

                    if (!result) {
                        Message message = handler.obtainMessage(0, "Not Connected");
                        message.sendToTarget();
                    } else {
                        ((StreamApplication)getApplication()).configureServer(ip, Integer.parseInt(port));
                        Intent connected = new Intent(MainActivity.this, SendActivity.class);
                        startActivity(connected);
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage(0, "Not Connected");
                    message.sendToTarget();
                }
            }
        }, 500);

    }
}
