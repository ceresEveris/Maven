package com.example.marvelcharacters.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.marvelcharacters.databinding.FragmentHomeBinding
import com.example.marvelcharacters.ui.HomeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint

class HomeFragment : Fragment() {
    private val adapter = HomeAdapter()
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.characterList.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCharters()

    }

    private fun getCharters() {
        var chartersJob: Job? = null

        chartersJob?.cancel()
        binding.progressBar.visibility = View.VISIBLE
        chartersJob = lifecycleScope.launch {
            viewModel.getListCharacters()?.collectLatest {
                adapter.submitData(it)
            }
        }
        while(chartersJob.isCancelled) {
            Toast.makeText(context, "asdasdasdasdasdasd", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}