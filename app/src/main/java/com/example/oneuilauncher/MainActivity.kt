package com.example.oneuilauncher

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private lateinit var grid: RecyclerView
    private lateinit var dock: LinearLayout
    private val adapter = AppAdapter(::launch, ::showMenu)
    private val main = Handler(Looper.getMainLooper())

    /** این چهار اپ در داک پایین می‌آیند (به ترتیب). */
    private val dockKeys = listOf("phone", "messages", "internet", "camera")

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = reload()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        grid = findViewById(R.id.grid)
        dock = findViewById(R.id.dock)
        grid.layoutManager = GridLayoutManager(this, 4)
        grid.adapter = adapter
        grid.setHasFixedSize(true)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        registerReceiver(packageReceiver, filter)

        reload()
    }

    override fun onDestroy() {
        unregisterReceiver(packageReceiver)
        super.onDestroy()
    }

    // دکمهٔ بازگشت روی صفحهٔ اصلی لانچر کاری نکند
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {}

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        grid.scrollToPosition(0) // دکمهٔ هوم = برگشت به بالای لیست
    }

    private fun reload() {
        val ctx = applicationContext
        thread {
            val pm = ctx.packageManager
            val density = ctx.resources.displayMetrics.density
            val iconPx = (56 * density).toInt()

            val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            val apps = pm.queryIntentActivities(query, 0)
                .filter { it.activityInfo.packageName != ctx.packageName }
                .map { ri ->
                    val pkg = ri.activityInfo.packageName
                    val label = ri.loadLabel(pm).toString()
                    val key = IconMapper.keyFor(pkg, label)
                    val custom = key?.let { IconMapper.load(ctx, it, iconPx) }
                    AppEntry(
                        label = label,
                        packageName = pkg,
                        component = android.content.ComponentName(pkg, ri.activityInfo.name),
                        iconKey = if (custom != null) key else null,
                        customIcon = custom,
                        defaultIcon = if (custom == null) ri.loadIcon(pm) else null
                    )
                }
                .sortedBy { it.label.lowercase() }

            // داک: برای هر کلید اولین اپ منطبق
            val dockApps = dockKeys.mapNotNull { k -> apps.firstOrNull { it.iconKey == k } }
            val gridApps = apps.filter { it !in dockApps }

            main.post {
                adapter.submit(gridApps)
                buildDock(dockApps)
            }
        }
    }

    private fun buildDock(list: List<AppEntry>) {
        dock.removeAllViews()
        val size = (56 * resources.displayMetrics.density).toInt()
        list.forEach { e ->
            val iv = ImageView(this).apply {
                if (e.customIcon != null) setImageBitmap(e.customIcon)
                else setImageDrawable(e.defaultIcon)
                setOnClickListener { launch(e) }
                setOnLongClickListener { showMenu(e); true }
            }
            dock.addView(iv, LinearLayout.LayoutParams(0, size, 1f).apply {
                gravity = Gravity.CENTER
            })
        }
    }

    private fun launch(e: AppEntry) {
        try { startActivity(e.launchIntent()) } catch (_: Exception) {}
    }

    private fun showMenu(e: AppEntry) {
        AlertDialog.Builder(this)
            .setTitle(e.label)
            .setItems(arrayOf("App info", "Uninstall")) { _, which ->
                val uri = Uri.parse("package:${e.packageName}")
                try {
                    if (which == 0) startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri))
                    else startActivity(Intent(Intent.ACTION_DELETE, uri))
                } catch (_: Exception) {}
            }
            .show()
    }
}
