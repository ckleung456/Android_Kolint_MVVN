package com.example.myapplication.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentCountryBinding
import com.example.myapplication.ui.adapter.CountryInfoAdapter
import com.example.myapplication.viewmodel.CountryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryFragment : Fragment() {
    companion object {
        val TAG: String = CountryFragment::class.java.name
    }

    private val viewModel: CountryViewModel by viewModels()
    private var binding: FragmentCountryBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountryBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            rlCountryContent.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = CountryInfoAdapter()
            }

            viewModel.countryInfo.observe(viewLifecycleOwner) {
                val adapter = rlCountryContent.adapter
                if (adapter is CountryInfoAdapter) {
                    adapter.items = it
                }
            }
        }
    }

    override fun onDestroyView() {
        binding?.rlCountryContent?.apply {
            adapter = null
        }
        binding = null
        super.onDestroyView()
    }
}