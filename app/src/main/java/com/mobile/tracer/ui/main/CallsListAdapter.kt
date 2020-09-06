package com.mobile.tracer.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobile.tracer.R
import com.mobile.tracer.databinding.CallsItemBinding
import com.mobile.tracer.model.CustomCallLog

/**
 * Created by Krishna Upadhya on 2020-02-22.
 */

class CallsListAdapter(private var items: ArrayList<CustomCallLog>) :
    RecyclerView.Adapter<CallsListAdapter.CallsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallsViewHolder {
        val binding: CallsItemBinding = DataBindingUtil.inflate<CallsItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.calls_item,
            parent,
            false
        )
        return CallsViewHolder(binding)
    }

    fun updateCallsList(messages: ArrayList<CustomCallLog>) {
        items = messages;
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CallsViewHolder, position: Int) {
        val calls = items[position]
        holder.callsItemBinding.customCalls = calls
    }

    inner class CallsViewHolder(val callsItemBinding: CallsItemBinding) :
        RecyclerView.ViewHolder(callsItemBinding.root)

}