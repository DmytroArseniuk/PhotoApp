package com.arsars.photoapp.photos.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.arsars.photoapp.ServiceLocator
import com.arsars.photoapp.base.SimpleViewModelFactory
import com.arsars.photoapp.databinding.FragmentPhotoDetailsBinding
import kotlinx.coroutines.Dispatchers

class PhotoDetailsFragment : Fragment() {

    private val args: PhotoDetailsFragmentArgs by navArgs()
    private val viewModel: PhotoDetailsViewModel by viewModels(factoryProducer = {
        SimpleViewModelFactory {
            PhotoDetailsViewModel(
                ServiceLocator.photosRepository,
                ServiceLocator.photoLoader,
                Dispatchers.IO
            )
        }
    })
    private var binding: FragmentPhotoDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoDetailsBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect {
                it.bitmapDrawable?.apply {
                    binding?.photo?.setImageDrawable(this)
                }
            }
        }
        viewModel.load(args.photoId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}