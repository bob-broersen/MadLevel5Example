package com.broersen.madlevel5example.dao

import androidx.room.*
import com.broersen.madlevel5example.model.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminderTable")
    suspend fun getAllReminder():List<Reminder>

    @Insert
    suspend fun addReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)
}