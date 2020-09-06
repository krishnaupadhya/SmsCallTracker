package com.mobile.tracer.ui.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.tracer.BuildConfig
import com.mobile.tracer.R
import com.mobile.tracer.databinding.MainFragmentBinding
import com.mobile.tracer.utils.AppConstants
import com.mobile.tracer.utils.GenericUtils
import com.mobile.tracer.utils.Logger
import java.util.*


class MainFragment() : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var viewPager: ViewPager2
    private var listener: MainCallbackListener? = null
    private lateinit var fragmentBinding: MainFragmentBinding

    lateinit var mday: String
    lateinit var mMonth: String
    lateinit var mYear: String
    lateinit var mHour: String
    lateinit var mMinute: String


    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = DataBindingUtil.inflate<MainFragmentBinding>(
            inflater,
            R.layout.main_fragment,
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
        initViews();
    }

    private fun initViews() {
        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = fragmentBinding.pager

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = activity?.let { ViewPagerAdapter(it) }
        viewPager.adapter = pagerAdapter

        val tabLayout = fragmentBinding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text =
                if (position == 0) AppConstants.CALLS else AppConstants.MESSAGES

        }.attach()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, AppConstants.FILTER_DATE_YESTERDAY)

        fragmentBinding.dateImage.setOnClickListener {
            showDateTimePicker()
        }

        fragmentBinding.selectedDateTv.setOnClickListener {
            showDateTimePicker()
        }

        fragmentBinding.refreshImage.setOnClickListener {
            fragmentBinding.progressBar.visibility = View.VISIBLE
            viewModel.fetchAllLogs()
        }

        fragmentBinding.publishNotification.setOnClickListener {
            startUploaderAnimation()
            viewModel.publishNotification()
        }

        viewModel.uploadDataToServer.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                listener?.startFirebaseUploadService()
            }
        })

        viewModel.fireBaseDataFetchSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            fragmentBinding.progressBar.visibility = View.GONE

        })
        viewModel.fetchAllLogs()
    }

    private fun startUploaderAnimation() {

        fragmentBinding.notificationProgress.visibility = View.VISIBLE
        fragmentBinding
            .notificationUploadAnim.playAnimation()

        val timer = object : CountDownTimer(15000, 5000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {

                fragmentBinding.notificationProgress.visibility = View.GONE
                fragmentBinding.progressBar.visibility = View.VISIBLE
                viewModel.fetchAllLogs()
            }
        }
        timer.start()
    }


    fun uploadDeviceDataToServer() {
        viewModel.selectedName.value = GenericUtils.getUserNameFromEmail()
        viewModel.fetchAllLogs()
        listener?.let { callback ->
            if (callback.hasReadCallLogsPermission() && callback.hasReadSmsPermission())
                viewModel.prepareDataToUpload()
        }
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

    private fun showDateTimePicker() {
        val calendar: Calendar = Calendar.getInstance()
        val datePickerDialog =
            activity?.let {
                DatePickerDialog(
                    it,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
        datePickerDialog?.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        mday = String.format("%02d", dayOfMonth)
        mYear = String.format("%02d", year)
        mMonth = String.format("%02d", month)
        val calendar: Calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            activity, this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
            is24HourFormat(activity)
        )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        mHour = String.format("%02d", hourOfDay)
        mMinute = String.format("%02d", minute)
        viewModel.selectedDate.value = "$mYear/$mMonth/$mday $mHour:$mMinute:00"
        Logger.log("$mYear/$mMonth/$mday $mHour:$mMinute:00")
    }
}
