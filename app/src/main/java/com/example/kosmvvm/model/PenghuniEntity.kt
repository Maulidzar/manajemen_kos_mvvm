package com.example.kosmvvm.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "penghuni",
)

data class PenghuniEntity(
    @PrimaryKey(autoGenerate = true) val idPenghuni: Int = 0,
    var namaPenghuni: String,
    var nikPenghuni: String,
    var alamatAsal: String
) : Parcelable