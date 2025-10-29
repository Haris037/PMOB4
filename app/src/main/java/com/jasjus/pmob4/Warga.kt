package com.jasjus.pmob4

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Warga(
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id")
    var id: Int=0,

    @ColumnInfo(name = "nama")
    var nama: String? = null,

    @ColumnInfo(name = "NIK")
    var nik: String? = null,

    @ColumnInfo(name = "kabupaten")
    var kabupaten: String? = null,

    @ColumnInfo(name = "kecamatan")
    var kecamatan: String? = null,

    @ColumnInfo(name = "desa")
    var desa: String? = null,

    @ColumnInfo(name = "rt")
    var rt: String? = null,

    @ColumnInfo(name = "rw")
    var rw: String? = null,

    @ColumnInfo(name = "jenisKelamin")
    var jenisKelamin: String? = null,

    @ColumnInfo(name = "status")
    var status: String? = null,
    )
