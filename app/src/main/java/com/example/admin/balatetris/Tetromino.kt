package com.example.admin.balatetris

import android.content.Context
import android.graphics.BitmapFactory
import java.util.*

/**
 * Created by zjw on 2018/8/30.
 */
//任何类型都继承于Object == 啥都是东西
//==唯物主义!== 一切皆
abstract class Tetromino (val mContext: Context?){
    var cells = arrayOfNulls<Cell>(4)
    protected var states = arrayOfNulls<State>(0)
    protected var index = 10000

    protected inner class State(internal var row0: Int, internal var col0: Int, internal var row1: Int, internal var col1: Int, internal var row2: Int, internal var col2: Int,
                                internal var row3: Int, internal var col3: Int)
    val bitmapT by lazy {
        BitmapFactory.decodeResource(mContext?.resources, R.mipmap.t)
    }
    val bitmapI by lazy {
        BitmapFactory.decodeResource(mContext?.resources, R.mipmap.i)
    }
    val bitmapS by lazy {
        BitmapFactory.decodeResource(mContext?.resources, R.mipmap.s)
    }
    val bitmapZ by lazy {
        BitmapFactory.decodeResource(mContext?.resources, R.mipmap.z)
    }
    val bitmapO by lazy {
        BitmapFactory.decodeResource(mContext?.resources, R.mipmap.o)
    }
    val bitmapL by lazy {
        BitmapFactory.decodeResource(mContext?.resources, R.mipmap.l)
    }
    val bitmapJ by lazy {
        BitmapFactory.decodeResource(mContext?.resources, R.mipmap.j)
    }
    /** 为了便于测试的方便  */
    override fun toString(): String {
        return Arrays.toString(cells)
        //return "["+cells[0].toString()+","+
        //cells[1].toString()+","+
        //cells[2].toString()+","+
        //cells[3].toString()+"]";
    }

    /** 当前这个对象的4格方块整体向左移动  */
    fun moveLeft() {
        cells[0]?.moveLeft()
        cells[1]?.moveLeft()
        cells[2]?.moveLeft()
        cells[3]?.moveLeft()
    }

    fun moveRight() {
        cells.forEachIndexed { index, cell -> cell?.moveRight() }
    }

    fun softDrop() {
        cells.forEachIndexed { index, cell -> cell?.softDrop()  }
    }

    //在Tetromino 中添加旋转算法
    /** 向右旋转算法  */
    fun rotateRight() {
        //计算index++
        //获取Sn(4组数据 0 1 2 3)
        //获取当前的轴
        //格子0 是轴 不变
        //格子1 的行列变为: 轴 + Sn[1]
        //格子2 的行列变为: 轴 + Sn[2]
        //格子3 的行列变为: 轴 + Sn[3]
        index++
        //System.out.println(
        //		"index:"+(index % states.length));
        val s = states!![index % states!!.size]
        val o = cells[0]
        val row = o?.row
        val col = o?.col
        //System.out.println(this);
        //System.out.println("cells[1]:"+cells[1]);
        //System.out.println("s:"+s);
        cells[1]?.row=row!!.plus(s!!.row1)
        cells[1]?.col=col!!.plus(s.col1)
        cells[2]?.row=row.plus(s.row2)
        cells[2]?.col=col.plus(s.col2)
        cells[3]?.row=row.plus(s.row3)
        cells[3]?.col=col.plus(s.col3)

    }

    fun rotateLeft() {
        index--
        val s = states[index % states.size]
        val o = cells[0]
        val row = o?.row
        val col = o?.col
        cells[1]?.row=row!!.plus(s!!.row1)
        cells[1]?.col=col!!.plus(s.col1)
        cells[2]?.row=row.plus(s.row2)
        cells[2]?.col=col.plus(s.col2)
        cells[3]?.row=row.plus(s.row3)
        cells[3]?.col=col.plus(s.col3)
    }

    companion object {

        /** 随机"生成"7种方块之一, 是公共方法
         * 工厂方法: 用于生产(创建)一个对象的方法
         * 封装了复杂的创建过程. 使用方便.
         * 用在创建对象过程复杂的情况.
         */
        fun randomOne(context: Context): Tetromino? {
            val random = Random()
            val type = random.nextInt(7)//[0,7)
            when (type) {
                0 -> return T(context)
                1 -> return I(context)
                2 -> return S(context)
                3 -> return Z(context)
                4 -> return J(context)
                5 -> return L(context)
                6 -> return O(context)
            }
            return null
        }
    }
}



internal class T(context: Context?) : Tetromino(context) {
    init {
        cells[0] = Cell(0, 4, bitmapT)
        cells[1] = Cell(0, 3, bitmapT)
        cells[2] = Cell(0, 5, bitmapT)
        cells[3] = Cell(1, 4, bitmapT)
        states = arrayOfNulls(4)
        states[0] = State(0, 0, 0, -1, 0, 1, 1, 0)//S0
        states[1] = State(0, 0, -1, 0, 1, 0, 0, -1)//S1
        states[2] = State(0, 0, 0, 1, 0, -1, -1, 0)//S2
        states[3] = State(0, 0, 1, 0, -1, 0, 0, 1)//S3
    }
}


internal class I(context: Context?) : Tetromino(context) {
    init {

        cells[0] = Cell(0, 4, bitmapI)
        cells[1] = Cell(0, 3, bitmapI)
        cells[2] = Cell(0, 5, bitmapI)
        cells[3] = Cell(0, 6, bitmapI)
        states = arrayOfNulls(2)
        states[0] = State(0, 0, 0, -1, 0, 1, 0, 2)
        states[1] = State(0, 0, -1, 0, 1, 0, 2, 0)
    }
}

internal class S(context: Context?) : Tetromino(context) {
    init {

        cells[0] = Cell(1, 4, bitmapS)
        cells[1] = Cell(0, 3, bitmapS)
        cells[2] = Cell(0, 4, bitmapS)
        cells[3] = Cell(1, 5, bitmapS)
        states = arrayOfNulls(2)
        states[0] = State(0, 0, 0, -1, -1, 0, -1, 1)
        states[1] = State(0, 0, -1, 0, 0, 1, 1, 1)
    }
}

internal class Z(context: Context?) : Tetromino(context) {
    init {

        cells[0] = Cell(1, 4, bitmapZ)
        cells[1] = Cell(0, 3, bitmapZ)
        cells[2] = Cell(0, 4, bitmapZ)
        cells[3] = Cell(1, 5, bitmapZ)
        states = arrayOfNulls(2)
        states[0] = State(0, 0, -1, -1, -1, 0, 0, 1)
        states[1] = State(0, 0, -1, 1, 0, 1, 1, 0)
    }
}

internal class O(context: Context?) : Tetromino(context) {
    init {

        cells[0] = Cell(0, 4, bitmapO)
        cells[1] = Cell(0, 5, bitmapO)
        cells[2] = Cell(1, 4, bitmapO)
        cells[3] = Cell(1, 5, bitmapO)
        states = arrayOfNulls(2)
        states[0] = State(0, 0, 0, 1, 1, 0, 1, 1)
        states[1] = State(0, 0, 0, 1, 1, 0, 1, 1)
    }
}

internal class L(context: Context?) : Tetromino(context) {
    init {

        cells[0] = Cell(0, 4, bitmapL)
        cells[1] = Cell(0, 3, bitmapL)
        cells[2] = Cell(0, 5, bitmapL)
        cells[3] = Cell(1, 3, bitmapL)
        states = arrayOfNulls(4)
        states[0] = State(0, 0, 0, 1, 0, -1, -1, 1)
        states[1] = State(0, 0, 1, 0, -1, 0, 1, 1)
        states[2] = State(0, 0, 0, -1, 0, 1, 1, -1)
        states[3] = State(0, 0, -1, 0, 1, 0, -1, -1)
    }
}

internal class J(context: Context?) : Tetromino(context) {
    init {

        cells[0] = Cell(0, 4, bitmapJ)
        cells[1] = Cell(0, 3, bitmapJ)
        cells[2] = Cell(0, 5, bitmapJ)
        cells[3] = Cell(1, 5, bitmapJ)
        states = arrayOfNulls(4)
        states[0] = State(0, 0, 0, -1, 0, 1, 1, 1)
        states[1] = State(0, 0, -1, 0, 1, 0, 1, -1)
        states[2] = State(0, 0, 0, 1, 0, -1, -1, -1)
        states[3] = State(0, 0, 1, 0, -1, 0, -1, 1)
    }
}