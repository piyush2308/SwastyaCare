package com.nitkkr.swastyacare.data

import androidx.room.*

@Dao
interface ReminderDao {

    @Insert
    suspend fun insertReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM ReminderEntity")
    suspend fun getAllReminders(): List<ReminderEntity>

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

}