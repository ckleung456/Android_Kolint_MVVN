package com.example.myapplication.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.example.core.utils.observe
import com.example.core.utils.observeEvent
import com.example.core.utils.setGone
import com.example.core.utils.setVisible
import com.example.myapplication.databinding.FragmentCountriesBinding
import com.example.myapplication.ui.adapter.CountryAdapter
import com.example.myapplication.viewmodel.CountriesViewModel
import dagger.hilt.android.AndroidEntryPoint
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

            setupObserver()
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

    private fun setupObserver() {
        binding?.apply {
            viewModel.countriesWithPosition.observe(lifecycleOwner = viewLifecycleOwner) { countriesWithPosition ->
                val adapter = rlCountries.adapter
                if (adapter is CountryAdapter) {
                    adapter.items = countriesWithPosition.countries
                }
                lastPosition = countriesWithPosition.position
                val weakRefRecyclerView = WeakReference(rlCountries)
                rlCountries.postDelayed({
                    weakRefRecyclerView.get()?.scrollToPosition(countriesWithPosition.position)
                }, SCROLL_ANIMATION)
            }

            viewModel.inProgress.observeEvent(lifecycleOwner = viewLifecycleOwner) { inProgress ->
                if (inProgress) {
                    pbLoading.setVisible()
                } else {
                    pbLoading.setGone()
                }
            }

            viewModel.onError.observeEvent(lifecycleOwner = viewLifecycleOwner) { errorMessageId ->
                activity?.let {
                    AlertDialog.Builder(it)
                        .setMessage(errorMessageId)
                        .create()
                        .show()
                }
            }
        }
    }
}