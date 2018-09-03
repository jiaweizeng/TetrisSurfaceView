package com.example.admin.balatetris

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Created by zjw on 2018/8/30.
 */
class BitmapUtil {
    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    fun scaleBitmap(origin: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
        if (origin == null) {
            return null
        }
        var newWid = newWidth
        var newHei = newHeight
        //原图的宽高
        val height = origin.height
        val width = origin.width
        //默认值,如果  新图 宽/高 == 0  就不拉升
        if (newHei == 0) newHei = height
        if (newWid == 0) newWid = width
        //通过矩阵拉升
        val scaleWidth = newWid.toFloat() / width
        val scaleHeight = newHei.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)// 使用后乘
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM != origin) {
            if (!origin.isRecycled) {
                origin.recycle()
            }
        }
        return newBM
    }
}