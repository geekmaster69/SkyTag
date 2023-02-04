package com.example.skytag.base.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.skytag.presentation.location.data.database.dao.UserInfoDao
import com.example.skytag.presentation.location.data.database.dao.entities.UserInfoEntity


@Database(entities = [UserInfoEntity::class], version =1 )
abstract class UserInfoDatabase : RoomDatabase() {

    abstract fun userInfoDao(): UserInfoDao
}