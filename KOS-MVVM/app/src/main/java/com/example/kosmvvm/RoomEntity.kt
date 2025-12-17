package com.example.kosmvvm

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "room",
    indices = [Index(value = ["adminId"])]
)
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) val roomKey: Int = 0,
    val id: String,
    val name: String,
    var isOccupied: Boolean,
    var isPaid: Boolean?,
    var lastPaymentDate: String?,
    var maintenanceNeeds: String?,
    val adminId: String
) : Parcelable 