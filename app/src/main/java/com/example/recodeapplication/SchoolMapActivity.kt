package com.example.recodeapplication

import android.graphics.Matrix
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SchoolMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.school_map) // sesuaikan dengan layout Anda

        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)

        val imageMap = findViewById<ImageView>(R.id.imageView5)
        val matrix = Matrix()
        val scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scale = detector.scaleFactor
                matrix.postScale(scale, scale, detector.focusX, detector.focusY)
                imageMap.imageMatrix = matrix
                return true
            }
        })

        var lastX = 0f
        var lastY = 0f
        var isDragging = false

        imageMap.setOnTouchListener { v, event ->
            scaleGestureDetector.onTouchEvent(event)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                    isDragging = true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) {
                        val dx = event.x - lastX
                        val dy = event.y - lastY
                        matrix.postTranslate(dx, dy)
                        imageMap.imageMatrix = matrix
                        lastX = event.x
                        lastY = event.y
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isDragging = false
                }
            }
            true
        }


        btnBack.setOnClickListener{
            finish()
        }

    }
}