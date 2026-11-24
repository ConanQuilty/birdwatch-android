package com.example.birdwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import android.net.Uri
import android.widget.ImageView

class BirdAdapter(
    private var allBirds: List<Bird>,

    private val listener: BirdListener
) : RecyclerView.Adapter<BirdAdapter.BirdViewHolder>() {

    class BirdViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)

        val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)

        val coordinatesText: TextView = itemView.findViewById(R.id.coordinatesText)

        val markImage: ImageView = itemView.findViewById(R.id.birdImage)

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

        val imageUri = bird.imageUri?.let { Uri.parse(it) }

        if (imageUri != null) {
            holder.markImage.setImageURI(imageUri)
        } else {
            holder.markImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener {
            Timber.d("Row tapped: bird id=${bird.id}")
            listener.onBirdClick(bird)
        }

        holder.itemView.setOnLongClickListener {
            Timber.d("Row long-pressed: bird id=${bird.id}")
            listener.onBirdLongClick(bird)
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