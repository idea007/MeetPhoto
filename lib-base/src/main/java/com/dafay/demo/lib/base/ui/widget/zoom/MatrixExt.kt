package com.dafay.demo.zoom.utils

import android.graphics.Matrix

fun Matrix.toPrint(): String {
    val values = FloatArray(9)
    this.getValues(values)
    return "Matrix:\n" +
            "[${values[0]},${values[1]},${values[2]}]\n" +
            "[${values[3]},${values[4]},${values[5]}]\n" +
            "[${values[6]},${values[7]},${values[8]}]"

}

private val matrixValues = FloatArray(9)
fun Matrix.transX(): Float {
    this.getValues(matrixValues)
    return matrixValues[Matrix.MTRANS_X]
}

fun Matrix.transY(): Float {
    this.getValues(matrixValues)
    return matrixValues[Matrix.MTRANS_Y]
}

fun Matrix.scaleX(): Float {
    this.getValues(matrixValues)
    return matrixValues[Matrix.MSCALE_X]
}

fun Matrix.scaleY(): Float {
    this.getValues(matrixValues)
    return matrixValues[Matrix.MSCALE_Y]
}

fun Matrix.translateBy(dx: Float, dy: Float) {
    this.postTranslate(dx, dy)
}

fun Matrix.translateTo(x: Float, y: Float) {
    this.postTranslate(-this.transX() + x, -this.transY() + y)
}

fun Matrix.zoomBy(factor: Float, pivotX: Float, pivotY: Float) {
    this.postScale(factor, factor, pivotX, pivotY)
}

fun Matrix.zoomTo(zoom: Float, pivotX: Float, pivotY: Float) {
    this.postScale(zoom / this.scaleX(), zoom / this.scaleY(), pivotX, pivotY)
}



