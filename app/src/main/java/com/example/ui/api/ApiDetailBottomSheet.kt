package com.example.ui.api

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.R
import com.example.databinding.BottomSheetApiDetailBinding
import com.example.model.ApiRequest
import com.example.utils.ApiExtensions
import com.example.viewmodel.ApiViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ApiDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetApiDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetApiDetailBinding.inflate(inflater, container, false)
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
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = false
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewPager2 and TabLayout
        val pagerAdapter = ApiDetailPagerAdapter(this)
        binding.viewPagerDetail.adapter = pagerAdapter

        val tabTitles = listOf(
            getString(R.string.tab_request),
            getString(R.string.tab_response),
            getString(R.string.tab_details)
        )

        TabLayoutMediator(binding.tabLayoutDetail, binding.viewPagerDetail) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.btnCloseDetail.setOnClickListener {
            dismiss()
        }

        // Observe selected request
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedRequest.collect { request ->
                    if (request == null) {
                        dismiss()
                    } else {
                        bindHeader(request)
                    }
                }
            }
        }
    }

    private fun bindHeader(request: ApiRequest) {
        val context = requireContext()

        // Method badge
        binding.tvDetailMethodBadge.text = request.method.name
        binding.tvDetailMethodBadge.setTextColor(ApiExtensions.getMethodTextColor(context, request.method))
        binding.tvDetailMethodBadge.backgroundTintList = ColorStateList.valueOf(
            ApiExtensions.getMethodBgColor(context, request.method)
        )

        // Endpoint & Host
        binding.tvDetailEndpoint.text = request.path
        binding.tvDetailHost.text = request.host

        // Status badge
        binding.tvDetailStatusBadge.text = "${request.statusCode} ${request.statusMessage}"
        binding.tvDetailStatusBadge.setTextColor(ApiExtensions.getStatusTextColor(context, request.statusCode))
        binding.tvDetailStatusBadge.backgroundTintList = ColorStateList.valueOf(
            ApiExtensions.getStatusBgColor(context, request.statusCode)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ApiDetailBottomSheet"

        fun newInstance() = ApiDetailBottomSheet()
    }
}
