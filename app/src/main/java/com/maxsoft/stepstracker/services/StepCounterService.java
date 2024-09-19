package com.maxsoft.stepstracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.maxsoft.stepstracker.MainActivity;
import com.maxsoft.stepstracker.R;
import com.maxsoft.stepstracker.StepsTrackerApp;
import com.maxsoft.stepstracker.data.StepDao;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StepCounterService extends Service implements SensorEventListener {

  private static final String CHANNEL_ID = "StepCounterChannel";
  private static final int NOTIFICATION_ID = 1;

  private SensorManager sensorManager;
  private Sensor stepSensor;
  private int stepCount = 0;
  private StepDao stepDao;
  private ExecutorService executorService;

  @Override
  public void onCreate() {
    super.onCreate();
    StepsTrackerApp app = (StepsTrackerApp) getApplication();
    sensorManager = app.getSensorManager();
    stepSensor = app.getStepSensor();
    stepDao = app.getStepDao();
    executorService = Executors.newSingleThreadExecutor();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    createNotificationChannel();
    startForeground(NOTIFICATION_ID, createNotification());
    registerStepSensor();
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterStepSensor();
    executorService.shutdown();
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
      int newStepCount = (int) event.values[0];
      int stepsDelta = newStepCount - stepCount;
      stepCount = newStepCount;
      updateStepCount(stepsDelta);
      updateNotification();
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  private void registerStepSensor() {
    if (stepSensor != null) {
      sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  private void unregisterStepSensor() {
    sensorManager.unregisterListener(this);
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel =
          new NotificationChannel(
              CHANNEL_ID, "Step Counter", NotificationManager.IMPORTANCE_DEFAULT);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private Notification createNotification() {
    Intent notificationIntent = new Intent(this, MainActivity.class);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

    return new NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Step Counter")
        .setContentText("Steps: " + stepCount)
        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
        .setContentIntent(pendingIntent)
        .build();
  }

  private void updateNotification() {
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(NOTIFICATION_ID, createNotification());
  }

  private void updateStepCount(int stepsDelta) {
    executorService.execute(
        () -> {
          long timestamp = System.currentTimeMillis();
          stepDao.insertSteps(timestamp, stepsDelta);
        });
  }

  public void startTracking() {
    Intent intent = new Intent(this, StepCounterService.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(intent);
    } else {
      startService(intent);
    }
  }
}