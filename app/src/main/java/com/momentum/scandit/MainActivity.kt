package com.momentum.scandit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.momentum.scandit.data.MatrixScanActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            startActivity(Intent(this, MatrixScanActivity::class.java))
            finish()
//            Toast.makeText(this, "START", Toast.LENGTH_SHORT).show()
        }, 1500)

    }
}