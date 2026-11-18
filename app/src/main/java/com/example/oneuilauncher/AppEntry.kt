package com.example.oneuilauncher

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable

class AppEntry(
    val label: String,
    val packageName: String,
    val component: ComponentName,
    val iconKey: String?,
    val customIcon: Bitmap?,
    val defaultIcon: Drawable?
) {
    fun launchIntent(): Intent =
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            this.component = this@AppEntry.component
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
}
