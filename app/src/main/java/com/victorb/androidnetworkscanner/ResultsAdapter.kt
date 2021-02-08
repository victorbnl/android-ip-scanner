package com.victorb.androidnetworkscanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultsAdapter() : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
    private val dataSet: ArrayList<Device> = arrayListOf();

    /**
     * Get the needed views as objects
     *
     * @param view The view of one element to get the children from
     *
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hostnameTextView: TextView = view.findViewById(R.id.textview_hostname)
        val ipTextView: TextView = view.findViewById(R.id.textview_ip)
    }

    /**
     * Sets the layout to use for one item
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_results,
                parent,
    false)
    )

    /**
     * Bind the text views to data
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ipTextView.text = dataSet[position].ip
        holder.hostnameTextView.text = dataSet[position].hostname
    }

    /**
     * Get item count
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount()
    = dataSet.size

    /**
     * Add an item to the dataset
     *
     * @param hostname The hostname of the item
     * @param ip The ip of the item
     */
    fun addItem(hostname: String, ip: String) {
        dataSet.add(Device(hostname, ip))
        runOnMainThread {
            notifyItemInserted(dataSet.size)
        }
    }

    /**
     * Clears the list, for refreshing for example
     */
    fun clear() {
        dataSet.clear()
        runOnMainThread {
            notifyDataSetChanged()
        }
    }
}