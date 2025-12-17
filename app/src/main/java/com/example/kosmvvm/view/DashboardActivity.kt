package com.example.kosmvvm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kosmvvm.databinding.ActivityDashboardBinding
import com.example.kosmvvm.model.KamarWithPenghuni
import com.example.kosmvvm.viewmodel.DashboardViewModel
import com.google.android.material.chip.Chip

class DashboardActivity : AppCompatActivity() {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var kamarAdapter: KamarAdapter
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFilterChips()
        setupMaintenanceCheckbox()

        kamarAdapter = KamarAdapter(
            onKamarClick = { data ->
                val intent = Intent(this, EditKamarActivity::class.java)
                intent.putExtra("id_kamar", data.kamar.idKamar)
                startActivity(intent)
            },
            onEditClick = { data ->
                val intent = Intent(this, EditKamarActivity::class.java)
                intent.putExtra("id_kamar", data.kamar.idKamar)
                startActivity(intent)
            },
            onDeleteClick = { data ->
                showDeleteConfirmationDialog(data)
            }
        )

        binding.rvKamar.layoutManager = LinearLayoutManager(this)
        binding.rvKamar.adapter = kamarAdapter

        dashboardViewModel.kamarList.observe(this, Observer { kamarList ->
            if (kamarList.isNullOrEmpty()) {
                binding.rvKamar.visibility = View.GONE
                binding.tvEmptyMessage.visibility = View.VISIBLE
            } else {
                binding.rvKamar.visibility = View.VISIBLE
                binding.tvEmptyMessage.visibility = View.GONE
            }
            kamarAdapter.submitList(kamarList)
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                dashboardViewModel.setSearchQuery(newText)
                return true
            }
        })

        binding.btnTambahKamar.setOnClickListener {
            val intent = Intent(this, EditKamarActivity::class.java)
            startActivity(intent)
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedChangeListener { _, _ ->
            updateCombinedFilter()
        }
    }

    private fun setupMaintenanceCheckbox() {
        binding.cbMaintenance.setOnCheckedChangeListener { _, _ ->
            updateCombinedFilter()
        }
    }

    private fun updateCombinedFilter() {
        val selectedChip = binding.filterChipGroup.checkedChipId.let { id ->
            if (id != -1) findViewById<Chip>(id) else null
        }
        val filter = selectedChip?.text?.toString() ?: "All"
        val butuhMaintenance = binding.cbMaintenance.isChecked
        dashboardViewModel.setCombinedFilter(filter, butuhMaintenance)
    }

    private fun showDeleteConfirmationDialog(data: KamarWithPenghuni) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kamar")
            .setMessage("Apakah Anda yakin ingin menghapus kamar ${data.kamar.nomorKamar}?")
            .setPositiveButton("Hapus") { _, _ ->
                dashboardViewModel.deleteKamar(data.kamar, data.penghuni)
                Toast.makeText(this, "Kamar ${data.kamar.nomorKamar} berhasil dihapus!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}