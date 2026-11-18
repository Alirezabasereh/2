package com.example.oneuilauncher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(
    private val onClick: (AppEntry) -> Unit,
    private val onLongClick: (AppEntry) -> Unit
) : RecyclerView.Adapter<AppAdapter.VH>() {

    private var items: List<AppEntry> = emptyList()

    fun submit(list: List<AppEntry>) {
        items = list
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.icon)
        val label: TextView = v.findViewById(R.id.label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val e = items[position]
        if (e.customIcon != null) h.icon.setImageBitmap(e.customIcon)
        else h.icon.setImageDrawable(e.defaultIcon)
        h.label.text = e.label
        h.itemView.setOnClickListener { onClick(e) }
        h.itemView.setOnLongClickListener { onLongClick(e); true }
    }

    override fun getItemCount() = items.size
}
