package com.example.recodeapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoCaptureActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var captureButton: FloatingActionButton
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var capturedImageView: ImageView
    private lateinit var switchCameraButton: TextView
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_capture)

        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.captureButton)
        capturedImageView = findViewById(R.id.capturedImageView)
        switchCameraButton = findViewById(R.id.switchCameraButton)
        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera(cameraSelector)

        val keterangan = intent.getStringExtra("keterangan").toString()


        captureButton.setOnClickListener {
            takePhoto(keterangan)
        }
        // Switch camera functionality
        switchCameraButton.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera(cameraSelector)
        }
    }

    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(this, "Gagal memulai kamera.", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(keterangan: String) {
        val file = File(externalMediaDirs.firstOrNull(), "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(this@PhotoCaptureActivity, "Foto berhasil diambil!", Toast.LENGTH_SHORT).show()
                    photoPath = file.absolutePath
                    showMoodSelection(keterangan) // Tambahkan keterangan
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@PhotoCaptureActivity, "Gagal mengambil foto.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showMoodSelection(keterangan: String) {
        val moodToast = MoodDialog(this)
        moodToast.setOnMoodSelectedListener { moodIndex ->
            val intent = Intent(this@PhotoCaptureActivity, NoteActivity::class.java).apply {
                putExtra("photo_path", photoPath)
                putExtra("mood_index", moodIndex)
                putExtra("keterangan", keterangan) // Meneruskan keterangan
            }
            startActivity(intent)
            finish()
        }
        moodToast.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}