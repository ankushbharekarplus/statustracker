package com.example.usagetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.usagetracker.databinding.ItemAppUsageBinding
import java.util.concurrent.TimeUnit

class UsageAdapter(private val items: List<AppUsageInfo>) :
    RecyclerView.Adapter<UsageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppUsageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppUsageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvAppName.text = item.appName
        holder.binding.tvUsageTime.text = formatDuration(item.totalTimeMs)
        holder.binding.ivIcon.setImageDrawable(item.icon)
    }

    override fun getItemCount(): Int = items.size

    private fun formatDuration(ms: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }
}
