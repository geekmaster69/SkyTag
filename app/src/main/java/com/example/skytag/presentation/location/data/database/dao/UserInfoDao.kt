package com.example.skytag.presentation.location.data.database.dao

import androidx.room.*
import com.example.skytag.presentation.location.data.database.dao.entities.UserInfoEntity

@Dao
interface UserInfoDao {

    @Query("SELECT * FROM user_info_entity")
    fun getAllData() :UserInfoEntity

    @Insert
    fun addUserINfo(userInfoEntity: UserInfoEntity)

    @Update
    fun updateUserInfo(userInfoEntity: UserInfoEntity)

    @Query("DELETE FROM user_info_entity")
    fun deleteUserInfo()
}