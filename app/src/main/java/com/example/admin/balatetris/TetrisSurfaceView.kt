package com.example.admin.balatetris

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Exception
import java.util.*

/**
 * Created by zjw on 2018/8/30.
 */
class TetrisSurfaceView @JvmOverloads constructor(
        context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var mBg: Bitmap? = null
    private var mGameOver: Bitmap? = null
    private var mPause: Bitmap? = null
    private var mPaint: Paint? = null
    private var mIsDraw: Boolean = false
    private var mCanvas: Canvas? = null


    private var mSurfaceWidth =0//分配给surfaceView的宽度
    private var mSurfaceHeight =0//分配给surfaceView的高度
    private val CELL_SIZE = 46//每块方块的大小
    private var ROWS = 29//行数
    private var COLS = 11//列数
    /** 正在下落的方块  */
    private var tetromino: Tetromino? = null
    /** 下一个进场的方块  */
    private var nextOne: Tetromino? = null
    /** 得分  */
    private var score: Int = 0
    /** 难度级别  */
    private var level: Int = 0
    /** 销毁行数  */
    private var lines: Int = 0
    /** 游戏的当前状态: RUNNING PAUSE GAME_OVER  */
    var state: Int = 0
    val RUNNING = 0
    val PAUSE = 1
    val GAME_OVER = 2
    /** 下落计数器 当 index%speed==0 时候下落一次  */
    var index: Int = 0
    /** 速度  */
    private var speed: Int = 0
    /** 墙 */
    private var mWall = Array(ROWS, { Array<Cell?>(COLS, { null }) })


    init {
        holder.addCallback(this)
        isFocusable = true
        isFocusableInTouchMode = true
        keepScreenOn = true
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mSurfaceWidth= width
        mSurfaceHeight=height
        ROWS=(height-60)/CELL_SIZE
        COLS=width/(2*CELL_SIZE)
        mWall = Array(ROWS, { Array<Cell?>(COLS, { null }) })
        BitmapUtil().apply {
            mBg = scaleBitmap(BitmapFactory.decodeResource(resources, R.mipmap.tetris), width, height)
            mGameOver = scaleBitmap(BitmapFactory.decodeResource(resources, R.mipmap.game_over), width, height)
            mPause = scaleBitmap(BitmapFactory.decodeResource(resources, R.mipmap.pause), width, height)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mIsDraw = false
        holder?.removeCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mPaint = Paint()
        mIsDraw = true

        Thread {
            while (mIsDraw) {
                drawTetris()
                Thread.sleep(20)
            }
        }.start()

        tetromino = Tetromino.randomOne(context)
        nextOne = Tetromino.randomOne(context)
        state = RUNNING


        Timer().schedule(object : TimerTask() {
            override fun run() {
                speed = 40 - lines / 100
                speed = if (speed < 1) 1 else speed
                level = 41 - speed
                if (state == RUNNING && index % speed == 0) {
                    softDropAction()
                }
                index++
            }

        }, 10.toLong(), 10.toLong())

    }

    private fun drawTetris() {
        try {
//            synchronized(this) {
                mCanvas = holder.lockCanvas()
                mCanvas?.drawBitmap(mBg, 0f, 0f, mPaint)//背景
                mCanvas?.translate(40.toFloat(), 45.toFloat())
                paintWall()
                /** 封装了绘制墙算法  */
                paintTetromino()
                paintNextOne()
                paintScore()
                paintState()// 绘制游戏的状态
//            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            holder.unlockCanvasAndPost(mCanvas)
        }
    }



    /** 封装了绘制墙算法  */
    private fun paintWall() {
        mWall.forEachIndexed { row, wall ->
            wall.forEachIndexed { col, i ->
                val x = col * CELL_SIZE
                val y = row * CELL_SIZE
                val cell = mWall[row][col]
                /*mCanvas?.drawBitmap(BitmapFactory.decodeResource(resources,R.drawable.shape_cell),
                        x.toFloat(), y.toFloat(), mPaint)*/
                if (cell!=null){
                    mCanvas?.drawBitmap(cell.image,
                            x.toFloat(), y.toFloat(), mPaint)
                }
            }
        }
    }

    private fun paintTetromino() {
        if (tetromino == null) {
            return
        }
        val cells = tetromino?.cells
        cells?.forEachIndexed { index, cell ->
            cell?.apply {
                val x = col * CELL_SIZE
                val y = row * CELL_SIZE
                mCanvas?.drawBitmap(image, x.toFloat(), y.toFloat(), mPaint)
            }
        }
    }

    private fun paintNextOne() {
        if (nextOne == null) {
            return
        }
        val cells = nextOne?.cells
        cells?.forEachIndexed { index, cell ->
            cell?.apply {
                val x = (col.plus(ROWS/2-2)) * CELL_SIZE
//                val x = mSurfaceWidth*3/4
                val y = (row.plus(2)) * CELL_SIZE
                mCanvas?.drawBitmap(image, x.toFloat(), y.toFloat(), mPaint)
            }
        }
    }

    private val mTvSize = 70f//分数字体大小


    /** 绘制分数 */
    private fun paintScore() {
        val mGapSize = mSurfaceHeight/10//间隔大小
        var x = (COLS+COLS/3)*CELL_SIZE
        var y = mSurfaceHeight/4+75
        mCanvas?.apply {
            mPaint?.apply {
//                color = Color.parseColor("#FFFFFFFF")
                color = Color.RED
                textSize=mTvSize
                drawText("SCORE:$score", x.toFloat(), y.toFloat(), mPaint)
                y += mGapSize
                drawText("LINES:$lines", x.toFloat(), y.toFloat(), mPaint)
                y += mGapSize
                drawText("LEVEL:$level", x.toFloat(), y.toFloat(), mPaint)
            }
        }
    }

    // 绘制游戏的状态
    private fun paintState() {
        mCanvas?.apply {
            when (state) {
                PAUSE -> drawBitmap(mPause, (-15).toFloat(), (-15).toFloat(), mPaint)
                GAME_OVER -> drawBitmap(mGameOver, (-15).toFloat(), (-15).toFloat(), mPaint)
            }
        }
    }

    /** 得分表  */
    private val scoreTable = intArrayOf(0, 1, 10, 100, 500)

    /** Tetris 中添加下落动作  */
    fun softDropAction() {
        // 1 如果能够下落就下落
        // 2 如果不能下落 着陆到墙里面
        // 3 销毁已经满的行
        // 4 如果没有结束, 就产生下一个方块
        if (canDrop()) {
            tetromino?.softDrop()
        } else {
            landIntoWall()
            val lines = destroyLines()
            this.lines += lines
            // lines = 0 1 2 3 4
            // {0,1,10,100,500};
            this.score += scoreTable[lines]
            if (isGameOver()) {
                state = GAME_OVER
            } else {
                tetromino = nextOne
                nextOne = Tetromino.randomOne(context)
            }
        }
    }

    /** 检查当前方块是否能够下落  */
    private fun canDrop(): Boolean {
        // 1 方块的某个格子行到达19就不能下落了
        // 2 方块的某个格子对应墙上的下方出现
        // 格子就不能下落了
        val cells = tetromino?.cells
        cells?.apply {
            forEachIndexed { index, cell ->
                cell?.apply {
                    if (row == ROWS - 1) {
                        return false// 不能下落了
                    }
                    if (mWall[row.plus(1)][col] != null) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /** 着陆到墙里面  */
    private fun landIntoWall() {
        // 根据每个格子的位置, 进入到墙上对于的位置
        val cells = tetromino?.cells
        // 增强版for循环, Java 5 提供,编译器处理
        // (本质上是"标准数组迭代"的简化版)
        cells?.forEachIndexed { index, cell ->
            val row = cell?.row
            val col = cell?.col

            row?.let {
                col?.let {
                    mWall[row][col] = cell
                }
            }
        }
    }

    /** 销毁已经满的行, 返回销毁行数  */
    private fun destroyLines(): Int {
        // 从0 ~ 19 逐行查找, 如果找到满行, 就
        // 删除这行
        var lines = 0
        for (row in 0 until ROWS) {
            if (isFullCells(row)) {
                deleteRow(row)
                lines++
            }
        }
        return lines
    }

    /** 检查row这行是否都是格子  */
    private fun isFullCells(row: Int): Boolean {
        val line = mWall[row]
        for (cell in line) {
            if (cell==null)
            return false
        }
        return true
    }

    /** 删除一行, row是行号  */
    private fun deleteRow(row: Int) {
        for (i in row downTo 1) {
            // 复制: wall[i-1] -> wall[i]
            System.arraycopy(mWall[i - 1], 0, mWall[i], 0, COLS)
        }
        Arrays.fill(mWall[0], null)// fill填充
    }

    /** 检查游戏是否结束  */
    private fun isGameOver(): Boolean {
        // 如果下一个方块没有出场位置了, 则游戏结束
        // 就是: 下一个出场方块每个格子行列对应的
        // 墙上位置如果有格子, 就游戏结束
        val cells = nextOne?.cells
        cells?.forEachIndexed { index, cell ->
            val row = cell?.row
            val col = cell?.col
            if (mWall[row!!][col!!] != null) {
                if (mTetrisListener!=null)mTetrisListener?.gameOver()
                return true
            }

        }
        return false
    }

    //監聽游戲結束
    private var mTetrisListener:TetrisListener?=null
    interface TetrisListener{
        fun gameOver()
    }
    fun setTetrisListener(listener:TetrisListener){
        mTetrisListener=listener
    }

    //向左移動
    fun moveLeftAction() {
        tetromino?.moveLeft()
        // coincode: 重合 检查4格方块与墙是否重合
        if (outOfBounds() || coincide()) {
            tetromino?.moveRight()
        }
    }

    /** 检查 (正在下落的方块)是否超出边界  */
    private fun outOfBounds(): Boolean {
        // 正在下落的方块的某个格子出界, 就是出界
        val cells = tetromino?.cells
        cells?.apply {
            forEachIndexed { index, cell ->
                val row = cell?.row
                val col = cell?.col
                row?.let {
                    col?.let {
                        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                            return true
                        }
                    }

                }
            }
        }
        return false
    }

    /** 检查重合  */
    private fun coincide(): Boolean {
        val cells = tetromino?.cells
        cells?.forEachIndexed { index, cell ->
            cell?.apply {
                if (mWall[row][col]!=null)return true
            }
        }
        return false
    }

    /** 旋轉方塊方法  */
    fun rotateRightAction() {
        tetromino?.rotateRight()
        if (outOfBounds() || coincide()) {
            tetromino?.rotateLeft()
        }
    }

    /** Tetris 类中添加方法  */
    fun moveRightAction() {
        // 正在下落的方块右移动
        tetromino?.moveRight()
        // 如果(正在下落的方块)超出边界(Bounds)
        if (outOfBounds() || coincide()) {
            // 正在下落的方块左移动
            tetromino?.moveLeft()
        }
    }

    /** 硬下落, 一下到底  */
    fun hardDropAction() {
        while (canDrop()) {
            tetromino?.softDrop()
        }
        landIntoWall()
        val lines = destroyLines()
        this.lines += lines
        this.score += scoreTable[lines]
        if (isGameOver()) {
            state = GAME_OVER
        } else {
            tetromino = nextOne
            nextOne = Tetromino.randomOne(context)
        }
    }

    fun onceAgain(){
        /** 游戏重新开始 */
        lines = 0
        score = 0
        mWall =   Array(ROWS, { Array<Cell?>(COLS, { null }) })
        tetromino = Tetromino.randomOne(context)
        nextOne = Tetromino.randomOne(context)
        state = RUNNING
        index = 0
    }

}


