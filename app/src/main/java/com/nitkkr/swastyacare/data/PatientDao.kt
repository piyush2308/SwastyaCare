package com.nitkkr.swastyacare.data

import androidx.room.*

@Dao
interface PatientDao {

    @Insert
    suspend fun insertPatient(patient: PatientEntity)

    @Query("SELECT * FROM PatientEntity")
    suspend fun getAllPatients(): List<PatientEntity>

    @Delete
    suspend fun deletePatient(patient: PatientEntity)

    @Update
    suspend fun updatePatient(patient: PatientEntity)

}