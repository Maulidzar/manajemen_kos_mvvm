package com.example.kosmvvm.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "kamar",
    foreignKeys = [
        ForeignKey(
            entity = PenghuniEntity::class,
            parentColumns = ["idPenghuni"],
            childColumns = ["idPenghuni"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)

data class KamarEntity(
    @PrimaryKey(autoGenerate = true) val idKamar: Int = 0,
    val nomorKamar: String,
    var statusTerisi: Boolean,
    var statusBayar: Boolean?,
    var tanggalMasuk: String?,
    var tanggalBayar: Int?,
    var statusMaintenance: String?,
    var idPenghuni: Int? = null
) : Parcelable