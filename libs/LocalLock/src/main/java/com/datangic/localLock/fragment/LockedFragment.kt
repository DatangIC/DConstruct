package com.datangic.localLock.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.datangic.localLock.LockerActivity
import com.datangic.localLock.R
import com.datangic.localLock.utils.SharePreferenceUtils

class LockedFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.locked_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val time = (System.currentTimeMillis() / 1000 - SharePreferenceUtils.getLongValue(this.requireContext(), SharePreferenceUtils.AUTH_ERROR)).toInt()

        if (time in 0..60) {
            timer(
                    view.findViewById(R.id.locked_textView),
                    60 - time,
            ) {
                (requireActivity() as LockerActivity).reAuth()
            }.start()
        } else {
            (requireActivity() as LockerActivity).reAuth()
        }
    }

    private fun timer(textView: TextView, delay: Int, action: (() -> Unit)) = object : CountDownTimer(delay * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            textView.text = getString(R.string.locked_time).format(millisUntilFinished / 1000)
        }

        override fun onFinish() {
            action()
        }
    }
}