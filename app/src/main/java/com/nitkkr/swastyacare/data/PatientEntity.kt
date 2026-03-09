package com.nitkkr.swastyacare.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PatientEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val age: Int,
    val disease: String

)