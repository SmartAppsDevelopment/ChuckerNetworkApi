package com.example.ui.api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.databinding.BottomSheetApiFilterBinding
import com.example.model.ApiFilter
import com.example.model.HttpMethod
import com.example.model.StatusCategory
import com.example.viewmodel.ApiViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ApiFilterBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetApiFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetApiFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCloseFilter.setOnClickListener {
            dismiss()
        }

        binding.btnClearFilters.setOnClickListener {
            viewModel.clearFilters()
            dismiss()
        }

        binding.btnApplyFilters.setOnClickListener {
            applyCurrentFilters()
            dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filter.collect { currentFilter ->
                    populateFilterUi(currentFilter)
                }
            }
        }
    }

    private fun populateFilterUi(filter: ApiFilter) {
        // Methods
        binding.chipMethodGet.isChecked = filter.methods.contains(HttpMethod.GET)
        binding.chipMethodPost.isChecked = filter.methods.contains(HttpMethod.POST)
        binding.chipMethodPut.isChecked = filter.methods.contains(HttpMethod.PUT)
        binding.chipMethodPatch.isChecked = filter.methods.contains(HttpMethod.PATCH)
        binding.chipMethodDelete.isChecked = filter.methods.contains(HttpMethod.DELETE)

        // Status Categories
        binding.chipStatusSuccess.isChecked = filter.statuses.contains(StatusCategory.SUCCESS)
        binding.chipStatusRedirect.isChecked = filter.statuses.contains(StatusCategory.REDIRECT)
        binding.chipStatusClientError.isChecked = filter.statuses.contains(StatusCategory.CLIENT_ERROR)
        binding.chipStatusServerError.isChecked = filter.statuses.contains(StatusCategory.SERVER_ERROR)

        // Switches
        binding.switchHttpsOnly.isChecked = filter.httpsOnly
        binding.switchErrorsOnly.isChecked = filter.errorsOnly
    }

    private fun applyCurrentFilters() {
        val selectedMethods = mutableSetOf<HttpMethod>()
        if (binding.chipMethodGet.isChecked) selectedMethods.add(HttpMethod.GET)
        if (binding.chipMethodPost.isChecked) selectedMethods.add(HttpMethod.POST)
        if (binding.chipMethodPut.isChecked) selectedMethods.add(HttpMethod.PUT)
        if (binding.chipMethodPatch.isChecked) selectedMethods.add(HttpMethod.PATCH)
        if (binding.chipMethodDelete.isChecked) selectedMethods.add(HttpMethod.DELETE)

        val selectedStatuses = mutableSetOf<StatusCategory>()
        if (binding.chipStatusSuccess.isChecked) selectedStatuses.add(StatusCategory.SUCCESS)
        if (binding.chipStatusRedirect.isChecked) selectedStatuses.add(StatusCategory.REDIRECT)
        if (binding.chipStatusClientError.isChecked) selectedStatuses.add(StatusCategory.CLIENT_ERROR)
        if (binding.chipStatusServerError.isChecked) selectedStatuses.add(StatusCategory.SERVER_ERROR)

        val newFilter = ApiFilter(
            methods = selectedMethods,
            statuses = selectedStatuses,
            httpsOnly = binding.switchHttpsOnly.isChecked,
            errorsOnly = binding.switchErrorsOnly.isChecked
        )

        viewModel.setFilter(newFilter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ApiFilterBottomSheet"

        fun newInstance() = ApiFilterBottomSheet()
    }
}
