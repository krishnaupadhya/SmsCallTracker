package com.mobile.tracer.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mobile.tracer.BuildConfig
import com.mobile.tracer.utils.EncryptDecryptUtil.generateKey


/**
 * Created by Krishna Upadhya on 2019-11-21.
 */


class CommonBindingUtils {

    companion object {
        @JvmStatic
        @BindingAdapter("app:decryptText")
        fun showHide(view: TextView, text: String) {
            view.text = EncryptDecryptUtil.decrypt(text, generateKey(BuildConfig.SECRET_KEY))
        }
    }
}