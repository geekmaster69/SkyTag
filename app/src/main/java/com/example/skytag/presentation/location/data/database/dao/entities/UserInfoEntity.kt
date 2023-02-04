package com.example.skytag.presentation.location.data.database.dao.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info_entity")
data class UserInfoEntity(
   @PrimaryKey(autoGenerate = true) var id: Long = 0,
   val mensaje: String = "",
   val telefono: String = "",
   val  tagkey: String = "",
   val  codigo: String = "",
   val  detail: String = "",
   val   fecha: String = "",
   val latitud: Double = 0.0,
   val longitud: Double = 0.0
)