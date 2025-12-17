package com.example.kosmvvm.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kosmvvm.R
import com.example.kosmvvm.model.KamarWithPenghuni

class KamarAdapter(
    private val onKamarClick: (KamarWithPenghuni) -> Unit,
    private val onEditClick: (KamarWithPenghuni) -> Unit,
    private val onDeleteClick: (KamarWithPenghuni) -> Unit
) : ListAdapter<KamarWithPenghuni, KamarAdapter.KamarViewHolder>(KamarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KamarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return KamarViewHolder(view)
    }

    override fun onBindViewHolder(holder: KamarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class KamarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaKamar: TextView = itemView.findViewById(R.id.tvNamaKamar)
        private val tvStatusTerisi: TextView = itemView.findViewById(R.id.tvStatusTerisi)
        private val tvStatusBayar: TextView = itemView.findViewById(R.id.tvStatusBayar)
        private val tvTanggalBayar: TextView = itemView.findViewById(R.id.tvTanggalBayar)
        private val tvNamaPenghuni: TextView = itemView.findViewById(R.id.tvNamaPenghuni)
        private val tvNikPenghuni: TextView = itemView.findViewById(R.id.tvNikPenghuni)
        private val tvAlamatAsal: TextView = itemView.findViewById(R.id.tvAlamatAsal)
        private val tvMaintenance: TextView = itemView.findViewById(R.id.tvMaintenance)
        private val btnEditKamar: Button = itemView.findViewById(R.id.btnEditKamar)
        private val btnHapusKamar: Button = itemView.findViewById(R.id.btnHapusKamar)
        private val colorGreen = ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
        private val colorRed = ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
        private val colorGray = ContextCompat.getColor(itemView.context, android.R.color.darker_gray)
        private val colorOrange = ContextCompat.getColor(itemView.context, android.R.color.holo_orange_dark)
        private val colorWhite = ContextCompat.getColor(itemView.context, android.R.color.white)


        fun bind(data: KamarWithPenghuni) {
            val kamar = data.kamar
            val penghuni = data.penghuni
            tvNamaKamar.text = "Kamar ${kamar.nomorKamar}"
            tvNamaKamar.setTextColor(colorWhite)

            if (kamar.statusTerisi) {
                tvStatusTerisi.text = "Status: Terisi"
                tvStatusTerisi.setTextColor(colorGreen)

                if (penghuni != null) {
                    tvNamaPenghuni.text = "Nama: ${penghuni.namaPenghuni}"
                    tvNikPenghuni.text = "NIK: ${penghuni.nikPenghuni}"
                    tvAlamatAsal.text = "Alamat: ${penghuni.alamatAsal}"
                    tvTanggalBayar.text = "Tanggal Masuk: ${kamar.tanggalMasuk ?: "-"}"
                    tvNamaPenghuni.setTextColor(colorGreen)
                    tvNikPenghuni.setTextColor(colorGreen)
                    tvAlamatAsal.setTextColor(colorGreen)
                    tvTanggalBayar.setTextColor(colorGreen)
                } else {
                    tvNamaPenghuni.text = "Nama: -"
                    tvNikPenghuni.text = "NIK: -"
                    tvAlamatAsal.text = "Alamat: -"
                    tvNamaPenghuni.setTextColor(colorGray)
                    tvNikPenghuni.setTextColor(colorGray)
                    tvAlamatAsal.setTextColor(colorGray)
                    tvTanggalBayar.setTextColor(colorGray)
                }

                if (kamar.statusBayar == true) {
                    tvStatusBayar.text = "Pembayaran: Sudah Bayar"
                    tvStatusBayar.setTextColor(colorGreen)
                } else {
                    tvStatusBayar.text = "Pembayaran: Belum Bayar"
                    tvStatusBayar.setTextColor(colorRed)
                }
            } else {
                tvStatusTerisi.text = "Status: Kosong"
                tvStatusTerisi.setTextColor(colorRed)
                tvNamaPenghuni.text = "Nama: -"
                tvNikPenghuni.text = "NIK: -"
                tvAlamatAsal.text = "Alamat: -"
                tvNamaPenghuni.setTextColor(colorGray)
                tvNikPenghuni.setTextColor(colorGray)
                tvAlamatAsal.setTextColor(colorGray)
                tvStatusBayar.text = "Pembayaran: -"
                tvTanggalBayar.text = "Tanggal Masuk: -"
                tvStatusBayar.setTextColor(colorGray)
                tvTanggalBayar.setTextColor(colorGray)
            }

            if (!kamar.statusMaintenance.isNullOrBlank()) {
                tvMaintenance.text = "Maintenance: ${kamar.statusMaintenance}"
                tvMaintenance.setTextColor(colorOrange)
            } else {
                tvMaintenance.text = "Maintenance: -"
                tvMaintenance.setTextColor(colorGray)
            }

            itemView.setOnClickListener {
                onKamarClick(data)
            }

            btnEditKamar.setOnClickListener {
                onEditClick(data)
            }

            btnHapusKamar.setOnClickListener {
                onDeleteClick(data)
            }
        }
    }

    class KamarDiffCallback : DiffUtil.ItemCallback<KamarWithPenghuni>() {
        override fun areItemsTheSame(oldItem: KamarWithPenghuni, newItem: KamarWithPenghuni): Boolean {
            return oldItem.kamar.idKamar == newItem.kamar.idKamar
        }

        override fun areContentsTheSame(oldItem: KamarWithPenghuni, newItem: KamarWithPenghuni): Boolean {
            return oldItem == newItem
        }
    }
}