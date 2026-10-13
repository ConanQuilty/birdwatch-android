package com.example.birdwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BirdAdapter(
    private var allBirds: List<Bird>,
    private val onEdit: (Bird) -> Unit,
    private val onDelete: (Bird) -> Unit
) : RecyclerView.Adapter<BirdAdapter.BirdViewHolder>() {

    class BirdViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)

        val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)

        val coordinatesText: TextView = itemView.findViewById(R.id.coordinatesText)

        val editButton: Button = itemView.findViewById(R.id.editButton)

        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

    }
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BirdViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_bird,
                    parent,
                    false
                )

            return BirdViewHolder(view)
        }

    override fun onBindViewHolder(
        holder: BirdViewHolder,
        position: Int
    ) {

        val bird = allBirds[position]

        holder.nameText.text =
            "${bird.id}: ${bird.name}"

        holder.descriptionText.text =
            bird.description

        holder.coordinatesText.text =
            "X: ${bird.x}, Y: ${bird.y}"

        holder.editButton.setOnClickListener {
            onEdit(bird)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(bird)
        }
    }

    override fun getItemCount(): Int {
        return allBirds.size
    }

    fun updateBirds(newBirds: List<Bird>) {
        allBirds = newBirds
        notifyDataSetChanged()
    }

}