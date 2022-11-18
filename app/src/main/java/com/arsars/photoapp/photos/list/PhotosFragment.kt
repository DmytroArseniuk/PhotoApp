package com.arsars.photoapp.photos.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.arsars.photoapp.databinding.FragmentPhotosBinding

class PhotosFragment : Fragment() {

    private val viewModel: PhotosViewModel by viewModels()
    private var binding: FragmentPhotosBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotosBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            camera.setOnClickListener {
                Navigation.findNavController(root)
                    .navigate(PhotosFragmentDirections.actionPhotosFragmentToCameraFragment())
            }
            testDetails.setOnClickListener {
                Navigation.findNavController(root)
                    .navigate(PhotosFragmentDirections.actionPhotosFragmentToPhotoDetailsFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}