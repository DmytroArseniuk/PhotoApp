package com.arsars.photoapp.photos.list

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.arsars.photoapp.ServiceLocator
import com.arsars.photoapp.base.SimpleViewModelFactory
import com.arsars.photoapp.databinding.FragmentPhotosBinding
import com.arsars.photoapp.photos.list.usecases.CreateTempPhotoContainer
import kotlinx.coroutines.Dispatchers

class PhotosFragment : Fragment() {

    private val viewModel: PhotosViewModel by viewModels(factoryProducer = {
        SimpleViewModelFactory {
            PhotosViewModel(
                CreateTempPhotoContainer(ServiceLocator.fileUtil),
                ServiceLocator.photosRepository,
                Dispatchers.IO
            )
        }
    })
    private var binding: FragmentPhotosBinding? = null
    private var adapter = PhotosAdapter(ServiceLocator.photoLoader) { photoId ->
        binding?.apply {
            Navigation.findNavController(root)
                .navigate(
                    PhotosFragmentDirections.actionPhotosFragmentToPhotoDetailsFragment(
                        photoId.toString()
                    )
                )
        }
    }

    private val takePicture = registerForActivityResult(TakePicture()) {
        viewModel.photoTaken(it)
    }

    private val cameraPermissions = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            takePicture.launch(viewModel.createPhotoUri(requireActivity()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotosBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.load()
        binding?.apply {
            photos.adapter = adapter

            takePhotoButton.setOnClickListener {
                takePhoto()
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.state.collect {
                adapter.differ.submitList(it.list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun takePhoto() {
        //Won't work if user denies permission several times.
        // Need to show a message to enable permission in settings.
        cameraPermissions.launch(CAMERA)
    }
}