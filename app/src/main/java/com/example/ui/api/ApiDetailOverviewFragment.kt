package com.example.ui.api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.R
import com.example.databinding.PageApiDetailDetailsBinding
import com.example.model.ApiRequest
import com.example.utils.ApiExtensions
import com.example.viewmodel.ApiViewModel
import kotlinx.coroutines.launch

class ApiDetailOverviewFragment : Fragment() {

    private var _binding: PageApiDetailDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ApiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageApiDetailDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExpandCollapse()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedRequest.collect { request ->
                    request?.let { bindOverviewData(it) }
                }
            }
        }
    }

    private fun setupExpandCollapse() {
        binding.sectionDetGeneralHeader.setOnClickListener {
            val isVisible = binding.layoutDetGeneralContent.isVisible
            binding.layoutDetGeneralContent.isVisible = !isVisible
            binding.ivExpandDetGeneral.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }

        binding.sectionDetTimingHeader.setOnClickListener {
            val isVisible = binding.layoutDetTimingContent.isVisible
            binding.layoutDetTimingContent.isVisible = !isVisible
            binding.ivExpandDetTiming.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }

        binding.sectionDetNetworkHeader.setOnClickListener {
            val isVisible = binding.layoutDetNetworkContent.isVisible
            binding.layoutDetNetworkContent.isVisible = !isVisible
            binding.ivExpandDetNetwork.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }

        binding.sectionDetReqHeader.setOnClickListener {
            val isVisible = binding.layoutDetReqContent.isVisible
            binding.layoutDetReqContent.isVisible = !isVisible
            binding.ivExpandDetReq.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }

        binding.sectionDetRespHeader.setOnClickListener {
            val isVisible = binding.layoutDetRespContent.isVisible
            binding.layoutDetRespContent.isVisible = !isVisible
            binding.ivExpandDetResp.setImageResource(
                if (isVisible) R.drawable.ic_expand_more else R.drawable.ic_expand_less
            )
        }
    }

    private fun bindOverviewData(request: ApiRequest) {
        // 1. GENERAL
        binding.rowUrl.tvKey.text = "URL"
        binding.rowUrl.tvValue.text = request.url

        binding.rowMethod.tvKey.text = "Method"
        binding.rowMethod.tvValue.text = request.method.name

        binding.rowProtocol.tvKey.text = "Protocol"
        binding.rowProtocol.tvValue.text = request.protocol

        binding.rowHost.tvKey.text = "Host"
        binding.rowHost.tvValue.text = request.host

        binding.rowStatus.tvKey.text = "Status"
        binding.rowStatus.tvValue.text = "${request.statusCode} ${request.statusMessage}"

        // 2. TIMING
        binding.rowStarted.tvKey.text = "Request started"
        binding.rowStarted.tvValue.text = ApiExtensions.formatFullDateTime(request.timestamp)

        binding.rowDuration.tvKey.text = "Request duration"
        binding.rowDuration.tvValue.text = ApiExtensions.formatDuration(request.durationMs)

        binding.rowConnectionDuration.tvKey.text = "Connection duration"
        binding.rowConnectionDuration.tvValue.text = ApiExtensions.formatDuration(request.connectionDurationMs)

        binding.rowResponseDuration.tvKey.text = "Response duration"
        val respDuration = (request.durationMs - request.connectionDurationMs).coerceAtLeast(1)
        binding.rowResponseDuration.tvValue.text = ApiExtensions.formatDuration(respDuration)

        // 3. NETWORK
        binding.rowRemoteAddress.tvKey.text = "Remote address"
        binding.rowRemoteAddress.tvValue.text = request.remoteAddress

        binding.rowReqSize.tvKey.text = "Request size"
        binding.rowReqSize.tvValue.text = ApiExtensions.formatBytes(request.requestSize)

        binding.rowRespSize.tvKey.text = "Response size"
        binding.rowRespSize.tvValue.text = ApiExtensions.formatBytes(request.responseSize)

        // 4. REQUEST METRICS
        val reqContentType = request.requestHeaders.entries.firstOrNull {
            it.key.equals("content-type", ignoreCase = true)
        }?.value ?: "None"

        binding.rowReqBodySize.tvKey.text = "Body size"
        binding.rowReqBodySize.tvValue.text = ApiExtensions.formatBytes(request.requestSize)

        binding.rowReqHeadersCount.tvKey.text = "Headers count"
        binding.rowReqHeadersCount.tvValue.text = "${request.requestHeaders.size}"

        binding.rowReqContentType.tvKey.text = "Content type"
        binding.rowReqContentType.tvValue.text = reqContentType

        // 5. RESPONSE METRICS
        val respContentType = request.responseHeaders.entries.firstOrNull {
            it.key.equals("content-type", ignoreCase = true)
        }?.value ?: "None"

        binding.rowRespBodySize.tvKey.text = "Body size"
        binding.rowRespBodySize.tvValue.text = ApiExtensions.formatBytes(request.responseSize)

        binding.rowRespHeadersCount.tvKey.text = "Headers count"
        binding.rowRespHeadersCount.tvValue.text = "${request.responseHeaders.size}"

        binding.rowRespContentType.tvKey.text = "Content type"
        binding.rowRespContentType.tvValue.text = respContentType
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ApiDetailOverviewFragment()
    }
}
