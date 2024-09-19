package com.maxsoft.stepstracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StepDao {

  @Insert
  void insert(Step step);

  @Update
  void update(Step step);

  @Query("SELECT * FROM steps ORDER BY date DESC")
  LiveData<List<Step>> getAllSteps();

  @Query("SELECT * FROM steps WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
  LiveData<List<Step>> getStepsBetweenDates(long startDate, long endDate);

  @Query("SELECT SUM(count) FROM steps WHERE date >= :startDate AND date <= :endDate")
  LiveData<Integer> getTotalStepsBetweenDates(long startDate, long endDate);

  @Query("SELECT * FROM steps WHERE date = :date LIMIT 1")
  LiveData<Step> getStepsByDate(long date);

  @Query("DELETE FROM steps")
  void deleteAllSteps();

  @Query("SELECT SUM(count) FROM steps WHERE date = :date")
  LiveData<Integer> getTotalStepsForDate(long date);

  @Query("SELECT AVG(count) FROM steps WHERE date >= :startDate AND date <= :endDate")
  LiveData<Float> getAverageStepsBetweenDates(long startDate, long endDate);

  @Query("SELECT MAX(count) FROM steps WHERE date >= :startDate AND date <= :endDate")
  LiveData<Integer> getMaxStepsBetweenDates(long startDate, long endDate);

  void insertSteps(long timestamp, int stepsDelta);
}