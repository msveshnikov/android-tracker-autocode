package com.maxsoft.stepstracker.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxsoft.stepstracker.Personal
import com.maxsoft.stepstracker.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationsViewModel(private val personal: Personal) : ViewModel() {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val dailyGoal = personal.dailyGoal.first()
            val currentSteps = 0 // TODO: Get actual steps from step tracking repository

            val notifications = mutableListOf<Notification>()

            if (currentSteps >= dailyGoal) {
                notifications.add(
                    Notification(
                        R.string.notification_goal_reached,
                        R.drawable.ic_notifications_black_24dp
                    )
                )
            } else {
                val remainingSteps = dailyGoal - currentSteps
                notifications.add(
                    Notification(
                        R.string.notification_steps_remaining,
                        R.drawable.ic_notifications_black_24dp,
                        remainingSteps.toString()
                    )
                )
            }

            // Add more notifications as needed

            _notifications.value = notifications
        }
    }

    fun refreshNotifications() {
        loadNotifications()
    }
}

data class Notification(
    val messageResId: Int,
    val iconResId: Int,
    val additionalInfo: String? = null
)