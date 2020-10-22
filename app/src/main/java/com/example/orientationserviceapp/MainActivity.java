package com.example.orientationserviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent() != null) {
            Log.d("MTAG", "onCreateEXTRAS: " + getIntent().getIntExtra("PORT", 9092));
            App.port = getIntent().getIntExtra("PORT", 9092);
        } 
        initSocketIO();
        startService(new Intent(this, OrientationServiceTest.class));
        finish();
    }

    private void initSocketIO() {
        if (App.mSocket == null) {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionAttempts = 1000;
            options.reconnectionDelay = 1000;
            try {
                String url = getApplicationContext().getString(R.string.socket_url);
                App.mSocket = IO.socket(url + ":" + App.port, options);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.d("MTAG", "Failed to connect to Server");
            }
            addSocketEventListener();
            App.mSocket.connect();
        } else {
            if (!App.mSocket.connected()) {
                App.mSocket.connect();
            }
        }
    }

    private void addSocketEventListener() {
        App.mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("MTAG", "call: Connected to Server " + App.mSocket.id());
            }
        });
        App.mSocket.on("orientationEvent", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("MTAG", "call: Event received " + App.mSocket.id());
            }
        });
    }
}