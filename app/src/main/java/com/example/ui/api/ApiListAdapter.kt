package com.example.ui.api

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.databinding.ItemApiRequestBinding
import com.example.model.ApiRequest
import com.example.utils.ApiExtensions

class ApiListAdapter(
    private val onItemClick: (ApiRequest) -> Unit
) : ListAdapter<ApiRequest, ApiListAdapter.ApiRequestViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiRequestViewHolder {
        val binding = ItemApiRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ApiRequestViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ApiRequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ApiRequestViewHolder(
        private val binding: ItemApiRequestBinding,
        private val onItemClick: (ApiRequest) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(request: ApiRequest) {
            val context = binding.root.context

            // Method badge
            binding.tvMethodBadge.text = request.method.name
            binding.tvMethodBadge.setTextColor(ApiExtensions.getMethodTextColor(context, request.method))
            binding.tvMethodBadge.backgroundTintList = ColorStateList.valueOf(
                ApiExtensions.getMethodBgColor(context, request.method)
            )

            // Path & Host
            binding.tvPath.text = request.path
            binding.tvHost.text = request.host

            // Status badge
            binding.tvStatusBadge.text = request.statusCode.toString()
            binding.tvStatusBadge.setTextColor(ApiExtensions.getStatusTextColor(context, request.statusCode))
            binding.tvStatusBadge.backgroundTintList = ColorStateList.valueOf(
                ApiExtensions.getStatusBgColor(context, request.statusCode)
            )

            // Status message
            binding.tvStatusMessage.text = "${request.statusCode} ${request.statusMessage}"
            if (request.isError) {
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(context, R.color.status_error_text))
            } else {
                binding.tvStatusMessage.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }

            // Timestamp & Duration
            binding.tvTimestamp.text = ApiExtensions.formatTime(request.timestamp)
            binding.tvDuration.text = ApiExtensions.formatDuration(request.durationMs)

            // Error row styling (4xx / 5xx)
            if (request.isError) {
                binding.cardRequest.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.status_error_row_bg)
                )
                binding.cardRequest.strokeColor = ContextCompat.getColor(context, R.color.status_error_bg)
            } else {
                binding.cardRequest.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.surface)
                )
                binding.cardRequest.strokeColor = ContextCompat.getColor(context, R.color.card_stroke)
            }

            // Click listener
            binding.cardRequest.setOnClickListener {
                onItemClick(request)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ApiRequest>() {
        override fun areItemsTheSame(oldItem: ApiRequest, newItem: ApiRequest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ApiRequest, newItem: ApiRequest): Boolean {
            return oldItem == newItem
        }
    }
}
