package com.example.voicexxfire0629

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)


        val btn_pp_1: Button = findViewById(R.id.btn_pri_1)
        val tv_pp_1: TextView = findViewById(R.id.tv_pri_1)
        btn_pp_1.setOnClickListener{
            val intent6 = Intent(this, SubActivity::class.java)
            startActivity(intent6)
        }
    }
}