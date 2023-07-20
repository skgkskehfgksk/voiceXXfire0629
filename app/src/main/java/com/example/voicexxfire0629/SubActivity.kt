package com.example.voicexxfire0629

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.voicexxfire0629.databinding.ActivitySubBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.io.File


class SubActivity : AppCompatActivity() {

    //광고변수전역선언
    lateinit var mAdView : AdView
    //뷰 바인딩
    val binding by lazy { ActivitySubBinding.inflate(layoutInflater) }
    //키 변수
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        setContentView(R.layout.activity_sub)


        //광고 초기화
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
//////////////키 변수 선언
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        key = sharedPreferences.getString("key", "") ?: ""
//구성요소 선언
        val tv_sub1: TextView = findViewById(R.id.tv_sub1)
        val tv_sub2: TextView = findViewById(R.id.tv_sub2)
        val et_sub1: EditText = findViewById(R.id.et_sub1)
        val btnback: Button = findViewById(R.id.btnback)
        val btn_history: Button = findViewById(R.id.btn_history)
        val btn_api: Button = findViewById(R.id.btn_api)
        val btn_privacy: Button = findViewById(R.id.btn_privacy)

// 돌아가기 버튼 클릭시 스타트엑티비티 intent
        binding.btnback.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

//api 버튼 클릭시 키값을 영구히 저장하고 후 tv_sub2표시
        btn_api.setOnClickListener {
            key = et_sub1.text.toString()

            // key 값을 영구히 저장
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("key", key)
            editor.apply()

            tv_sub2.text = key
        }
        // 히스토리 버튼 클리시 다이오로그프레그먼트1함수실행후 변수 저장후 보여주기
        btn_history.setOnClickListener {
            val intent = Intent(this, RcActivity::class.java)
            startActivity(intent)


        }
        //개인보호정책 버튼 클릭시
        btn_privacy.setOnClickListener {

        }


    }

}

//
//class CustomDialogFragment_1 : DialogFragment() {
//    private lateinit var fileListLayout: LinearLayout
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        ////다이얼 화면 크기
//
//
//        val dialog = super.onCreateDialog(savedInstanceState)
//
//        //다이얼로그 위치 왼쪽
//        dialog.window?.setGravity(Gravity.START)
//
//
//
//
//        val contentView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_content, null)
//        fileListLayout = contentView.findViewById(R.id.filelistlayout)
//
//
//
//
////다이얼로그 실행
//        dialog.setContentView(contentView)
//
//
//        //다이얼 로그 넑이 높이 변수 선언
//        val width = (resources.displayMetrics.widthPixels * 0.5).toInt()
//        val height = (resources.displayMetrics.heightPixels * 0.5).toInt()
//        //다이얼로그 넓이 높이 변수 값으로
//        dialog.window?.setLayout(width, height)
//        //        // 타이틀바에 표시할 이름 설정
//        dialog.setTitle("파일 모음")
////외부캐시에 저장된 파일 불러오기
//        val txtFiles = getTxtFilesFromExternalCache()
//
//        val sortedFiles = txtFiles.sortedByDescending { it.lastModified() }
//        val displayFiles = sortedFiles.take(100) // 최대 100개 파일만 표시
//
//
//
//        for (file in displayFiles) {
//            val textView = TextView(requireContext())
//            textView.text = file.name
//            textView.setOnClickListener {
//                displayFileContent(file)
////                dialog.dismiss()
//            }
//            fileListLayout.addView(textView)
//        }
//
//        return dialog
//    }
//
////    private fun displayFileContent(file: File) {
////        val content = file.readText()
////        Toast.makeText(dialog?.context, content, Toast.LENGTH_SHORT).show()
////
////
////    }
//    private fun displayFileContent(file: File) {
//        if (file.exists()) {
//            val content = file.readText()
//            Toast.makeText(requireContext(), content, Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(requireContext(), "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    private fun getTxtFilesFromExternalCache(): List<File> {
//        val externalCacheDir = requireContext().externalCacheDir ?: return emptyList()
//
//        val txtFiles = mutableListOf<File>()
//
//        val files = externalCacheDir.listFiles()
//        if (files != null) {
//            for (file in files) {
//                if (file.isFile && file.extension == "txt") {
//                    txtFiles.add(file)
//                }
//            }
//        }
//
//        return txtFiles
//    }
//}




