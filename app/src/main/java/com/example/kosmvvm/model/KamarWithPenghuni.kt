package com.example.kosmvvm.model

import androidx.room.Embedded
import androidx.room.Relation

data class KamarWithPenghuni(
    @Embedded
    val kamar: KamarEntity,

    @Relation(
        parentColumn = "idPenghuni",
        entityColumn = "idPenghuni"
    )

    val penghuni: PenghuniEntity?
)