package com.example.voicexxfire0629


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.bumptech.glide.Glide
import com.example.voicexxfire0629.databinding.ActivityProfileDetailBinding // (yourappname은 앱의 패키지 이름에 맞게 수정)

class ProfileDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailBinding
    private lateinit var datas: ProfileData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //버튼 변수 선언
        val btn_pd_1: Button = findViewById(R.id.btn_pd_1)

        btn_pd_1.setOnClickListener{
            val intent3 = Intent(this, RcActivity::class.java)
            startActivity(intent3)
        }

        datas = intent.getSerializableExtra("data") as? ProfileData ?: return


        Glide.with(this).load(datas.img).into(binding.imgProfile)
        binding.tvName.text = datas.name
        binding.tvP1.text = "힘들다"
    }
}