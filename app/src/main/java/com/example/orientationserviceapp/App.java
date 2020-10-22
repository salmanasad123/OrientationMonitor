package com.example.orientationserviceapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class App extends Application {

    public static final String CHANNEL_ID = "serviceChannel";
    public static Socket mSocket;
    public static int port;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MTAG", "onCreate: Application class");
        createNotificationChannel();
        //initSocketIO();
        //startService(new Intent(this, OrientationServiceTest.class));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void initSocketIO() {
        if (mSocket == null) {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionAttempts = 1000;
            options.reconnectionDelay = 1000;
            try {
                mSocket = IO.socket(getApplicationContext().getString(R.string.socket_url), options);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.d("MTAG", "Failed to connect to Server");
            }
            addSocketEventListener();
            mSocket.connect();
        } else {
            if (!mSocket.connected()) {
                mSocket.connect();
            }
        }
    }

    private void addSocketEventListener() {
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("MTAG", "call: Connected to Server" + mSocket.id());
            }
        });
        mSocket.on("orientationEvent", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("MTAG", "call: Event received" + mSocket.id());
            }
        });
    }
}
