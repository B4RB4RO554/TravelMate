package tn.bidpaifusion.travelmatekotlin.data.local.dao

import androidx.room.*
import tn.bidpaifusion.travelmatekotlin.data.local.entity.EmergencyNumberEntity

@Dao
interface EmergencyNumberDao {
    @Query("SELECT * FROM emergency_numbers WHERE country = :country LIMIT 1")
    suspend fun getEmergencyNumbersByCountry(country: String): EmergencyNumberEntity?

    @Query("SELECT * FROM emergency_numbers")
    suspend fun getAllEmergencyNumbers(): List<EmergencyNumberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyNumber(emergencyNumber: EmergencyNumberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyNumbers(emergencyNumbers: List<EmergencyNumberEntity>)

    @Query("DELETE FROM emergency_numbers")
    suspend fun deleteAllEmergencyNumbers()
}
