package tn.bidpaifusion.travelmatekotlin.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import tn.bidpaifusion.travelmatekotlin.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("UPDATE users SET token = :token WHERE id = :userId")
    suspend fun updateToken(userId: String, token: String)

    @Query("UPDATE users SET lastSyncTime = :syncTime WHERE id = :userId")
    suspend fun updateSyncTime(userId: String, syncTime: Long)
}
