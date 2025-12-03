package tn.bidpaifusion.travelmatekotlin.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import tn.bidpaifusion.travelmatekotlin.data.local.entity.POIEntity

@Dao
interface POIDao {
    @Query("SELECT * FROM pois WHERE type = :type ORDER BY rating DESC")
    fun getPOIsByType(type: String): Flow<List<POIEntity>>

    @Query("SELECT * FROM pois WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoritePOIs(): Flow<List<POIEntity>>

    @Query("SELECT * FROM pois WHERE id = :poiId")
    suspend fun getPOIById(poiId: Long): POIEntity?

    @Query("""
        SELECT * FROM pois 
        WHERE type = :type 
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY rating DESC
    """)
    fun getPOIsInArea(
        type: String,
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<POIEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOI(poi: POIEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOIs(pois: List<POIEntity>)

    @Update
    suspend fun updatePOI(poi: POIEntity)

    @Delete
    suspend fun deletePOI(poi: POIEntity)

    @Query("UPDATE pois SET isFavorite = :isFavorite WHERE id = :poiId")
    suspend fun updateFavoriteStatus(poiId: Long, isFavorite: Boolean)

    @Query("DELETE FROM pois WHERE cachedAt < :threshold")
    suspend fun deleteOldPOIs(threshold: Long)

    @Query("DELETE FROM pois")
    suspend fun deleteAllPOIs()
}
