package com.example.ui.api

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.R
import com.example.databinding.ItemHeaderBinding
import com.example.databinding.PageApiDetailResponseBinding
import com.example.model.ApiRequest
import com.example.utils.ApiExtensions
import com.example.utils.JsonFormatter
import com.example.viewmodel.ApiViewModel
import kotlinx.coroutines.launch

class ApiDetailResponseFragment : Fragment() {

    private var _binding: PageApiDetailResponseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageApiDetailResponseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExpandCollapse()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedRequest.collect { request ->
                    request?.let { bindResponseData(it) }
                }
            }
        }
    }

    private fun setupExpandCollapse() {
        binding.sectionRespHeadersHeader.setOnClickListener {
            val isVisible = binding.sectionRespHeadersContent.isVisible
            binding.sectionRespHeadersContent.isVisible = !isVisible
            binding.ivExpandRespHeaders.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }

        binding.sectionRespBodyHeader.setOnClickListener {
            val isVisible = binding.sectionRespBodyContent.isVisible
            binding.sectionRespBodyContent.isVisible = !isVisible
            binding.ivExpandRespBody.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }
    }

    private fun bindResponseData(request: ApiRequest) {
        val context = requireContext()

        // 1. Response Status
        binding.tvResponseStatusFull.text = "${request.statusCode} ${request.statusMessage}"
        if (request.isError) {
            binding.tvResponseStatusFull.setTextColor(
                ContextCompat.getColor(context, R.color.status_error_text)
            )
        } else {
            binding.tvResponseStatusFull.setTextColor(
                ContextCompat.getColor(context, R.color.status_success_text)
            )
        }
        binding.tvResponseDurationBadge.text = ApiExtensions.formatDuration(request.durationMs)

        // 2. Response Headers
        binding.tvRespHeadersCountBadge.text = "${request.responseHeaders.size} headers"
        binding.layoutRespHeadersList.removeAllViews()

        if (request.responseHeaders.isEmpty()) {
            val emptyHeaderBinding = ItemHeaderBinding.inflate(layoutInflater, binding.layoutRespHeadersList, false)
            emptyHeaderBinding.tvHeaderName.text = "None"
            emptyHeaderBinding.tvHeaderValue.text = "No response headers"
            binding.layoutRespHeadersList.addView(emptyHeaderBinding.root)
        } else {
            val inflater = LayoutInflater.from(context)
            request.responseHeaders.forEach { (key, value) ->
                val headerBinding = ItemHeaderBinding.inflate(inflater, binding.layoutRespHeadersList, false)
                headerBinding.tvHeaderName.text = key
                headerBinding.tvHeaderValue.text = value
                binding.layoutRespHeadersList.addView(headerBinding.root)
            }
        }

        binding.btnCopyRespHeaders.setOnClickListener {
            val headersText = request.responseHeaders.entries.joinToString("\n") { "${it.key}: ${it.value}" }
            copyToClipboard("Response Headers", headersText)
        }

        // 3. Response Body
        val formattedBody = JsonFormatter.formatJson(request.responseBody)
        binding.tvResponseBodySizeBadge.text = ApiExtensions.formatBytes(request.responseSize)

        if (formattedBody.isNotBlank()) {
            binding.tvResponseBodyJson.text = formattedBody
            binding.tvResponseBodyJson.isVisible = true
            binding.tvNoResponseBody.isVisible = false
            binding.btnCopyRespBody.isVisible = true

            binding.btnCopyRespBody.setOnClickListener {
                copyToClipboard("Response Body", formattedBody)
            }
        } else {
            binding.tvResponseBodyJson.isVisible = false
            binding.tvNoResponseBody.isVisible = true
            binding.btnCopyRespBody.isVisible = false
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ApiDetailResponseFragment()
    }
}
