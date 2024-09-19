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

  @Query("INSERT OR REPLACE INTO steps (date, count) VALUES (:timestamp, COALESCE((SELECT count FROM steps WHERE date = :timestamp), 0) + :stepsDelta)")
  void insertSteps(long timestamp, int stepsDelta);

  @Query("SELECT SUM(count) FROM steps WHERE date >= :startDate AND date <= :endDate")
  LiveData<Integer> getCaloriesBurnedBetweenDates(long startDate, long endDate);

  @Query("SELECT SUM(count) * :strideLength FROM steps WHERE date >= :startDate AND date <= :endDate")
  LiveData<Float> getWalkingDistanceBetweenDates(long startDate, long endDate, float strideLength);

  @Query("SELECT COUNT(DISTINCT date) FROM steps WHERE date >= :startDate AND date <= :endDate AND count >= :goalSteps")
  LiveData<Integer> getDaysGoalAchievedBetweenDates(long startDate, long endDate, int goalSteps);

  @Query("SELECT * FROM steps WHERE count = (SELECT MAX(count) FROM steps)")
  LiveData<Step> getMostActiveDay();

  @Query("SELECT * FROM steps WHERE count = (SELECT MIN(count) FROM steps WHERE count > 0)")
  LiveData<Step> getLeastActiveDay();

  @Query("SELECT AVG(count) FROM steps WHERE strftime('%w', date / 1000, 'unixepoch') = :dayOfWeek")
  LiveData<Float> getAverageStepsForDayOfWeek(String dayOfWeek);
}