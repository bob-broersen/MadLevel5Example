package com.broersen.madlevel5example.repository

import android.content.Context
import com.broersen.madlevel5example.model.Reminder
import com.broersen.madlevel5example.dao.ReminderDao
import com.broersen.madlevel5example.database.ReminderRoomDatabase

class ReminderRepository(context: Context) {
    private var reminderDao: ReminderDao

    init {
        val reminderRoomDatabase = ReminderRoomDatabase.getDatabase(context)
        reminderDao = reminderRoomDatabase!!.reminderDao()
    }

    suspend fun getAllReminders() : List<Reminder>{
        return reminderDao.getAllReminder()
    }

    suspend fun updateReminder(reminder: Reminder){
        reminderDao.updateReminder(reminder)
    }

    suspend fun insertReminder(reminder: Reminder){
        reminderDao.addReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder){
        reminderDao.deleteReminder(reminder)
    }
}