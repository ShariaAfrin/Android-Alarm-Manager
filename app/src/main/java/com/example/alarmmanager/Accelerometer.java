package com.example.alarmmanager;

import android.annotation.SuppressLint;
import android.util.Log;
import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import java.text.DecimalFormat;


public class Accelerometer extends Service {
    public float z;

    DecimalFormat decimalformat = new DecimalFormat("#.##");
    private StringBuilder stringBuilder;
    public static final int REQUEST_CODE=101;

    public Accelerometer() {
    }

    @SuppressLint("ShortAlarm")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i("Alarm setup", "Current system time = " + System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis() + 10000, 10000, pendingIntent);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stringBuilder = new StringBuilder();

        sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        return START_STICKY;
    }

    public final SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            String sx = decimalformat.format(x);
            String sy = decimalformat.format(y);
            String sz = decimalformat.format(z);
            final String accelerometerValues = sx + "," + sy + "," + sz;
            stringBuilder.append(accelerometerValues);
            stringBuilder.append("\n");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent restartService = new Intent("RestartService");
        sendBroadcast(restartService);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
