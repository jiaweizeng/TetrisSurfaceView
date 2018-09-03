package com.example.admin.balatetris

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val surfaceView by lazy {
        findViewById<TetrisSurfaceView>(R.id.tetrisSv)
    }
    private val left by lazy {
        findViewById<Button>(R.id.left)
    }
    private val up by lazy {
        findViewById<Button>(R.id.up)
    }
    private val right by lazy {
        findViewById<Button>(R.id.right)
    }
    private val pause by lazy {
        findViewById<Button>(R.id.pause)
    }
    private val down by lazy {
        findViewById<Button>(R.id.down)
    }
    private val hardDrop by lazy {
        findViewById<Button>(R.id.hardDrop)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        left.setOnClickListener(this)
        up.setOnClickListener(this)
        right.setOnClickListener(this)
        pause.setOnClickListener(this)
        down.setOnClickListener(this)
        hardDrop.setOnClickListener(this)
        surfaceView.setTetrisListener(object :TetrisSurfaceView.TetrisListener{
            override fun gameOver() {
                pause.text = "once again"
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            left -> surfaceView.moveLeftAction()
            up -> surfaceView.rotateRightAction()
            right -> surfaceView.moveRightAction()
            down -> surfaceView.softDropAction()
            pause -> {
                surfaceView.apply {
                    when (state) {
                        RUNNING -> {
                            state = PAUSE
                            pause.text = "continue"
                        }
                        PAUSE -> {
                            state = RUNNING
                            index = 0
                            pause.text = "pause"
                        }
                        GAME_OVER-> {
                            onceAgain()
                            pause.text = "pause"
                        }
                    }
                }
            }
            hardDrop-> surfaceView.hardDropAction()

        }
    }
}

