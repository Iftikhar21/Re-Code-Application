package com.example.recodeapplication

import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class SchoolMapActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var matrix = Matrix()
    private var savedMatrix = Matrix()
    private var mode = 0

    private var startX = 0f
    private var startY = 0f
    private var oldDistance = 1f
    private var minScale = 1f
    private var maxScale = 5f

    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.school_map)

        val btnBack = findViewById<FloatingActionButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        imageView = findViewById(R.id.imageView5)
        imageView.scaleType = ImageView.ScaleType.MATRIX
        imageView.imageMatrix = matrix

        imageView.post {
            val drawable = imageView.drawable
            if (drawable != null) {
                val imageWidth = drawable.intrinsicWidth.toFloat()
                val imageHeight = drawable.intrinsicHeight.toFloat()
                val viewWidth = imageView.width.toFloat()
                val viewHeight = imageView.height.toFloat()

                val scale = min(viewWidth / imageWidth, viewHeight / imageHeight)
                minScale = scale
                matrix.setScale(scale, scale)
                val dx = (viewWidth - imageWidth * scale) / 2f
                val dy = (viewHeight - imageHeight * scale) / 2f
                matrix.postTranslate(dx, dy)
                imageView.imageMatrix = matrix
            }
        }

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
                            val values = FloatArray(9)
                            matrix.getValues(values)
                            val currentScale = values[Matrix.MSCALE_X]
                            val newScale = max(minScale, min(currentScale * scale, maxScale))
                            val factor = newScale / currentScale
                            matrix.postScale(factor, factor, imageView.width / 2f, imageView.height / 2f)
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                    limitZoomAndTranslate()
                }
            }
            imageView.imageMatrix = matrix
            true
        }
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun limitZoomAndTranslate() {
        val values = FloatArray(9)
        matrix.getValues(values)
        val currentScale = values[Matrix.MSCALE_X]
        if (currentScale < minScale) {
            matrix.setScale(minScale, minScale)
        }

        val bounds = RectF()
        imageView.drawable?.let {
            bounds.set(0f, 0f, it.intrinsicWidth.toFloat(), it.intrinsicHeight.toFloat())
            matrix.mapRect(bounds)
        }
        imageView.imageMatrix = matrix
    }
}
