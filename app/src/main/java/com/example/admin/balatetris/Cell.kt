package com.example.admin.balatetris

import android.graphics.Bitmap

/**
 * Created by zjw on 2018/8/30.
 */
class Cell(var row: Int, var col: Int// Column 列
           ,
           var image: Bitmap? // 贴图
) {

    override fun toString(): String {
        return row.toString() + "," + col
    }

    fun moveRight() {
        col++
    }

    fun moveLeft() {
        col--
    }

    fun softDrop() {
        row++
    }
}