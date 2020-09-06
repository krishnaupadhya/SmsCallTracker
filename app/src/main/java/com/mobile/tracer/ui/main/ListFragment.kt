package com.mobile.tracer.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.tracer.R
import com.mobile.tracer.databinding.ListFragmentBinding
import com.mobile.tracer.model.CustomCallLog
import com.mobile.tracer.model.CustomMessage
import com.mobile.tracer.utils.AppConstants


class ListFragment : Fragment() {

    private lateinit var listener: MainCallbackListener
    private lateinit var fragmentBinding: ListFragmentBinding
    private lateinit var recyclerView: RecyclerView
    lateinit var listType: String

    companion object {
        fun newInstance() = ListFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = DataBindingUtil.inflate<ListFragmentBinding>(
            inflater,
            R.layout.list_fragment,
            container,
            false
        )
        return fragmentBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }!!
        fragmentBinding.viewmodel = viewModel
        // Call binding.setLifecycleOwner to make the data binding lifecycle aware:
        fragmentBinding.lifecycleOwner = this

        if (AppConstants.KEY_CALL_LOG_LIST.equals(listType)) {
            viewModel.callsList.observe(viewLifecycleOwner, Observer {
                updateCallsList(it)
            })
        } else if (AppConstants.KEY_MESSAGE_LIST.equals(listType)) {
            viewModel.messagesList.observe(viewLifecycleOwner, Observer {
                updateMessageList(it)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews();
    }

    private fun initViews() {
        recyclerView = fragmentBinding.rvMessageList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
    }

    private fun updateCallsList(calls: ArrayList<CustomCallLog>) {
        recyclerView.adapter = CallsListAdapter(calls)
        recyclerView.scheduleLayoutAnimation();
    }

    private fun updateMessageList(messages: ArrayList<CustomMessage>) {
        recyclerView.adapter = MessageListAdapter(messages)
        recyclerView.scheduleLayoutAnimation();
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainCallbackListener) {
            listener = context
        } else {
            throw ClassCastException(
                "$context must implement MainCallbackListener."
            )
        }
    }
}
