package com.example.kosmvvm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class RoomAdapter(
    private val onRoomClick: (RoomEntity) -> Unit,
    private val onDeleteClick: (RoomEntity) -> Unit
) : ListAdapter<RoomEntity, RoomAdapter.RoomViewHolder>(RoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRoomName: TextView = itemView.findViewById(R.id.tvRoomName)
        private val tvOccupancy: TextView = itemView.findViewById(R.id.tvOccupancy)
        private val tvPayment: TextView = itemView.findViewById(R.id.tvPayment)
        private val tvPaymentDate: TextView = itemView.findViewById(R.id.tvPaymentDate)
        private val tvMaintenance: TextView = itemView.findViewById(R.id.tvMaintenance)
        private val btnDeleteRoom: Button = itemView.findViewById(R.id.btnDeleteRoom)

        fun bind(room: RoomEntity) {
            tvRoomName.text = room.name

            // Set occupancy status
            if (room.isOccupied) {
                tvOccupancy.text = "Status: Occupied"
                tvOccupancy.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            } else {
                tvOccupancy.text = "Status: Available"
                tvOccupancy.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            }

            // Set payment status
            if (room.isOccupied) {
                if (room.isPaid == true) {
                    tvPayment.text = "Payment: Paid"
                    tvPayment.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                } else {
                    tvPayment.text = "Payment: Unpaid"
                    tvPayment.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                }
            } else {
                tvPayment.text = "Payment: N/A"
                tvPayment.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
            }

            // Set payment date
            if (room.isOccupied && room.isPaid == true && !room.lastPaymentDate.isNullOrBlank()) {
                tvPaymentDate.text = "Last Payment: ${room.lastPaymentDate}"
            } else {
                tvPaymentDate.text = "Last Payment: N/A"
            }

            // Set maintenance
            if (!room.maintenanceNeeds.isNullOrBlank()) {
                tvMaintenance.text = "Maintenance: ${room.maintenanceNeeds}"
                tvMaintenance.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
            } else {
                tvMaintenance.text = "Maintenance: None"
                tvMaintenance.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
            }

            // Set click listeners
            itemView.setOnClickListener {
                onRoomClick(room)
            }

            btnDeleteRoom.setOnClickListener {
                onDeleteClick(room)
            }
        }
    }

    class RoomDiffCallback : DiffUtil.ItemCallback<RoomEntity>() {
        override fun areItemsTheSame(oldItem: RoomEntity, newItem: RoomEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RoomEntity, newItem: RoomEntity): Boolean {
            return oldItem == newItem
        }
    }
} 