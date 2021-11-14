package com.example.marvelcharacters.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.marvelcharacters.R
import com.example.marvelcharacters.data.local.models.CharactersEntity
import com.example.marvelcharacters.databinding.FragmentDetailBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val args: DetailFragmentArgs by navArgs()
    private val detailViewModel: DetailViewModel by viewModels()
    private var chartersJob: Job? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate<FragmentDetailBinding>(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        ).apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
            callback = Callback { character ->
                chartersJob?.cancel()
                chartersJob = lifecycleScope.launch {
                    if (detailViewModel.addFavourite()) {
                        Snackbar.make(root, getString(R.string.charter_added), Snackbar.LENGTH_LONG).show()
                    } else {
                        Snackbar.make(root, getString(R.string.error_ocurred), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
        binding.loadingState.ivReload.setOnClickListener {
            it.visibility = View.GONE
            getCharacter()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCharacter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCharacter() {
        binding.apply {
            loadingState.progressBar.visibility = View.VISIBLE
            loadingState.tvMessageLoading.text  = getString(R.string.message_loading)
            loadingState.tvMessageLoading.visibility = View.VISIBLE
        }
        chartersJob?.cancel()
        chartersJob = lifecycleScope.launch {
            val character = detailViewModel.getCharacter(args.characterId)
            if (character != null) {
                val characterEntity = character.firstOrNull()
                characterEntity.let { itemCharacter ->
                    binding.apply {
                        loadingState.tvMessageLoading.visibility = View.GONE
                        loadingState.progressBar.visibility = View.GONE
                        tvName.text = itemCharacter!!.name
                        Glide.with(requireContext()).load(
                            itemCharacter.thumbnail!!.path.replace(
                                "http",
                                "https"
                            ) + "." + itemCharacter.thumbnail.extension
                        ).into(ivDetailImage)
                        tvDescription.text = itemCharacter.description
                    }
                }
            } else {
                binding.apply {
                    loadingState.tvMessageLoading.text = getString(R.string.message_error_connection)
                    loadingState.tvMessageLoading.visibility = View.VISIBLE
                    loadingState.ivReload.visibility = View.VISIBLE
                    loadingState.progressBar.visibility = View.GONE
                }
            }
        }
    }

    fun interface Callback {
        fun add(character: CharactersEntity?)
    }
}