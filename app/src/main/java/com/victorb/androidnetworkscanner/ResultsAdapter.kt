package com.victorb.androidnetworkscanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultsAdapter(private val dataSet: MutableList<Device>)
    : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hostnameTextView: TextView = view.findViewById(R.id.textview_hostname)
        val ipTextView: TextView = view.findViewById(R.id.textview_ip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_results,
                parent,
    false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ipTextView.text = dataSet[position].ip
        holder.hostnameTextView.text = dataSet[position].hostname
    }

    override fun getItemCount()
    = dataSet.size
}