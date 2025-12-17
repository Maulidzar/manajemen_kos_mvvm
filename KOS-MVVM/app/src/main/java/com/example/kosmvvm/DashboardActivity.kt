package com.example.kosmvvm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DashboardActivity : AppCompatActivity() {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var searchView: SearchView
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var cbNeedsMaintenance: CheckBox

    companion object {
        private const val ROOM_DETAIL_REQUEST = 1001
        const val EXTRA_ADMIN_ID = "admin_id"
        const val EXTRA_ADMIN_USERNAME = "admin_username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Get admin info from intent
        val adminId = intent.getStringExtra(EXTRA_ADMIN_ID) ?: "1"
        /*val adminUsername = intent.getStringExtra(EXTRA_ADMIN_USERNAME) ?: "Admin"*/
        
        // Set current admin session
        dashboardViewModel.setCurrentAdmin(adminId)

        val rvRooms = findViewById<RecyclerView>(R.id.rvRooms)
        val btnAddRoom = findViewById<Button>(R.id.btnAddRoom)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        searchView = findViewById(R.id.searchView)
        filterChipGroup = findViewById(R.id.filterChipGroup)
        cbNeedsMaintenance = findViewById(R.id.cbNeedsMaintenance)

        setupFilterChips()
        setupMaintenanceCheckbox()

        roomAdapter = RoomAdapter(
            onRoomClick = { room ->
                val intent = Intent(this, RoomDetailActivity::class.java)
                intent.putExtra("room_id", room.id)
                intent.putExtra("admin_id", adminId)
                startActivityForResult(intent, ROOM_DETAIL_REQUEST)
            },
            onDeleteClick = { room ->
                showDeleteConfirmationDialog(room)
            }
        )
        rvRooms.layoutManager = LinearLayoutManager(this)
        rvRooms.adapter = roomAdapter

        dashboardViewModel.rooms.observe(this, Observer { rooms ->
            roomAdapter.submitList(rooms)
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                dashboardViewModel.setSearchQuery(newText)
                return true
            }
        })

        btnAddRoom.setOnClickListener {
            dashboardViewModel.addNewRoom()
            Toast.makeText(this, "New room added!", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            dashboardViewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupFilterChips() {
        filterChipGroup.setOnCheckedChangeListener { _, _ ->
            updateCombinedFilter()
        }
    }

    private fun setupMaintenanceCheckbox() {
        cbNeedsMaintenance.setOnCheckedChangeListener { _, _ ->
            updateCombinedFilter()
        }
    }

    private fun updateCombinedFilter() {
        val selectedChip = findViewById<Chip>(filterChipGroup.checkedChipId)
        val filter = selectedChip?.text?.toString() ?: "All"
        val needsMaintenance = cbNeedsMaintenance.isChecked
        dashboardViewModel.setCombinedFilter(filter, needsMaintenance)
    }


    private fun showDeleteConfirmationDialog(room: RoomEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Room")
            .setMessage("Are you sure you want to delete ${room.name}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                dashboardViewModel.deleteRoom(room)
                Toast.makeText(this, "${room.name} deleted!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == ROOM_DETAIL_REQUEST && resultCode == Activity.RESULT_OK) {
            val updatedRoom = data?.getParcelableExtra<RoomEntity>("updated_room")
            if (updatedRoom != null) {
                dashboardViewModel.updateRoom(updatedRoom)
            }
        }
    }
}