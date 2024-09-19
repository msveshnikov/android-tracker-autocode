package com.maxsoft.stepstracker.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "steps")
public class Step {
  @PrimaryKey(autoGenerate = true)
  private int id;

  @ColumnInfo(name = "date")
  private long date;

  @ColumnInfo(name = "count")
  private int count;

  @ColumnInfo(name = "calories")
  private float calories;

  @ColumnInfo(name = "distance")
  private float distance;

  @ColumnInfo(name = "duration")
  private long duration;

  public Step(long date, int count, float calories, float distance, long duration) {
    this.date = date;
    this.count = count;
    this.calories = calories;
    this.distance = distance;
    this.duration = duration;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public float getCalories() {
    return calories;
  }

  public void setCalories(float calories) {
    this.calories = calories;
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }
}