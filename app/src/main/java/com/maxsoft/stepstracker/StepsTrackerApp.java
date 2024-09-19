package com.maxsoft.stepstracker;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.room.Room;

import com.maxsoft.stepstracker.data.AppDatabase;
import com.maxsoft.stepstracker.data.StepDao;
import com.maxsoft.stepstracker.services.StepCounterService;


public class StepsTrackerApp extends Application {

    private AppDatabase database;
    private StepDao stepDao;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private StepCounterService stepCounterService;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDatabase();
        initializeSensors();
        startStepCounterService();
    }

    private void initializeDatabase() {
        database =
                Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "steps_tracker_db")
                        .build();
        stepDao = database.stepDao();
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    private void startStepCounterService() {
        stepCounterService = new StepCounterService();
        stepCounterService.startTracking();
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public StepDao getStepDao() {
        return stepDao;
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public Sensor getStepSensor() {
        return stepSensor;
    }
}