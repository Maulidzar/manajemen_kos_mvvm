package com.example.kosmvvm.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kosmvvm.databinding.ActivityEditKamarBinding
import com.example.kosmvvm.model.KamarEntity
import com.example.kosmvvm.model.KamarWithPenghuni
import com.example.kosmvvm.model.PenghuniEntity
import com.example.kosmvvm.viewmodel.EditKamarViewModel
import com.example.kosmvvm.viewmodel.SaveResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class EditKamarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditKamarBinding
    private val editKamarViewModel: EditKamarViewModel by viewModels()
    private var selectedTanggalMasuk: String? = null
    private var selectedHariJatuhTempo: Int? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    private var currentKamarData: KamarWithPenghuni? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditKamarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idKamar = intent.getIntExtra("id_kamar", -1)

        if (idKamar == -1) {
            isEditMode = false
            binding.tvNamaKamar.text = "Tambah Kamar Baru"
            binding.etNomorKamar.isEnabled = true
            binding.cbTerisi.visibility = View.GONE
            binding.cbBayar.visibility = View.GONE
        } else {
            isEditMode = true
            editKamarViewModel.loadKamarById(idKamar)
            binding.cbTerisi.visibility = View.VISIBLE
            binding.cbBayar.visibility = View.VISIBLE
            binding.etNomorKamar.visibility = View.GONE
        }

        editKamarViewModel.kamarWithPenghuni.observe(this) { data ->
            currentKamarData = data
            if (data != null) {
                binding.tvNamaKamar.text = "Kamar ${data.kamar.nomorKamar}"
                binding.etNomorKamar.setText(data.kamar.nomorKamar)
                binding.cbTerisi.isChecked = data.kamar.statusTerisi
                binding.cbBayar.isChecked = data.kamar.statusBayar == true
                selectedTanggalMasuk = data.kamar.tanggalMasuk
                selectedHariJatuhTempo = data.kamar.tanggalBayar
                updateTanggalDisplay()
                binding.etMaintenance.setText(data.kamar.statusMaintenance ?: "")
                binding.etNamaPenghuni.setText(data.penghuni?.namaPenghuni ?: "")
                binding.etNikPenghuni.setText(data.penghuni?.nikPenghuni ?: "")
                binding.etAlamatAsal.setText(data.penghuni?.alamatAsal ?: "")
            }
        }

        editKamarViewModel.saveStatus.observe(this) { result ->
            when (result) {
                is SaveResult.Success -> {
                    Toast.makeText(this, "Data Kamar Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is SaveResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnTanggalBayar.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            showSaveConfirmationDialog()
        }
    }

    private fun showSaveConfirmationDialog() {
        val dialogTitle = if (isEditMode) "Edit Kamar" else "Tambah Kamar"
        val dialogMessage = "Apakah Anda yakin data yang dimasukkan sudah benar?"

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Simpan") { _, _ ->
                saveChanges()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun saveChanges() {
        val data = currentKamarData
        val nomorKamar: String
        val isTerisi: Boolean
        val statusBayar: Boolean?

        if (isEditMode) {
            nomorKamar = data!!.kamar.nomorKamar
            isTerisi = binding.cbTerisi.isChecked
            statusBayar = if (isTerisi) binding.cbBayar.isChecked else null
        } else {
            nomorKamar = binding.etNomorKamar.text.toString()
            isTerisi = true
            statusBayar = true
        }

        val namaPenghuni = binding.etNamaPenghuni.text.toString()
        val nikPenghuni = binding.etNikPenghuni.text.toString()
        val alamatAsal = binding.etAlamatAsal.text.toString()
        val hariJatuhTempo = selectedHariJatuhTempo
        val tanggalMasuk = selectedTanggalMasuk
        val statusMaintenance = binding.etMaintenance.text.toString().takeIf { it.isNotBlank() }

        if (nomorKamar.isBlank()) {
            Toast.makeText(this, "Nomor kamar wajib diisi!", Toast.LENGTH_LONG).show()
            return
        }

        if (isTerisi) {
            if (namaPenghuni.isBlank() || nikPenghuni.isBlank() || alamatAsal.isBlank()) {
                Toast.makeText(this, "Isi data penghuni terlebih dahulu!", Toast.LENGTH_LONG).show()
                return
            }
            if (tanggalMasuk.isNullOrBlank() || hariJatuhTempo == null) {
                Toast.makeText(this, "Tentukan tanggal terlebih dahulu!", Toast.LENGTH_LONG).show()
                return
            }
        }

        val penghuni = PenghuniEntity(
            idPenghuni = data?.penghuni?.idPenghuni ?: 0,
            namaPenghuni = namaPenghuni,
            nikPenghuni = nikPenghuni,
            alamatAsal = alamatAsal
        )

        val kamar = KamarEntity(
            idKamar = data?.kamar?.idKamar ?: 0,
            nomorKamar = nomorKamar,
            statusTerisi = isTerisi,
            statusBayar = statusBayar,
            tanggalMasuk = if (isTerisi) tanggalMasuk else null,
            tanggalBayar = if (isTerisi) hariJatuhTempo else null,
            statusMaintenance = statusMaintenance,
            idPenghuni = data?.kamar?.idPenghuni
        )

        editKamarViewModel.updateKamar(kamar, penghuni)
    }

    private fun showDatePicker() {
        val kalender = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                selectedTanggalMasuk = selectedDate.format(dateFormatter)
                selectedHariJatuhTempo = selectedDate.dayOfMonth
                updateTanggalDisplay()
            },
            kalender.get(Calendar.YEAR),
            kalender.get(Calendar.MONTH),
            kalender.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateTanggalDisplay() {
        binding.tvTanggalBayar.text = selectedTanggalMasuk ?: "tanggal masuk belum dipilih"
    }
}