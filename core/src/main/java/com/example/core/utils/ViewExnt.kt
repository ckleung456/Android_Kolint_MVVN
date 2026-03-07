package com.example.core.utils

import android.net.Uri
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou

fun View.setVisible() = this.apply {
    isVisible = true
}

fun View.setGone() = this.apply {
    isVisible = false
}

fun View.setInvisible() = this.apply {
    visibility = View.INVISIBLE
}

fun View.setOnThrottleClickListener(throttleInterval: Long? = null, onClickFn: (View?) -> Unit) {
    setOnClickListener(throttleInterval?.let {
        OnThrottleClickListener(
            interval = throttleInterval,
            fn = onClickFn
        )
    } ?: OnThrottleClickListener(fn = onClickFn))
}

fun ImageView.loadSvgImage(imageUri: String) = this.apply {
    GlideToVectorYou
        .init()
        .with(context)
        .load(Uri.parse(imageUri), this)
}

class OnThrottleClickListener(
    private val interval: Long = DEFAULT_THROTTLE_TIME,
    private val fn: (View?) -> Unit
) : View.OnClickListener {
    private var lastClickTime = 0L

    override fun onClick(view: View?) {
        val prevClickTime = lastClickTime
        val currClickTime = SystemClock.uptimeMillis()
        lastClickTime = currClickTime
        if (currClickTime - prevClickTime > interval) {
            fn(view)
        }
    }

    companion object {
        private const val DEFAULT_THROTTLE_TIME = 1000L
    }
}