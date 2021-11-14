package com.example.marvelcharacters.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.marvelcharacters.R
import com.example.marvelcharacters.databinding.FragmentHomeBinding
import com.example.marvelcharacters.ui.HomeAdapter
import com.example.marvelcharacters.ui.LoadingStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint

class HomeFragment : Fragment() {
    private val adapter = HomeAdapter()
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    var chartersJob: Job? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.characterList.adapter = adapter
        setLoadingAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCharters()
        binding.apply {
            ivReload.setOnClickListener {
                ivReload.visibility = View.GONE
                getCharters()
            }
        }
    }

    private fun getCharters() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            tvMessageLoading.text = getString(R.string.message_loading)
            tvMessageLoading.visibility = View.VISIBLE
            characterList.visibility = View.GONE
        }
        chartersJob?.cancel()
        chartersJob = lifecycleScope.launch {
            viewModel.getListCharacters()?.collectLatest {
                adapter.submitData(it)
            }
        }

    }
    private fun setLoadingAdapter() {
        binding.characterList.adapter = adapter.withLoadStateFooter(
            LoadingStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener {
            if (it.refresh is LoadState.Error) {
                binding.apply {
                    progressBar.visibility = View.GONE
                    tvMessageLoading.text = getString(R.string.message_error_connection)
                    ivReload.visibility = View.VISIBLE
                }
            }else if (it.refresh is LoadState.NotLoading) {
                binding.apply {
                    progressBar.visibility = View.GONE
                    tvMessageLoading.visibility = View.GONE
                    characterList.visibility = View.VISIBLE
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}