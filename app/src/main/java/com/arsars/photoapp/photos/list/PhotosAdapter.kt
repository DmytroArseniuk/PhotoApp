package com.arsars.photoapp.photos.list

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arsars.photoapp.data.Photo
import com.arsars.photoapp.databinding.PhotoPreviewBinding
import java.util.*

class PhotosAdapter(private val photoClickedListener: (UUID) -> Unit) :
    RecyclerView.Adapter<PhotosAdapter.PhotoVH>() {

    private var photos: ArrayList<Photo> = ArrayList()

    fun updateList(items: List<Photo>) {
        photos.clear()
        photos.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoVH {
        val binding =
            PhotoPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoVH(binding)
    }

    override fun onBindViewHolder(holder: PhotoVH, position: Int) {
        holder.bind(photos[position], photoClickedListener)
    }

    override fun getItemCount() = photos.size

    override fun getItemId(position: Int) = photos[position].id.mostSignificantBits

    class PhotoVH(private val binding: PhotoPreviewBinding) : ViewHolder(binding.root) {

        fun bind(photo: Photo, clickListener: (UUID) -> Unit) {
            binding.preview.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    photo.decodedByteArray,
                    0,
                    photo.decodedByteArray.size
                )
            )
            binding.preview.setOnClickListener { clickListener(photo.id) }
        }
    }

}