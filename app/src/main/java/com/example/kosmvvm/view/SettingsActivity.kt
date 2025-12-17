package com.example.kosmvvm.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kosmvvm.databinding.ActivitySettingsBinding
import com.example.kosmvvm.model.AppDatabase
import com.example.kosmvvm.worker.NotificationWorker
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val DATABASE_NAME = "kos_database"

    private val exportDataLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    backupDatabase(uri)
                }
            }
        }

    private val importDataLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    restoreDatabase(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Pengaturan"

        binding.btnTestNotifikasi.setOnClickListener {
            val testRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(5, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(this).enqueue(testRequest)
            Toast.makeText(this, "Tes notifikasi dalam 5 detik!", Toast.LENGTH_SHORT).show()
        }

        binding.btnExportData.setOnClickListener {
            launchExportFilePicker()
        }

        binding.btnImportData.setOnClickListener {
            launchImportFilePicker()
        }
    }

    private fun launchExportFilePicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "backup_kos_${System.currentTimeMillis()}.db")
        }
        exportDataLauncher.launch(intent)
    }

    private fun backupDatabase(destinationUri: Uri) {
        val currentDbFile = getDatabasePath(DATABASE_NAME)
        if (!currentDbFile.exists()) {
            Toast.makeText(this, "Database tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            AppDatabase.forceCheckpoint(this)

            contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                currentDbFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Toast.makeText(this, "Backup berhasil disimpan!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Backup gagal: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun launchImportFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        importDataLauncher.launch(intent)
    }

    private fun restoreDatabase(sourceUri: Uri) {
        val targetDbFile = getDatabasePath(DATABASE_NAME)
        val walFile = File(targetDbFile.path + "-wal")
        val shmFile = File(targetDbFile.path + "-shm")

        try {
            AppDatabase.closeInstance()

            if (walFile.exists()) {
                walFile.delete()
            }
            if (shmFile.exists()) {
                shmFile.delete()
            }

            contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                targetDbFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Toast.makeText(this, "Data berhasil dipulihkan. Aplikasi akan ditutup.", Toast.LENGTH_LONG).show()
            finishAffinity()
            exitProcess(0)

        } catch (e: Exception) {
            Toast.makeText(this, "Restore gagal: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}