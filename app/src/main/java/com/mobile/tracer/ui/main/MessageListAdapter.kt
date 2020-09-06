package com.mobile.tracer.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mobile.tracer.R
import com.mobile.tracer.databinding.MessageItemBinding
import com.mobile.tracer.model.CustomMessage

/**
 * Created by Krishna Upadhya on 2020-02-22.
 */

class MessageListAdapter(private var items: ArrayList<CustomMessage>) :
    RecyclerView.Adapter<MessageListAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding: MessageItemBinding = DataBindingUtil.inflate<MessageItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.message_item,
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    fun updateMessageList(messages: ArrayList<CustomMessage>) {
        items = messages;
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = items[position]
        holder.messageItemBinding.customMessage = message
    }

    inner class MessageViewHolder(val messageItemBinding: MessageItemBinding) :
        RecyclerView.ViewHolder(messageItemBinding.getRoot())

}