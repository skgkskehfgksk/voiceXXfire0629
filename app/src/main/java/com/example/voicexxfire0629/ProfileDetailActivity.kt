package com.example.voicexxfire0629


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.bumptech.glide.Glide
import com.example.voicexxfire0629.databinding.ActivityProfileDetailBinding // (yourappname은 앱의 패키지 이름에 맞게 수정)
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class ProfileDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailBinding
    private lateinit var datas: ProfileData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //버튼 변수 선언
        val btn_pd_1: Button = findViewById(R.id.btn_pd_1)
        //버튼 클릭시
        btn_pd_1.setOnClickListener{
            // intent 엑티비티 선언
            val intent3 = Intent(this, RcActivity::class.java)
            // 실행
            startActivity(intent3)
        }
        //profiledata 값을 받아와서 datas 저장
        datas = intent.getSerializableExtra("data") as? ProfileData ?: return


        // 클릭된 파일의 내용을 읽어와서 표시합니다.
        val fileContents = readFileContents(datas.name) // name 필드에 파일 경로가 들어있다고 가정합니다.
        if (!fileContents.isNullOrBlank()) {
            binding.tvP1.text = fileContents
        } else {
            binding.tvP1.text = "파일 내용을 읽을 수 없습니다."
        }

        Glide.with(this).load(datas.img).into(binding.imgProfile)
        // 문자열을 ","를 기준으로 분할하여 배열로 만듭니다.
        val stringArray = datas.name.toString().split("/")

        // 마지막 배열 요소를 선택합니다.
        val t = stringArray.last()
        binding.tvName.text = t
    }
    private fun readFileContents(filePath: String): String? {
        return try {
            val file = File(filePath)
            val bufferedReader = BufferedReader(FileReader(file))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            bufferedReader.close()
            stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}