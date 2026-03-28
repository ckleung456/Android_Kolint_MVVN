package com.example.myapplication.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.example.core.utils.setGone
import com.example.core.utils.setVisible
import com.example.myapplication.databinding.FragmentCountriesBinding
import com.example.myapplication.model.domain.CountryUiState
import com.example.myapplication.ui.adapter.CountryAdapter
import com.example.myapplication.viewmodel.CountriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class CountriesFragment : Fragment() {
    companion object {
        val TAG: String = CountriesFragment::class.java.name
        private const val SCROLL_ANIMATION = 250L
    }

    private val viewModel: CountriesViewModel by viewModels()
    private var binding: FragmentCountriesBinding? = null
    private var lastPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountriesBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            rlCountries.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )
                adapter = CountryAdapter {
                    CountriesFragmentDirections.actionCountriesToCountryDetail(
                        countryDetail = it
                    ).also { action ->
                        findNavController().navigate(action)
                    }
                }
                val weakRefOfThis = WeakReference(this@CountriesFragment)
                addOnScrollListener(object: OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        when (newState) {
                            SCROLL_STATE_IDLE -> {
                                recyclerView.layoutManager?.let {
                                    if (it is LinearLayoutManager) {
                                        weakRefOfThis.get()?.lastPosition = it.findLastCompletelyVisibleItemPosition()
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                })
            }

            search.clearFocus()
            search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrBlank()) {
                        val adapter = rlCountries.adapter as CountryAdapter
                        adapter.filter.filter(query)
                    }

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!newText.isNullOrBlank()) {
                        val adapter = rlCountries.adapter as CountryAdapter
                        adapter.filter.filter(newText)
                    }
                    return true
                }
            })

            search.setOnCloseListener {
                val adapter = rlCountries.adapter as CountryAdapter
                adapter.filter.filter("")
                true
            }

            lifecycleScope.launch {
                viewLifecycleOwner.let {
                    viewModel.uiState.flowWithLifecycle(
                        lifecycle = viewLifecycleOwner.lifecycle,
                        minActiveState = androidx.lifecycle.Lifecycle.State.STARTED
                    ).collectLatest {
                        handleUiState(uiState = it)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveLastPosition(position = lastPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding?.rlCountries?.apply {
            clearOnScrollListeners()
            adapter = null
        }
        binding = null
        super.onDestroyView()
    }

    private fun handleUiState(uiState: CountryUiState) {
        binding?.apply {
            if (uiState == CountryUiState.Loading) {
                pbLoading.setVisible()
            } else {
                pbLoading.setGone()
            }
            when (uiState) {
                is CountryUiState.Failure -> activity?.let {
                    AlertDialog.Builder(it)
                        .setMessage(uiState.errorResId)
                        .create()
                        .show()
                }
                is CountryUiState.Success -> {
                    val adapter = rlCountries.adapter
                    if (adapter is CountryAdapter) {
                        adapter.items = uiState.countries
                    }
                    lastPosition = uiState.position
                    val weakRefRecyclerView = WeakReference(rlCountries)
                    rlCountries.postDelayed({
                        weakRefRecyclerView.get()?.scrollToPosition(uiState.position)
                    }, SCROLL_ANIMATION)
                }
                else -> {}
            }
        }
    }
}