package com.dafay.demo.zoom.utils

import android.graphics.Matrix
import androidx.annotation.Size


private val tmpMatrix = Matrix()
private val tmpMatrixInverse = Matrix()

object MathUtils {
    fun interpolate(
        start: Matrix,
        startPivotX: Float,
        startPivotY: Float,
        end: Matrix,
        endPivotX: Float,
        endPivotY: Float,
        factor: Float
    ): Matrix {
        val out = Matrix(start)
        if (start.scaleX() != end.scaleX()) {
            val zoom: Float = interpolate(start.scaleX(), end.scaleX(), factor)
            out.zoomTo(zoom, startPivotX, startPivotY)
        }
        val dx: Float = interpolate(0f, endPivotX - startPivotX, factor)
        val dy: Float = interpolate(0f, endPivotY - startPivotY, factor)
        out.translateBy(dx, dy)
        return out
    }

    /**
     * 基于进度求插值
     */
    fun interpolate(start: Float, end: Float, factor: Float): Float {
        return start + (end - start) * factor
    }

    fun computeNewPosition(
        @Size(2) point: FloatArray,
        initialMatrix: Matrix,
        finalMatrix: Matrix
    ) {
        tmpMatrix.set(initialMatrix)
        tmpMatrix.invert(tmpMatrixInverse)
        tmpMatrixInverse.mapPoints(point)
        tmpMatrix.set(finalMatrix)
        tmpMatrix.mapPoints(point)
    }

}