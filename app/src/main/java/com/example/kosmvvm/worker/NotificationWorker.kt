package com.example.kosmvvm.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kosmvvm.R
import com.example.kosmvvm.model.AppRepository
import com.example.kosmvvm.view.DashboardActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val repository = AppRepository(appContext.applicationContext as android.app.Application)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    companion object {
        const val CHANNEL_ID = "JATUH_TEMPO_CHANNEL"
        const val NOTIFICATION_ID_BASE = 1200
    }

    override suspend fun doWork(): Result {
        try {
            val daftarKamar = repository.getAllKamarWithPenghuniSync()
            val hariIni = LocalDate.now()
            val tanggalHariIni = hariIni.dayOfMonth
            val tanggalTerakhirBulanIni = hariIni.lengthOfMonth()

            daftarKamar.forEach { data ->
                val kamar = data.kamar
                val tanggalJatuhTempo = kamar.tanggalBayar

                if (kamar.statusTerisi && tanggalJatuhTempo != null) {
                    val isJatuhTempoHariIni = (tanggalHariIni == tanggalJatuhTempo)
                    val isJatuhTempoDiAkhirBulan = (tanggalJatuhTempo > tanggalTerakhirBulanIni && tanggalHariIni == tanggalTerakhirBulanIni)
                    var isHariMasuk = false
                    try {
                        if (kamar.tanggalMasuk != null) {
                            val tanggalMasuk = LocalDate.parse(kamar.tanggalMasuk, dateFormatter)
                            if (hariIni.isEqual(tanggalMasuk)) {
                                isHariMasuk = true
                            }
                        }
                    } catch (e: Exception) {
                    }

                    if (isJatuhTempoHariIni || isJatuhTempoDiAkhirBulan) {
                        if (kamar.statusBayar == true && !isHariMasuk) {
                            val pesan = "Tanggal bayar bulanan kamar ${kamar.nomorKamar} (${data.penghuni?.namaPenghuni ?: "Penghuni"}) hari ini."
                            showNotification(kamar.idKamar, "Pengingat Pembayaran Bulanan", pesan)
                            repository.updateKamarStatusBayar(kamar.idKamar, false)
                        }
                        else if (kamar.statusBayar == false) {
                            val pesan = "Kamar ${kamar.nomorKamar} (${data.penghuni?.namaPenghuni ?: "Penghuni"}) masih belum melakukan pembayaran bulanan."
                            showNotification(kamar.idKamar, "Pengingat Pembayaran Bulanan", pesan)
                        }
                    }
                    else if (kamar.statusBayar == false) {
                        val pesan = "Kamar ${kamar.nomorKamar} (${data.penghuni?.namaPenghuni ?: "Penghuni"}) masih belum melakukan pembayaran bulanan."
                        showNotification(kamar.idKamar, "Pengingat Pembayaran Bulanan", pesan)
                    }
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun showNotification(notificationId: Int, title: String, content: String) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val intent = Intent(appContext, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            appContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_notifications)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BASE + notificationId, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notifikasi Pembayaran Kos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel untuk pengingat pembayaran kos"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}