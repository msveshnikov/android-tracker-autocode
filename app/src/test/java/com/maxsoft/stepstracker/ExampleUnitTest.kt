package com.maxsoft.stepstracker

import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.runBlocking

class ExampleUnitTest {
    @Test
    fun testStepCounting() {
        val stepCounter = StepCounter()
        stepCounter.start()
        Thread.sleep(1000) // Simulate walking for 1 second
        stepCounter.stop()
        assertTrue(stepCounter.getStepCount() > 0)
    }

    @Test
    fun testCalorieCalculation() {
        val calorieCalculator = CalorieCalculator()
        val steps = 1000
        val weight = 70.0 // kg
        val calories = calorieCalculator.calculateCalories(steps, weight)
        assertTrue(calories > 0)
    }

    @Test
    fun testDistanceCalculation() {
        val distanceCalculator = DistanceCalculator()
        val steps = 1000
        val strideLength = 0.7 // meters
        val distance = distanceCalculator.calculateDistance(steps, strideLength)
        assertTrue(distance > 0)
    }

    @Test
    fun testDataBackup() = runBlocking {
        val dataBackup = DataBackup()
        val testData = "Test data"
        dataBackup.backup(testData)
        val restoredData = dataBackup.restore()
        assertEquals(testData, restoredData)
    }

    @Test
    fun testSensitivityAdjustment() {
        val sensitivityAdjuster = SensitivityAdjuster()
        sensitivityAdjuster.setSensitivity(0.8)
        assertEquals(0.8, sensitivityAdjuster.getSensitivity(), 0.01)
    }

    @Test
    fun testLanguageSupport() {
        val languageManager = LanguageManager()
        languageManager.setLanguage("es")
        assertEquals("es", languageManager.getCurrentLanguage())
    }
}