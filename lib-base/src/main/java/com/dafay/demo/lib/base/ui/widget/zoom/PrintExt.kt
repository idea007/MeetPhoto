package com.dafay.demo.lib.base.ui.widget.zoom

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.OverScroller
import android.widget.Scroller
import com.dafay.demo.lib.base.utils.debug

fun MotionEvent.toPrint(): String {
    return "MotionEvent { action=${MotionEvent.actionToString(this.action)}, x=${this.x},y=${this.y} }"

}

/**
 * currentSpan 返回通过焦点形成正在进行的手势的每个指针之间的平均距离
 * eventTime 事件时间戳
 * timeDelta 两个事件间隔时间
 * scaleFactor currentSpan/previousSpan
 */
fun ScaleGestureDetector.toPrint(): String {
    return "ScaleGestureDetector { currentSpan=${this.currentSpan}, currentSpanX=${this.currentSpanX}, currentSpanY=${this.currentSpanY}," +
            "previousSpan=${this.previousSpan}, previousSpanX=${this.previousSpanX}, previousSpanY=${this.previousSpanY}," +
            "eventTime=${this.eventTime},timeDelta=${this.timeDelta}, isQuickScaleEnabled=${this.isQuickScaleEnabled}," +
            "focusX=${this.focusX}, focusY=${this.focusY}, isInProgress=${this.isInProgress}, scaleFactor=${this.scaleFactor}}"

}


fun OverScroller.toPrint(): String {
    return "computeScrollOffset=${computeScrollOffset()} isFinished=${isFinished}\n" +
            "startX=${startX} startY=${startY} finalX=${finalX} finalY=${finalY}\n" +
            "currX=${currX} currY=${currY} currVelocity=${currVelocity}\n" +
            "isOverScrolled=${isOverScrolled}"
}


fun Scroller.toPrint(): String {
    return "\n" +
            "computeScrollOffset=${this.computeScrollOffset()} isFinished=${isFinished}\n" +
            "startX=${startX} startY=${startY} finalX=${finalX} finalY=${finalY}\n" +
            "currX=${currX} currY=${currY} currVelocity=${currVelocity}\n" +
            "duration=${duration} timePassed=${timePassed()}"

}