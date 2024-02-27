package com.dafay.demo.lib.base.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewTreeObserver
import android.widget.OverScroller
import androidx.appcompat.widget.AppCompatImageView
import com.dafay.demo.lib.base.utils.ScreenUtils


class FullZoomImageView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) :
    AppCompatImageView(context, attr, defStyle), View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private var minScale = DEFAULT_MIN_SCALE
    private var midScale = DEFAULT_MID_SCALE
    private var maxScale = DEFAULT_MAX_SCALE
    private var allowParentInterceptOnEdge = true
    private var multiGestureDetector: MultiGestureDetector

    private var scaleType = ScaleType.FIT_CENTER

    // These are set so we don't keep allocating them on the heap
    // 初始化时基于 scaleType 设置的矩阵
    private val baseMatrix = Matrix()

    // 绘制/显示时的矩阵
    private val drawMatrix = Matrix()

    // 各种操作计算时的矩阵
    private val suppMatrix = Matrix()
    private val displayRect = RectF()
    private val matrixValues = FloatArray(9)


    private var top = 0
    private var right = 0
    private var bottom = 0
    private var left = 0
    private var currentFlingRunnable: FlingRunnable? = null
    private var scrollEdge = EDGE_BOTH
    private var isZoomEnabled = false

    // 当前的缩放值
    val scale: Float
        /**
         * Returns the current scale value
         *
         * @return float - current scale value
         */
        get() {
            suppMatrix.getValues(matrixValues)
            return matrixValues[Matrix.MSCALE_X]
        }

    protected val displayMatrix: Matrix
        protected get() {
            drawMatrix.set(baseMatrix)
            drawMatrix.postConcat(suppMatrix)
            return drawMatrix
        }


    // Listeners
    private var viewTapListener: OnViewTapListener? = null

    init {
        super.setScaleType(ScaleType.MATRIX)
        setOnTouchListener(this)
        multiGestureDetector = MultiGestureDetector(context)
        setIsZoomEnabled(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val newHeight: Int = ScreenUtils.getFullScreenHeight()
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY))
    }

    /**
     * Gets the Display Rectangle of the currently displayed Drawable. The
     * Rectangle is relative to this View and includes all scaling and
     * translations.
     *
     * @return - RectF of Displayed Drawable
     */
    fun getDisplayRect(): RectF? {
        checkMatrixBounds()
        return getDisplayRect(displayMatrix)
    }

    /**
     * @return The current minimum scale level. What this value represents
     * depends on the current [ScaleType]
     */
    fun getMinScale(): Float {
        return minScale
    }

    /**
     * Sets the minimum scale level. What this value represents depends on the
     * current [ScaleType].
     */
    fun setMinScale(minScale: Float) {
        checkZoomLevels(minScale, midScale, maxScale)
        this.minScale = minScale
    }

    /**
     * @return The current middle scale level. What this value represents
     * depends on the current [ScaleType]
     */
    fun getMidScale(): Float {
        return midScale
    }

    /**
     * Sets the middle scale level. What this value represents depends on the
     * current [ScaleType].
     */
    fun setMidScale(midScale: Float) {
        checkZoomLevels(minScale, midScale, maxScale)
        this.midScale = midScale
    }

    /**
     * @return The current maximum scale level. What this value represents
     * depends on the current [ScaleType]
     */
    fun getMaxScale(): Float {
        return maxScale
    }

    /**
     * Sets the maximum scale level. What this value represents depends on the
     * current [ScaleType].
     */
    fun setMaxScale(maxScale: Float) {
        checkZoomLevels(minScale, midScale, maxScale)
        this.maxScale = maxScale
    }


    /**
     * Return the current scale type in use by the ImageView.
     */
    override fun getScaleType(): ScaleType {
        return scaleType
    }

    /**
     * Controls how the image should be resized or moved to match the size of
     * the ImageView. Any scaling or panning will happen within the confines of
     * this [ScaleType].
     *
     * @param scaleType
     * - The desired scaling mode.
     */
    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType == ScaleType.MATRIX) {
            throw IllegalArgumentException(
                scaleType.name
                        + " is not supported in ZoomImageView"
            )
        }
        if (scaleType != this.scaleType) {
            this.scaleType = scaleType
            update()
        }
    }

    /**
     * Returns true if the ZoomImageView is set to allow zooming of Photos.
     *
     * @return true if the ZoomImageView allows zooming.
     */
    fun isZoomEnabled(): Boolean {
        return isZoomEnabled
    }

    /**
     * Allows you to enable/disable the zoom functionality on the ImageView.
     * When disable the ImageView reverts to using the FIT_CENTER matrix.
     *
     * @param isZoomEnabled
     * - Whether the zoom functionality is enabled.
     */
    fun setIsZoomEnabled(isZoomEnabled: Boolean) {
        this.isZoomEnabled = isZoomEnabled
        update()
    }

    /**
     * Whether to allow the ImageView's parent to intercept the touch event when
     * the photo is scroll to it's horizontal edge.
     */
    fun setAllowParentInterceptOnEdge(allowParentInterceptOnEdge: Boolean) {
        this.allowParentInterceptOnEdge = allowParentInterceptOnEdge
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        super.setImageBitmap(bitmap)
        update()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        update()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        update()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        update()
    }

    /**
     * Register a callback to be invoked when the View is tapped with a single
     * tap.
     *
     * @param listener
     * - Listener to be registered.
     */
    fun setOnViewTapListener(listener: OnViewTapListener?) {
        viewTapListener = listener
    }

    override fun onGlobalLayout() {
        if (isZoomEnabled) {
            val top = getTop()
            val right = getRight()
            val bottom = getBottom()
            val left = getLeft()
            /**
             * We need to check whether the ImageView's bounds have changed.
             * This would be easier if we targeted API 11+ as we could just use
             * View.OnLayoutChangeListener. Instead we have to replicate the
             * work, keeping track of the ImageView's bounds and then checking
             * if the values change.
             */
            if (((top != this.top) || (bottom != this.bottom) || (left != this.left)
                        || (right != this.right))
            ) {
                // Update our base matrix, as the bounds have changed
                updateBaseMatrix(drawable)

                // Update values as something has changed
                this.top = top
                this.right = right
                this.bottom = bottom
                this.left = left
            }
        }
    }

    override fun onTouch(v: View, ev: MotionEvent): Boolean {
        var handled = false
        if (isZoomEnabled) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    // First, disable the Parent from intercepting the touch
                    // event
                    if (v.parent != null) {
                        v.parent.requestDisallowInterceptTouchEvent(true)
                    }

                    // If we're flinging, and the user presses down, cancel
                    // fling
                    if (currentFlingRunnable != null) {
                        currentFlingRunnable!!.cancelFling()
                        currentFlingRunnable = null
                    }
                }

                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->                     // If the user has zoomed less than min scale, zoom back
                    // to min scale
                    if (scale < minScale) {
                        val rect = getDisplayRect()
                        if (null != rect) {
                            v.post(
                                AnimatedZoomRunnable(
                                    scale, minScale, rect.centerX(),
                                    rect.centerY()
                                )
                            )
                            handled = true
                        }
                    }
            }

            // Finally, try the scale/drag/tap detector
            if ((multiGestureDetector != null) && multiGestureDetector.onTouchEvent(ev)) {
                handled = true
            }
        }
        return handled
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    @Suppress("deprecation")
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeGlobalOnLayoutListener(this)
    }


    private fun update() {
        if (isZoomEnabled) {
            super.setScaleType(ScaleType.MATRIX)
            updateBaseMatrix(drawable)
        } else {
            resetMatrix()
        }
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private fun checkAndDisplayMatrix() {
        checkMatrixBounds()
        imageMatrix = displayMatrix
    }

    private fun checkMatrixBounds() {
        val rect = getDisplayRect(displayMatrix) ?: return
        val height = rect.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f
        val viewHeight = getHeight()
        if (height <= viewHeight) {
            when (scaleType) {
                ScaleType.FIT_START -> deltaY = -rect.top
                ScaleType.FIT_END -> deltaY = viewHeight - height - rect.top
                else -> deltaY = ((viewHeight - height) / 2) - rect.top
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom
        }
        val viewWidth = getWidth()
        if (width <= viewWidth) {
            when (scaleType) {
                ScaleType.FIT_START -> deltaX = -rect.left
                ScaleType.FIT_END -> deltaX = viewWidth - width - rect.left
                else -> deltaX = ((viewWidth - width) / 2) - rect.left
            }
            scrollEdge = EDGE_BOTH
        } else if (rect.left > 0) {
            scrollEdge = EDGE_LEFT
            deltaX = -rect.left
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right
            scrollEdge = EDGE_RIGHT
        } else {
            scrollEdge = EDGE_NONE
        }

        // Finally actually translate the matrix
        suppMatrix.postTranslate(deltaX, deltaY)
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix
     * - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private fun getDisplayRect(matrix: Matrix): RectF? {
        val d = drawable
        if (null != d) {
            displayRect[0f, 0f, d.intrinsicWidth.toFloat()] = d.intrinsicHeight.toFloat()
            matrix.mapRect(displayRect)
            return displayRect
        }
        return null
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays it.s
     */
    private fun resetMatrix() {
        if(suppMatrix!=null){
            suppMatrix.reset()
            imageMatrix = displayMatrix
            checkMatrixBounds()
        }
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param d
     * - Drawable being displayed
     */
    private fun updateBaseMatrix(d: Drawable?) {
        if (null == d) {
            return
        }
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val drawableWidth = d.intrinsicWidth
        val drawableHeight = d.intrinsicHeight
        baseMatrix.reset()
        val widthScale = viewWidth / drawableWidth
        val heightScale = viewHeight / drawableHeight
        if (scaleType == ScaleType.CENTER) {
            baseMatrix.postTranslate(
                (viewWidth - drawableWidth) / 2f,
                (viewHeight - drawableHeight) / 2f
            )
        } else if (scaleType == ScaleType.CENTER_CROP) {
            val scale = Math.max(widthScale, heightScale)
            baseMatrix.postScale(scale, scale)
            baseMatrix.postTranslate(
                (viewWidth - (drawableWidth * scale)) / 2f,
                (viewHeight - (drawableHeight * scale)) / 2f
            )
        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            val scale = Math.min(1.0f, Math.min(widthScale, heightScale))
            baseMatrix.postScale(scale, scale)
            baseMatrix.postTranslate(
                (viewWidth - (drawableWidth * scale)) / 2f,
                (viewHeight - (drawableHeight * scale)) / 2f
            )
        } else {
            val mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
            val mTempDst = RectF(0f, 0f, viewWidth, viewHeight)
            when (scaleType) {
                ScaleType.FIT_CENTER -> baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER)
                ScaleType.FIT_START -> baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START)
                ScaleType.FIT_END -> baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END)
                ScaleType.FIT_XY -> baseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL)
                else -> {}
            }
        }
        resetMatrix()
    }

    private fun checkZoomLevels(minZoom: Float, midZoom: Float, maxZoom: Float) {
        if (minZoom >= midZoom) {
            throw IllegalArgumentException("MinZoom should be less than MidZoom")
        } else if (midZoom >= maxZoom) {
            throw IllegalArgumentException("MidZoom should be less than MaxZoom")
        }
    }

    /**
     * 缩放执行
     */
    private inner class AnimatedZoomRunnable(
        currentZoom: Float, private val targetZoom: Float,
        private val focalX: Float, private val focalY: Float
    ) : Runnable {
        private var deltaScale = 0f

        init {
            if (currentZoom < targetZoom) {
                deltaScale = Companion.ANIMATION_SCALE_PER_ITERATION_IN
            } else {
                deltaScale = Companion.ANIMATION_SCALE_PER_ITERATION_OUT
            }
        }

        override fun run() {
            suppMatrix.postScale(deltaScale, deltaScale, focalX, focalY)
            checkAndDisplayMatrix()
            val currentScale: Float = scale
            if ((((deltaScale > 1f) && (currentScale < targetZoom))
                        || ((deltaScale < 1f) && (targetZoom < currentScale)))
            ) {
                // We haven't hit our target scale yet, so post ourselves
                // again
                postOnAnimation(this)
            } else {
                // We've scaled past our target zoom, so calculate the
                // necessary scale so we're back at target zoom
                val delta = targetZoom / currentScale
                suppMatrix.postScale(delta, delta, focalX, focalY)
                checkAndDisplayMatrix()
            }
        }
    }

    /**
     * 惯性运动执行
     */
    private inner class FlingRunnable(context: Context?) : Runnable {
        private val overScroller: OverScroller
        private var currentX = 0
        private var currentY = 0

        init {
            overScroller = OverScroller(context)
        }

        fun cancelFling() {
            overScroller.forceFinished(true)
        }

        fun fling(viewWidth: Int, viewHeight: Int, velocityX: Int, velocityY: Int) {
            val rect = getDisplayRect() ?: return
            val startX = Math.round(-rect.left)
            val minX: Int
            val maxX: Int
            val minY: Int
            val maxY: Int
            if (viewWidth < rect.width()) {
                minX = 0
                maxX = Math.round(rect.width() - viewWidth)
            } else {
                maxX = startX
                minX = maxX
            }
            val startY = Math.round(-rect.top)
            if (viewHeight < rect.height()) {
                minY = 0
                maxY = Math.round(rect.height() - viewHeight)
            } else {
                maxY = startY
                minY = maxY
            }
            currentX = startX
            currentY = startY

            // If we actually can move, fling the scroller
            if ((startX != maxX) || (startY != maxY)) {
                overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0)
            }
        }

        override fun run() {
            if (overScroller.computeScrollOffset()) {
                val newX = overScroller.currX
                val newY = overScroller.currY
                suppMatrix.postTranslate((currentX - newX).toFloat(), (currentY - newY).toFloat())
                imageMatrix = displayMatrix
                currentX = newX
                currentY = newY
                postOnAnimation(this)
            }
        }
    }

    /**
     * 多手势检测器，平移、缩放等
     */
    private inner class MultiGestureDetector(context: Context?) : GestureDetector.SimpleOnGestureListener(),
        ScaleGestureDetector.OnScaleGestureListener {
        private val scaleGestureDetector: ScaleGestureDetector
        private val gestureDetector: GestureDetector
        private var velocityTracker: VelocityTracker? = null
        private var isDragging = false
        private var lastTouchX = 0f
        private var lastTouchY = 0f
        private var lastPointerCount = 0f
        private val scaledTouchSlop: Float
        private val scaledMinimumFlingVelocity: Float

        init {
            scaleGestureDetector = ScaleGestureDetector(context!!, this)
            gestureDetector = GestureDetector(context, this)
            gestureDetector.setOnDoubleTapListener(this)
            val configuration = ViewConfiguration.get(context)
            scaledMinimumFlingVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
            scaledTouchSlop = configuration.scaledTouchSlop.toFloat()
        }

        val isScaling: Boolean
            get() = scaleGestureDetector.isInProgress

        fun onTouchEvent(event: MotionEvent): Boolean {
            if (gestureDetector.onTouchEvent(event)) {
                return true
            }
            scaleGestureDetector.onTouchEvent(event)

            /*
             * Get the center x, y of all the pointers
             */
            var x = 0f
            var y = 0f
            val pointerCount = event.pointerCount
            for (i in 0 until pointerCount) {
                x += event.getX(i)
                y += event.getY(i)
            }
            x = x / pointerCount
            y = y / pointerCount

            /*
             * If the pointer count has changed cancel the drag
             */if (pointerCount.toFloat() != lastPointerCount) {
                isDragging = false
                if (velocityTracker != null) {
                    velocityTracker!!.clear()
                }
                lastTouchX = x
                lastTouchY = y
            }
            lastPointerCount = pointerCount.toFloat()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain()
                    } else {
                        velocityTracker!!.clear()
                    }
                    velocityTracker!!.addMovement(event)
                    lastTouchX = x
                    lastTouchY = y
                    isDragging = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = x - lastTouchX
                    val dy = y - lastTouchY
                    if (isDragging == false) {
                        // Use Pythagoras to see if drag length is larger than
                        // touch slop
                        isDragging = Math.sqrt((dx * dx + dy * dy).toDouble()) >= scaledTouchSlop
                    }
                    if (isDragging) {
                        if (drawable != null) {
                            suppMatrix.postTranslate(dx, dy)
                            checkAndDisplayMatrix()
                            /**
                             * Here we decide whether to let the ImageView's
                             * parent to start taking over the touch event.
                             *
                             * First we check whether this function is enabled.
                             * We never want the parent to take over if we're
                             * scaling. We then check the edge we're on, and the
                             * direction of the scroll (i.e. if we're pulling
                             * against the edge, aka 'overscrolling', let the
                             * parent take over).
                             */
                            if (allowParentInterceptOnEdge && !multiGestureDetector!!.isScaling) {
                                if (scrollEdge == EDGE_BOTH || scrollEdge == EDGE_LEFT && dx >= 1f || scrollEdge == EDGE_RIGHT && dx <= -1f) {
                                    if (parent != null) {
                                        parent.requestDisallowInterceptTouchEvent(false)
                                    }
                                }
                            }
                        }
                        lastTouchX = x
                        lastTouchY = y
                        if (velocityTracker != null) {
                            velocityTracker!!.addMovement(event)
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        lastTouchX = x
                        lastTouchY = y

                        // Compute velocity within the last 1000ms
                        if (velocityTracker != null) {
                            velocityTracker!!.addMovement(event)
                            velocityTracker!!.computeCurrentVelocity(1000)
                            val vX = velocityTracker!!.xVelocity
                            val vY = velocityTracker!!
                                .yVelocity

                            // If the velocity is greater than minVelocity perform
                            // a fling
                            if (Math.max(Math.abs(vX), Math.abs(vY)) >= scaledMinimumFlingVelocity && drawable != null) {
                                currentFlingRunnable = FlingRunnable(context)
                                currentFlingRunnable!!.fling(width, height, -vX.toInt(), -vY.toInt())
                                post(currentFlingRunnable)
                            }
                        }
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    lastPointerCount = 0f
                    if (velocityTracker != null) {
                        velocityTracker!!.recycle()
                        velocityTracker = null
                    }
                }
            }
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale: Float = scale
            val scaleFactor = detector.scaleFactor
            if (drawable != null
                && !(scale >= maxScale && scaleFactor > 1f || scale <= 0.75 && scaleFactor < 1f)
            ) {
                suppMatrix.postScale(
                    scaleFactor, scaleFactor, detector.focusX,
                    detector.focusY
                )
                checkAndDisplayMatrix()
            }
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {}
        override fun onDoubleTap(event: MotionEvent): Boolean {
            try {
                val scale: Float = scale
                val x = event.x
                val y = event.y
                if (scale < midScale) {
                    post(AnimatedZoomRunnable(scale, midScale, x, y))
                } else if (scale >= midScale && scale < maxScale) {
                    post(AnimatedZoomRunnable(scale, maxScale, x, y))
                } else {
                    post(AnimatedZoomRunnable(scale, minScale, x, y))
                }
            } catch (e: Exception) {
                // Can sometimes happen when getX() and getY() is called
            }
            return true
        }

        override fun onDoubleTapEvent(event: MotionEvent): Boolean {
            // Wait for the confirmed onDoubleTap() instead
            return false
        }

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            if (viewTapListener != null) {
                viewTapListener!!.onViewTap(this@FullZoomImageView, event.x, event.y)
            }
            return false
        }

        override fun onLongPress(e: MotionEvent) {
            if (viewTapListener != null) {
                viewTapListener!!.onLongClick(this@FullZoomImageView)
            }
        }
    }

    companion object {
        private val EDGE_NONE = -1
        private val EDGE_LEFT = 0
        private val EDGE_RIGHT = 1
        private val EDGE_BOTH = 2
        val DEFAULT_MAX_SCALE = 3.0f
        val DEFAULT_MID_SCALE = 1.75f
        val DEFAULT_MIN_SCALE = 1f


        val ANIMATION_SCALE_PER_ITERATION_IN = 1.03f
        val ANIMATION_SCALE_PER_ITERATION_OUT = 0.97f
    }

    interface OnViewTapListener {
        /**
         * A callback to receive where the user taps on a ImageView. You will
         * receive a callback if the user taps anywhere on the view, tapping on
         * 'whitespace' will not be ignored.
         *
         * @param view
         * - View the user tapped.
         * @param x
         * - where the user tapped from the left of the View.
         * @param y
         * - where the user tapped from the top of the View.
         */
        fun onViewTap(view: View?, x: Float, y: Float)
        fun onLongClick(view: View?)
    }
}
