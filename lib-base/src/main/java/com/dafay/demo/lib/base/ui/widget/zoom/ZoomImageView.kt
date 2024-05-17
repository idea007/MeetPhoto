package com.dafay.demo.lib.base.ui.widget.zoom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.Choreographer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.OverScroller
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.lib.base.utils.ScreenUtils
import com.dafay.demo.zoom.utils.MathUtils
import com.dafay.demo.zoom.utils.scaleX
import com.dafay.demo.zoom.utils.translateBy
import com.dafay.demo.zoom.utils.zoomBy
import com.dafay.demo.zoom.utils.zoomTo

/**
 * 实现功能
 * over zoom 处理（< minZoom)
 */
class ZoomImageView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // 手势检测器
    private val gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector

    // 默认类似 fitCenter 显示模式时的矩阵
    private val originMatrix = Matrix()
    private val suppMatrix = Matrix()

    // 绘画前辅助计算用
    private val tempMatrix = Matrix()
    private var minZoom = DEFAULT_MIN_ZOOM
    private var midZoom = DEFAULT_MID_ZOOM
    private var maxZoom = DEFAULT_MAX_ZOOM
    private var zoomAnim = ValueAnimator().apply { duration = DEFAULT_ANIM_DURATION }
    private val pivotPointF = PointF(0f, 0f)

    // 用来执行 onFling 动画
    private var overScroller: OverScroller
    private var startTime: Long = 0
    private val choreographer = Choreographer.getInstance()
    private var currentX = 0
    private var currentY = 0
    private var overMinZoomRadio = 0.75f

    private var scrollEdge = Edge.EDGE_BOTH
    private var allowParentInterceptOnEdge = true

    var viewTapListener: OnViewTapListener? = null

    init {
        overScroller = OverScroller(context)
        scaleType = ScaleType.MATRIX
        val multiGestureDetector = MultiGestureDetector()
        gestureDetector = GestureDetector(context, multiGestureDetector)
        scaleGestureDetector = ScaleGestureDetector(context, multiGestureDetector)
        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (gestureDetector.onTouchEvent(event)) {
                    return true
                }
                scaleGestureDetector.onTouchEvent(event)
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        if (v.parent != null) {
                            v.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        dealUpOrCancel(event)
                    }
                }
                return true
            }
        })
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        updateOriginMatrix(drawable)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        updateOriginMatrix(drawable)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        updateOriginMatrix(drawable)
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        updateOriginMatrix(drawable)
    }

    fun setMinZoom(minZoom: Float) {
        checkZoomLevels(minZoom, midZoom, maxZoom)
        this.minZoom = minZoom
    }

    fun setMidZoom(midZoom: Float) {
        checkZoomLevels(minZoom, midZoom, maxZoom)
        this.midZoom = midZoom
    }

    fun setMaxZoom(maxZoom: Float) {
        checkZoomLevels(minZoom, midZoom, maxZoom)
        this.maxZoom = maxZoom
    }

    private fun checkZoomLevels(minZoom: Float, midZoom: Float, maxZoom: Float) {
        if (minZoom >= midZoom) {
            throw IllegalArgumentException("MinZoom should be less than MidZoom")
        } else if (midZoom >= maxZoom) {
            throw IllegalArgumentException("MidZoom should be less than MaxZoom")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeight: Int = ScreenUtils.getFullScreenHeight()
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateOriginMatrix(drawable)
    }

    /**
     * 计算图片显示类似 fitCenter 效果时的矩阵（忽视 pading，只处理 fitCenter 这一种显示模式）
     */
    private fun updateOriginMatrix(drawable: Drawable?) {
        drawable ?: return
        if (width <= 0) {
            return
        }
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight
        originMatrix.reset()
        val tempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
        val tempDst = RectF(0f, 0f, viewWidth, viewHeight)
        originMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER)
        applyToImageMatrix()
    }

    /**
     * 处理双击事件，双击执行缩放动画
     */
    private fun dealOnDoubleTap(e: MotionEvent) {
        animZoom(e.x, e.y)
    }

    private fun animZoom(pivotX: Float, pivotY: Float) {
        val currZoom = suppMatrix.scaleX()
        val endZoom = if (currZoom < midZoom) {
            midZoom
        } else if (currZoom < maxZoom) {
            maxZoom
        } else {
            minZoom
        }
        playZoomAnim(currZoom, endZoom, pivotX, pivotY)
    }

    private fun dealUpOrCancel(event: MotionEvent) {
        val currZoom = suppMatrix.scaleX()
        if (currZoom < minZoom) {
            val rect = getDrawMatrixRect(imageMatrix)
            rect?.let { playZoomAnim(currZoom, minZoom, it.centerX(), it.centerY()) }
        }
    }

    private fun playZoomAnim(startZoom: Float, endZoom: Float, pivotX: Float, pivotY: Float) {
        zoomAnim.removeAllUpdateListeners()
        zoomAnim.cancel()

        // 点击的点设置为缩放的支点
        pivotPointF.set(pivotX, pivotY)
        val startMatrix = Matrix(imageMatrix)
        val endMatrix = Matrix(originMatrix).apply {
            val tempSuppMatrix = Matrix(suppMatrix)
            tempSuppMatrix.zoomTo(endZoom, pivotPointF.x, pivotPointF.y)
            this.postConcat(tempSuppMatrix)
        }
        // 边界矫正
        correctByViewBound(endMatrix).let {
            endMatrix.translateBy(it.x, it.y)
        }

        val tmpPointArr = floatArrayOf(pivotX, pivotY)
        MathUtils.computeNewPosition(tmpPointArr, imageMatrix, endMatrix)
        val endPivotPointF = PointF(tmpPointArr[0], tmpPointArr[1])
        val currMatrix = Matrix()
        val animatorUpdateListener = object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val tempValue = animation.animatedValue as Float
                val factor = (tempValue - startZoom) / (endZoom - startZoom)
                currMatrix.set(
                    MathUtils.interpolate(
                        startMatrix,
                        pivotPointF.x,
                        pivotPointF.y,
                        endMatrix,
                        endPivotPointF.x,
                        endPivotPointF.y,
                        factor
                    )
                )
                // suppMatrix * originMatrix = currMatrix;  suppMatrix = currMatrix *（originMatrix 的逆矩阵）
                tempMatrix.reset()
                originMatrix.invert(tempMatrix)
                tempMatrix.postConcat(currMatrix)
                suppMatrix.set(tempMatrix)
                applyToImageMatrix(true)
            }
        }
        zoomAnim.setFloatValues(startZoom, endZoom)
        zoomAnim.addUpdateListener(animatorUpdateListener)
        zoomAnim.start()
    }

    private fun dealOnFling(e2: MotionEvent, velocityX: Float, velocityY: Float) {
        val rect = getDrawMatrixRect(imageMatrix) ?: return
        val startX = Math.round(-rect.left)
        val minX: Int
        val maxX: Int
        val minY: Int
        val maxY: Int
        if (width < rect.width()) {
            minX = 0
            maxX = Math.round(rect.width() - width)
        } else {
            maxX = startX
            minX = maxX
        }
        val startY = Math.round(-rect.top)
        if (height < rect.height()) {
            minY = 0
            maxY = Math.round(rect.height() - height)
        } else {
            maxY = startY
            minY = maxY
        }
        currentX = startX
        currentY = startY
        if (!((startX != maxX) || (startY != maxY))) {
            return
        }
        overScroller.fling(startX, startY, -velocityX.toInt(), -velocityY.toInt(), minX, maxX, minY, maxY, 0, 0)
        startFlingAnim()
    }

    private fun startFlingAnim() {
        startTime = AnimationUtils.currentAnimationTimeMillis()
        postNextFrame()
    }

    private fun postNextFrame() {
        if (overScroller.computeScrollOffset()) {
            val currX = overScroller.currX
            val currY = overScroller.currY
            suppMatrix.translateBy((currentX - currX).toFloat(), (currentY - currY).toFloat())
            applyToImageMatrix()
            currentX = currX
            currentY = currY
            choreographer.postFrameCallback {
                postNextFrame()
            }
        }
    }

    /**
     * 处理拖动（平移）事件
     */
    private fun dealOnScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float) {
        if (allowParentInterceptOnEdge && !scaleGestureDetector.isInProgress) {
            if (scrollEdge == Edge.EDGE_BOTH || (scrollEdge == Edge.EDGE_LEFT && -distanceX >= 1f) || (scrollEdge == Edge.EDGE_RIGHT && -distanceX <= -1f)) {
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        suppMatrix.translateBy(-distanceX, -distanceY)
        applyToImageMatrix()
    }

    /**
     * 处理双指缩放
     */
    private fun dealOnScale(detector: ScaleGestureDetector) {
        val currScale: Float = suppMatrix.scaleX()
        var scaleFactor = detector.scaleFactor
        if ((currScale >= maxZoom && scaleFactor > 1f) || (currScale <= minZoom * overMinZoomRadio && scaleFactor < 1f)) {
            return
        }
        suppMatrix.zoomBy(scaleFactor, detector.focusX, detector.focusY)
        applyToImageMatrix()
    }

    /**
     * 对输入矩阵，依据 View 宽高进行调整，输出需要调整的平移量
     */
    private fun correctByViewBound(srcMatrix: Matrix): PointF {
        val tempPointF = PointF()
        // 得到 matrix 的 rect
        val tempRectF = getDrawMatrixRect(srcMatrix)
        tempRectF ?: return tempPointF
        var deltaX = 0f
        var deltaY = 0f
        if (tempRectF.height() < height) {
            deltaY = ((height - tempRectF.height()) / 2) - tempRectF.top
        } else if (tempRectF.top > 0) {
            deltaY = -tempRectF.top
        } else if (tempRectF.bottom < height) {
            deltaY = height - tempRectF.bottom
        }
        if (tempRectF.width() <= width) {
            deltaX = ((width - tempRectF.width()) / 2) - tempRectF.left
            scrollEdge = Edge.EDGE_BOTH
        } else if (tempRectF.left > 0) {
            deltaX = -tempRectF.left
            scrollEdge = Edge.EDGE_LEFT
        } else if (tempRectF.right < width) {
            deltaX = width - tempRectF.right
            scrollEdge = Edge.EDGE_RIGHT
        } else {
            scrollEdge = Edge.EDGE_NONE
        }
        tempPointF.set(deltaX, deltaY)
        return tempPointF
    }

    private fun getDrawMatrixRect(matrix: Matrix): RectF? {
        val d = drawable
        if (null != d) {
            val tempRect = RectF()
            // 新奇的写法
            tempRect[0f, 0f, d.intrinsicWidth.toFloat()] = d.intrinsicHeight.toFloat()
            matrix.mapRect(tempRect)
            return tempRect
        }
        return null
    }


    /**
     * 在显示之前，进行边界矫正，对 suppMatrix 进行调整
     */
    private fun correctSuppMatrix() {
        // 目标 matrix
        tempMatrix.set(originMatrix)
        tempMatrix.postConcat(suppMatrix)
        correctByViewBound(tempMatrix).let {
            suppMatrix.translateBy(it.x, it.y)
        }
    }

    /**
     * 应用于 ImageView 的 matrix，为了思路清晰，这里先频繁创建对象 drawMatrix
     */
    private fun applyToImageMatrix(skipCorrect: Boolean = false) {
        if (!skipCorrect) {
            // 在应用之前，进行边界矫正
            correctSuppMatrix()
        }
        tempMatrix.set(originMatrix)
        // 即当前 Matrix 会乘以传入的 Matrix。 suppMatrix*originMatrix
        tempMatrix.postConcat(suppMatrix)
        imageMatrix = tempMatrix
    }

    inner class MultiGestureDetector : GestureDetector.SimpleOnGestureListener(),
        ScaleGestureDetector.OnScaleGestureListener {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            viewTapListener?.onViewTap(this@ZoomImageView, e.x, e.y)
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            viewTapListener?.onLongClick(this@ZoomImageView)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            dealOnDoubleTap(e)
            return super.onDoubleTap(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            dealOnScroll(e1, e2, distanceX, distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            dealOnFling(e2, velocityX, velocityY)
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            dealOnScale(detector)
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
        }
    }

    interface OnViewTapListener {
        fun onViewTap(view: View?, x: Float, y: Float)
        fun onLongClick(view: View?)
    }
}