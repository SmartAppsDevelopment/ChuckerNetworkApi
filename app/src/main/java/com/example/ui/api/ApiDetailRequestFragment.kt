package com.example.ui.api

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.R
import com.example.databinding.ItemHeaderBinding
import com.example.databinding.PageApiDetailRequestBinding
import com.example.model.ApiRequest
import com.example.utils.JsonFormatter
import com.example.viewmodel.ApiViewModel
import kotlinx.coroutines.launch

class ApiDetailRequestFragment : Fragment() {

    private var _binding: PageApiDetailRequestBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageApiDetailRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExpandCollapse()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedRequest.collect { request ->
                    request?.let { bindRequestData(it) }
                }
            }
        }
    }

    private fun setupExpandCollapse() {
        binding.sectionGeneralHeader.setOnClickListener {
            val isVisible = binding.sectionGeneralContent.isVisible
            binding.sectionGeneralContent.isVisible = !isVisible
            binding.ivExpandGeneral.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }

        binding.sectionHeadersHeader.setOnClickListener {
            val isVisible = binding.sectionHeadersContent.isVisible
            binding.sectionHeadersContent.isVisible = !isVisible
            binding.ivExpandHeaders.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }

        binding.sectionBodyHeader.setOnClickListener {
            val isVisible = binding.sectionBodyContent.isVisible
            binding.sectionBodyContent.isVisible = !isVisible
            binding.ivExpandBody.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }
    }

    private fun bindRequestData(request: ApiRequest) {
        val context = requireContext()

        // 1. General Section
        binding.tvDetailUrl.text = request.url
        binding.tvDetailMethod.text = request.method.name

        binding.btnCopyUrl.setOnClickListener {
            copyToClipboard("URL", request.url)
        }

        // 2. Headers Section
        binding.tvHeadersCountBadge.text = "${request.requestHeaders.size} headers"
        binding.layoutHeadersList.removeAllViews()

        if (request.requestHeaders.isEmpty()) {
            val emptyHeaderBinding = ItemHeaderBinding.inflate(layoutInflater, binding.layoutHeadersList, false)
            emptyHeaderBinding.tvHeaderName.text = "None"
            emptyHeaderBinding.tvHeaderValue.text = "No request headers"
            binding.layoutHeadersList.addView(emptyHeaderBinding.root)
        } else {
            val inflater = LayoutInflater.from(context)
            request.requestHeaders.forEach { (key, value) ->
                val headerBinding = ItemHeaderBinding.inflate(inflater, binding.layoutHeadersList, false)
                headerBinding.tvHeaderName.text = key
                headerBinding.tvHeaderValue.text = value
                binding.layoutHeadersList.addView(headerBinding.root)
            }
        }

        binding.btnCopyHeaders.setOnClickListener {
            val headersText = request.requestHeaders.entries.joinToString("\n") { "${it.key}: ${it.value}" }
            copyToClipboard("Request Headers", headersText)
        }

        // 3. Request Body Section
        val formattedBody = JsonFormatter.formatJson(request.requestBody)
        if (formattedBody.isNotBlank()) {
            binding.frameCodeBlock.isVisible = true
            binding.tvRequestBodyJson.text = formattedBody
            binding.tvNoRequestBody.isVisible = false
            binding.btnCopyBody.isVisible = true

            binding.btnCopyBody.setOnClickListener {
                copyToClipboard("Request Body", formattedBody)
            }
        } else {
            binding.frameCodeBlock.isVisible = false
            binding.tvNoRequestBody.isVisible = true
            binding.btnCopyBody.isVisible = false
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
        fun newInstance() = ApiDetailRequestFragment()
    }
}
