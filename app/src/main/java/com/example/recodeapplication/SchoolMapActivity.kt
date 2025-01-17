package com.example.recodeapplication

import android.graphics.Matrix
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SchoolMapActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var matrix = Matrix()
    private var savedMatrix = Matrix()
    private var mode = 0

    private var startX = 0f
    private var startY = 0f
    private var oldDistance = 1f

    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.school_map) // sesuaikan dengan layout Anda

        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)

        btnBack.setOnClickListener{
            finish()
        }

        imageView = findViewById(R.id.imageView5)
        imageView.scaleType = ImageView.ScaleType.MATRIX
        imageView.imageMatrix = matrix

        imageView.setOnTouchListener { _, event ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    savedMatrix.set(matrix)
                    startX = event.x
                    startY = event.y
                    mode = DRAG
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDistance = spacing(event)
                    if (oldDistance > 10f) {
                        savedMatrix.set(matrix)
                        mode = ZOOM
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        matrix.set(savedMatrix)
                        matrix.postTranslate(event.x - startX, event.y - startY)
                    } else if (mode == ZOOM) {
                        val newDistance = spacing(event)
                        if (newDistance > 10f) {
                            matrix.set(savedMatrix)
                            val scale = newDistance / oldDistance
                            matrix.postScale(scale, scale, imageView.width / 2f, imageView.height / 2f)
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                }
            }
            imageView.imageMatrix = matrix
            true
        }
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }
}
