package com.example.voicexxfire0629

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.voicexxfire0629.databinding.ActivityRcBinding
import java.io.File


class RcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRcBinding
    lateinit var profileAdapter: ProfileAdapter
    val datas = mutableListOf<ProfileData>()
//    val nameList = listOf("mary", "jenny", "jhon", "ruby", "yuna")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRcBinding.inflate(layoutInflater)
        setContentView(binding.root)
//버튼 변수 선언
        val btn_rc_1: Button = findViewById(R.id.btn_rc_1)


        initRecycler()

        btn_rc_1.setOnClickListener{
            val intent2 = Intent(this, SubActivity::class.java)
            startActivity(intent2)
        }
    }

    private fun initRecycler() {
        //파일리스트 불러온후 변수저장
        val txtFiles = getTxtFilesFromExternalCache()


        profileAdapter = ProfileAdapter(this)
        binding.rvProfile.adapter = profileAdapter

        for (i in txtFiles) {
// ProfileData 클래스에서 이미지 리소스 아이디가 Int 타입으로 정의되어 있다고 가정합니다.
            datas.add(ProfileData(img = R.drawable.baseline_article_24, name = i.toString()))

        }

        binding.rvProfile.addItemDecoration(VerticalItemDecorator(20))
        binding.rvProfile.addItemDecoration(HorizontalItemDecorator(10))

        profileAdapter.datas = datas
        profileAdapter.notifyDataSetChanged()
    }


    private fun getTxtFilesFromExternalCache(): List<String> {
        val txtFiles = mutableListOf<String>()

        val externalCacheDir = externalCacheDir
        if (externalCacheDir != null && externalCacheDir.exists()) {
            val files = externalCacheDir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile && file.extension.equals("txt", true)) {
                        // 문자열을 ","를 기준으로 분할하여 배열로 만듭니다.
                        val stringArray = file.toString().split("/")

                        // 마지막 배열 요소를 선택합니다.
                        val file = stringArray.last()

                        txtFiles.add(file) // 파일 이름만 추가
                    }
                }
            }
        }

        return txtFiles
    }
}




class VerticalItemDecorator(private val divHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = divHeight
        outRect.bottom = divHeight
    }
}

class HorizontalItemDecorator(private val divHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = divHeight
        outRect.right = divHeight
    }
}
