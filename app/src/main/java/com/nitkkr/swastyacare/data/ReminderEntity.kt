package com.nitkkr.swastyacare.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReminderEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val medicineName: String,
    val time: String

)