package com.example.skytag.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("mensaje") val mensaje: String = "",
    @SerializedName("usuario")val usuario: String = "",
    @SerializedName("telefono")val telefono: String = "",
    @SerializedName("tagkey")val  tagkey: String = "",
    @SerializedName("codigo")val  codigo: String = "",
    @SerializedName("detail")val  detail: String = "",
    @SerializedName("fecha")val   fecha: String = "",
    @SerializedName("latitud")val latitud: Double = 0.0,
    @SerializedName("longitud")val longitud: Double = 0.0
)
