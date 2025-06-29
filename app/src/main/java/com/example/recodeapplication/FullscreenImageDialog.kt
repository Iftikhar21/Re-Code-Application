package com.example.recodeapplication

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_fullscreen_image)

        val imageView = findViewById<ImageView>(R.id.fullscreenImageView)
        val imageUrl = intent.getStringExtra("IMAGE_URL")

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        imageView.setOnClickListener {
            finish()
        }
    }
}
