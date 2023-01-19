package io.github.alaksion.sample.xml

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.alaksion.sample.databinding.XmlItemBinding

internal class XmlActivityViewHolder(
    private val binding: XmlItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(item: String) {
        binding.tvItemName.text = item
    }

}

internal class XmlActivityAdapter : RecyclerView.Adapter<XmlActivityViewHolder>() {

    private var items: List<String> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XmlActivityViewHolder {
        val view = XmlItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return XmlActivityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: XmlActivityViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    fun updateItems(items: List<String>) {
        this.items = items
        notifyDataSetChanged()
    }

}