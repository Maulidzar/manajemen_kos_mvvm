package com.example.kosmvvm

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat
import java.util.*
/*import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch*/

class RoomDetailActivity : AppCompatActivity() {
    private val roomDetailViewModel: RoomDetailViewModel by viewModels()
    private var selectedPaymentDate: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var currentRoom: RoomEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_detail)

        val tvRoomName = findViewById<TextView>(R.id.tvRoomName)
        val cbOccupied = findViewById<CheckBox>(R.id.cbOccupied)
        val cbPaid = findViewById<CheckBox>(R.id.cbPaid)
        val btnPaymentDate = findViewById<Button>(R.id.btnPaymentDate)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        val etMaintenance = findViewById<EditText>(R.id.etMaintenance)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Get room ID and admin ID from Intent
        val roomId = intent.getStringExtra("room_id")
        val adminId = intent.getStringExtra("admin_id")
        
        if (roomId != null && adminId != null) {
            Log.d("RoomDetailActivity", "Loading room with ID: $roomId, adminId: $adminId")
            roomDetailViewModel.loadRoom(roomId, adminId)
        }

        roomDetailViewModel.room.observe(this, Observer { room ->
            currentRoom = room
            tvRoomName.text = room.name
            cbOccupied.isChecked = room.isOccupied
            cbPaid.isChecked = room.isPaid == true
            selectedPaymentDate = room.lastPaymentDate
            updateDateDisplay(tvSelectedDate)
            etMaintenance.setText(room.maintenanceNeeds ?: "")
            Log.d("RoomDetailActivity", "Room loaded: $room")
        })

        btnPaymentDate.setOnClickListener {
            showDatePicker(tvSelectedDate)
        }

        btnSave.setOnClickListener {
            val room = currentRoom
            if (room != null) {
                // Create updated room with preserved roomKey
                val updatedRoom = room.copy(
                    isOccupied = cbOccupied.isChecked,
                    isPaid = if (cbOccupied.isChecked) cbPaid.isChecked else null,
                    lastPaymentDate = if (cbOccupied.isChecked && cbPaid.isChecked && !selectedPaymentDate.isNullOrBlank()) selectedPaymentDate else null,
                    maintenanceNeeds = etMaintenance.text.toString().takeIf { it.isNotBlank() }
                )
                
                Log.d("RoomDetailActivity", "Saving updated room: $updatedRoom")
                val resultIntent = Intent()
                resultIntent.putExtra("updated_room", updatedRoom)
                setResult(RESULT_OK, resultIntent)
            }
            
            Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDatePicker(tvSelectedDate: TextView) {
        val calendar = Calendar.getInstance()
        
        // If we have a selected date, parse it to set the initial date
        if (!selectedPaymentDate.isNullOrBlank()) {
            try {
                val date = dateFormat.parse(selectedPaymentDate)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                // If parsing fails, use current date
            }
        }

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedPaymentDate = dateFormat.format(selectedCalendar.time)
                updateDateDisplay(tvSelectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.show()
    }

    private fun updateDateDisplay(tvSelectedDate: TextView) {
        if (!selectedPaymentDate.isNullOrBlank()) {
            tvSelectedDate.text = "Selected: $selectedPaymentDate"
        } else {
            tvSelectedDate.text = "No date selected"
        }
    }
} 