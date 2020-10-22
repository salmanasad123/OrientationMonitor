package com.example.orientationserviceapp;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import jp.co.cyberagent.stf.rotationwatcher.OrientationChangeListener;
import jp.co.cyberagent.stf.rotationwatcher.RotationWatcher;

public class OrientationServiceTest extends Service implements OrientationChangeListener {

    private RotationWatcher rotationWatcher;
    private Socket mSocket;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get socket instance
        mSocket = App.mSocket;
        // initialize rotation watcher thread and set listener
//        rotationWatcher = new RotationWatcher();
//        rotationWatcher.setOnOrientationChangeListener(this);
//        rotationWatcher.start();
        // start foreground service
        startForeground();
        return START_NOT_STICKY;
    }


    private void startForeground() {
        Log.d("MTAG", "Start Foreground ");
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //rotationWatcher.interrupt();
    }

    @Override
    public void onChange(int rotation) {
        Log.d("MTAG", "Device Orientation " + rotation);
        JSONObject eventToSend = prepareEventToSend(rotation);
        mSocket.emit(AppConst.ORIENTATION_EVENT, eventToSend);
        System.out.println(rotation);
    }

    private JSONObject prepareEventToSend(int orientation) {

        JSONObject object = new JSONObject();
        try {
            object.put("orientation", orientation + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = getRotation(this);
        Log.d("MTAG", "onConfigurationChanged: " + orientation);
        JSONObject eventToSend = prepareEventToSend(orientation);
        mSocket.emit(AppConst.ORIENTATION_EVENT, eventToSend);
    }

    public int getRotation(Context context) {
        final int orientation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (orientation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            default:
                return 270;
        }
    }
}
