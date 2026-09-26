package com.example.ui.api

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.databinding.BottomSheetApiListBinding
import com.example.viewmodel.ApiListUiState
import com.example.viewmodel.ApiViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ApiListBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetApiListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ApiViewModel by activityViewModels()
    private lateinit var adapter: ApiListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetApiListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                val displayMetrics = resources.displayMetrics
                val peek = (displayMetrics.heightPixels * 0.85).toInt()
                behavior.peekHeight = peek
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                behavior.skipCollapsed = false
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ApiListAdapter { request ->
            viewModel.selectRequest(request)
            val detailSheet = ApiDetailBottomSheet.newInstance()
            detailSheet.show(parentFragmentManager, ApiDetailBottomSheet.TAG)
        }
        binding.rvApiRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApiRequests.adapter = adapter
    }

    private fun setupListeners() {
        // Close button
        binding.btnCloseSheet.setOnClickListener {
            dismiss()
        }

        // Toggle Search Bar
        binding.btnToggleSearch.setOnClickListener {
            val isCurrentlyVisible = binding.layoutSearchBar.isVisible
            binding.layoutSearchBar.isVisible = !isCurrentlyVisible
            if (!isCurrentlyVisible) {
                binding.etSearchQuery.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.etSearchQuery, InputMethodManager.SHOW_IMPLICIT)
            } else {
                binding.etSearchQuery.text?.clear()
                viewModel.setSearchQuery("")
            }
        }

        // Search text watcher - forwards to ViewModel
        binding.etSearchQuery.doAfterTextChanged { text ->
            val query = text?.toString() ?: ""
            viewModel.setSearchQuery(query)
            binding.btnClearSearch.isVisible = query.isNotEmpty()
        }

        // Clear search text
        binding.btnClearSearch.setOnClickListener {
            binding.etSearchQuery.text?.clear()
            viewModel.setSearchQuery("")
        }

        // Open Filter Bottom Sheet
        binding.btnOpenFilter.setOnClickListener {
            val filterSheet = ApiFilterBottomSheet.newInstance()
            filterSheet.show(parentFragmentManager, ApiFilterBottomSheet.TAG)
        }

        // Clear History with confirmation dialog
        binding.btnClearHistory.setOnClickListener {
            showClearHistoryDialog()
        }

        // Empty state reload button
        binding.viewEmpty.btnReloadSample.setOnClickListener {
            viewModel.reloadSampleData()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Observe UI State
                launch {
                    viewModel.uiState.collect { state ->
                        renderUiState(state)
                    }
                }

                // 2. Observe Filter
                launch {
                    viewModel.filter.collect { filter ->
                        val count = filter.activeCount
                        if (count > 0) {
                            binding.viewFilterActiveBadge.isVisible = true
                            binding.tvActiveFiltersIndicator.isVisible = true
                            binding.tvActiveFiltersIndicator.text = "$count active"
                        } else {
                            binding.viewFilterActiveBadge.isVisible = false
                            binding.tvActiveFiltersIndicator.isVisible = false
                        }
                    }
                }

                // 3. Observe search query & counts
                launch {
                    viewModel.filteredCount.collect { count ->
                        val query = viewModel.searchQuery.value
                        if (query.isNotBlank()) {
                            binding.tvSearchResultCount.isVisible = true
                            binding.tvSearchResultCount.text = "$count found"
                            binding.tvRequestCountSubtitle.text = "$count Matching Requests"
                        } else {
                            binding.tvSearchResultCount.isVisible = false
                            val total = viewModel.totalCount.value
                            binding.tvRequestCountSubtitle.text = "$total Requests"
                        }
                    }
                }

                launch {
                    viewModel.totalCount.collect { total ->
                        val query = viewModel.searchQuery.value
                        if (query.isBlank()) {
                            binding.tvRequestCountSubtitle.text = "$total Requests"
                        }
                        binding.tvLiveSessionStats.text = "Recording HTTP traffic ($total logged)"
                    }
                }
            }
        }
    }

    private fun renderUiState(state: ApiListUiState) {
        when (state) {
            is ApiListUiState.Loading -> {
                binding.viewLoading.root.isVisible = true
                binding.viewEmpty.root.isVisible = false
                binding.rvApiRequests.isVisible = false
            }
            is ApiListUiState.Empty -> {
                binding.viewLoading.root.isVisible = false
                binding.viewEmpty.root.isVisible = true
                binding.rvApiRequests.isVisible = false
                adapter.submitList(emptyList())

                val query = viewModel.searchQuery.value
                val filter = viewModel.filter.value
                if (query.isNotBlank() || filter.isActive) {
                    binding.viewEmpty.tvEmptyTitle.text = "No matching requests"
                    binding.viewEmpty.tvEmptySubtitle.text = "Try adjusting your search query or filters."
                    binding.viewEmpty.btnReloadSample.text = "Reset Filters"
                    binding.viewEmpty.btnReloadSample.setOnClickListener {
                        viewModel.clearFilters()
                    }
                } else {
                    binding.viewEmpty.tvEmptyTitle.text = getString(R.string.empty_title)
                    binding.viewEmpty.tvEmptySubtitle.text = getString(R.string.empty_subtitle)
                    binding.viewEmpty.btnReloadSample.text = getString(R.string.reload_sample_data)
                    binding.viewEmpty.btnReloadSample.setOnClickListener {
                        viewModel.reloadSampleData()
                    }
                }
            }
            is ApiListUiState.Success -> {
                binding.viewLoading.root.isVisible = false
                binding.viewEmpty.root.isVisible = false
                binding.rvApiRequests.isVisible = true
                adapter.submitList(state.requests)
            }
            is ApiListUiState.Error -> {
                binding.viewLoading.root.isVisible = false
                binding.viewEmpty.root.isVisible = true
                binding.rvApiRequests.isVisible = false
                binding.viewEmpty.tvEmptyTitle.text = "Error Loading Requests"
                binding.viewEmpty.tvEmptySubtitle.text = state.message
                binding.viewEmpty.btnReloadSample.text = "Retry"
                binding.viewEmpty.btnReloadSample.setOnClickListener {
                    viewModel.loadRequests()
                }
            }
        }
    }

    private fun showClearHistoryDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_clear_title)
            .setMessage(R.string.dialog_clear_message)
            .setPositiveButton(R.string.dialog_clear_confirm) { _, _ ->
                viewModel.clearHistory()
            }
            .setNegativeButton(R.string.dialog_clear_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ApiListBottomSheet"

        fun newInstance() = ApiListBottomSheet()
    }
}
