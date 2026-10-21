package com.example.usagetracker

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.usagetracker.databinding.ActivityMainBinding
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnGrantPermission.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check every time the app comes to foreground, since permission
        // is granted in a separate Settings screen.
        if (hasUsageAccessPermission()) {
            binding.layoutPermission.visibility = android.view.View.GONE
            binding.recyclerView.visibility = android.view.View.VISIBLE
            loadUsageStats()
        } else {
            binding.layoutPermission.visibility = android.view.View.VISIBLE
            binding.recyclerView.visibility = android.view.View.GONE
            binding.tvTotalTime.text = "0h 0m"
        }
    }

    /** Checks whether the user has granted the special "Usage Access" permission. */
    private fun hasUsageAccessPermission(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /** Pulls today's per-app foreground usage and renders it in the list. */
    private fun loadUsageStats() {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        // Aggregated per package, so each app appears once with its total for today
        val statsMap = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)

        val pm = packageManager
        var totalMs = 0L

        val usageList = statsMap.values
            .filter { it.totalTimeInForeground > 0 }
            .map { stat ->
                var label = stat.packageName
                var icon: android.graphics.drawable.Drawable? = null
                try {
                    val appInfo: ApplicationInfo = pm.getApplicationInfo(stat.packageName, 0)
                    label = pm.getApplicationLabel(appInfo).toString()
                    icon = pm.getApplicationIcon(appInfo)
                } catch (e: PackageManager.NameNotFoundException) {
                    // Keep the entry, just show the package name instead
                }
                totalMs += stat.totalTimeInForeground
                AppUsageInfo(
                    appName = label,
                    packageName = stat.packageName,
                    icon = icon,
                    totalTimeMs = stat.totalTimeInForeground
                )
            }
            .sortedByDescending { it.totalTimeMs }

        val hours = TimeUnit.MILLISECONDS.toHours(totalMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalMs) % 60
        binding.tvTotalTime.text = "${hours}h ${minutes}m"

        binding.recyclerView.adapter = UsageAdapter(usageList)
    }
}
