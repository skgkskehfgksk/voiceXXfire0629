package com.example.voicexxfire0629

//<a href="https://www.flaticon.com/kr/free-icons/" title="마이크로폰 아이콘">마이크로폰 아이콘  제작자: Freepik - Flaticon</a>

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.translation.Translator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voicexxfire0629.databinding.ActivityMainBinding

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation

import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*

class MainActivity : AppCompatActivity() {
//    바인딩 추가 초기화
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //번역 관련 변수
    private lateinit var translatorentokorean: com.google.mlkit.nl.translate.Translator
    private lateinit var translatorkoreantoen: com.google.mlkit.nl.translate.Translator
    private var booleanentokorean = false
    private var booleankoreantoen = false

    //음성 코드
    private val PERMISSIONS_RECORD_AUDIO = 1
    private val RC_SPEECH_RECOGNIZER = 2

    private lateinit var tv4: TextView
    private lateinit var tv5: TextView
    //한영 버튼 변수 1이면 한영  2이면 영한
    private  var kor = 1
    //편집 텍스트
    private var et1: EditText? = null
    private var et2: EditText? = null
    private var et3: EditText? = null






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



//        setContentView(R.layout.activity_main)

        setContentView(binding.root)

        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        et3 = findViewById(R.id.et3)
        tv4 = findViewById(R.id.tv4)
        tv5 = findViewById(R.id.tv5)

//      메뉴 버튼 클릭시 새창으로 이동
        var intent = Intent(this, SubActivity::class.java)

        val btnmenu : Button = findViewById(R.id.btnmenu)
        val btn_voice: Button = findViewById(R.id.btn_voice)
        val btn_current_location : ImageButton = findViewById(R.id.btn_current_location)
        val btn_trans: Button = findViewById(R.id.btn_trans)
        val btn_clear: Button = findViewById(R.id.btn_clear)
        val btn_speak1: Button = findViewById(R.id.btn_speak1)
        val btn_speak2: Button = findViewById(R.id.btn_speak2)
        val btn_show: Button = findViewById(R.id.btn_show)
        val btn_ai: Button = findViewById(R.id.btn_ai)



        ///////////////////////////텍스트입출력///////////////////////////////////////
        val translatorOptionsKorean = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.KOREAN)
            .build()
        val translatorOptionsenlish = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.KOREAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        translatorkoreantoen = Translation.getClient(translatorOptionsenlish)
        translatorentokorean = Translation.getClient(translatorOptionsKorean)
        downloadModel()
//////////////////////////////////버튼 설정
        //메뉴버튼
        binding.btnmenu.setOnClickListener{
            startActivity(intent)
        }
        //  영어 체이지 버튼 클릭시
        btn_current_location.setOnClickListener{
            if(tv4.text === "한글"){
                tv4.text = "영어"
                tv5.text = "한글"
                kor = 1
            }else {
                tv4.text = "한글"
                tv5.text = "영어"
                kor = 0
            }


        }
        //지우기 버튼
        btn_clear.setOnClickListener{
            et1?.setText("")
//            kor = 1
        }
        //번역버튼
        btn_trans.setOnClickListener {
            if (kor === 1){
                transkortoen()
            }
            else if (kor ===2){
                transentokor()
            }
        }
        //음성입력버튼
        btn_voice.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
            } else {
                startSpeechToText()
            }
        }
        //버튼 듣기1
        btn_speak1.setOnClickListener{

        }
        //버튼 듣기2
        btn_speak2.setOnClickListener{

        }
        //버튼 ai
        btn_ai.setOnClickListener{

        }
        //버튼 보여주기
        btn_show.setOnClickListener{

        }


        /////////////////////////////////////////////////////////////////////





// 버튼1 클릭시 음성녹음 후 텍스트 변환
//        button1 = findViewById(R.id.button1)
//        text1 = findViewById(R.id.text1)




        btn_voice.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
            } else {
                startSpeechToText()
            }
        }

    }
    //
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechToText()
            }
        }
    }


    private fun startSpeechToText() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음성을 입력하세요.")
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // 음성 입력 준비가 완료되었을 때 호출됩니다.

            }
            override fun onBeginningOfSpeech() {
                // 음성 입력이 시작될 때 호출됩니다.
            }

            override fun onRmsChanged(rmsdB: Float) {
                // 음성 입력의 소리 크기가 변경될 때 호출됩니다.
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // 음성 입력에 대한 buffer를 받을 때 호출됩니다.
            }

            override fun onEndOfSpeech() {
                // 음성 입력이 종료될 때 호출됩니다.

            }

            override fun onError(error: Int) {
                // 음성 입력 중 오류가 발생했을 때 호출됩니다.

            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val inputText = matches[0]
                    et2?.setText("번역 모델 다운로드 실패")
                    translateText1(inputText)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // 부분 결과를 받을 때 호출됩니다.
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // 추가 이벤트를 받을 때 호출됩니다.
            }
        })
        speechRecognizer.startListening(intent)
    }

    private fun downloadModel() {
        val downloadConditions = DownloadConditions.Builder().build()

        translatorkoreantoen.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                booleankoreantoen = true
                println("222222222222222222222222")
            }
            .addOnFailureListener {
                println("1111111111111111")
                booleankoreantoen = false
            }
        translatorentokorean.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                booleanentokorean = true
                println("222222222222222222222222")
            }
            .addOnFailureListener {
                println("1111111111111111")
                booleanentokorean = false
            }
    }
    private  fun transkortoen(){

        if (booleankoreantoen) {
            translatorkoreantoen.translate(et1?.text.toString())
                .addOnSuccessListener{ translatedText ->
                    et2?.setText(translatedText.toString())

                }
                .addOnFailureListener { exception ->
                    et2?.setText(exception.toString())
                }
        }

    }
    private  fun transentokor(){
        if (booleanentokorean) {
            translatorentokorean.translate(et1?.text.toString())
                .addOnSuccessListener { translatedText ->
                    et2?.setText(translatedText.toString())

                }
                .addOnFailureListener { exception ->
                    et2?.setText(exception.toString())
                }
        }

    }

    private fun translateText1(inputText: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.KOREAN)
            //대상 언어 선택
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        val downloadTask = translator.downloadModelIfNeeded(conditions)
        downloadTask.addOnSuccessListener {
            val translateTask = translator.translate(inputText)
            translateTask.addOnSuccessListener { translatedText ->
                // 번역된 텍스트를 표시합니다.
                et2?.setText(translatedText.toString())
            }

        }
    }
//    private fun translateEnglishToKorean(inputText: String) {
//        val options = TranslatorOptions.Builder()
//            .setSourceLanguage(TranslateLanguage.KOREAN)
//            //대상 언어 선택
//            .setTargetLanguage(TranslateLanguage.ENGLISH)
//            .build()
//        val translator = Translation.getClient(options)
//
//        val conditions = DownloadConditions.Builder()
//            .requireWifi()
//            .build()
//
//        val downloadTask = translator.downloadModelIfNeeded(conditions)
//        downloadTask.addOnSuccessListener {
//            val translateTask = translator.translate(inputText)
//            translateTask.addOnSuccessListener { translatedText ->
//                // 번역된 텍스트를 표시합니다.
//                et2.setText(translatedText.toString())
//            }
//
//        }
//
//    }
//    private fun translateKoreanToEnglish(inputText: String) {
//        val options = TranslatorOptions.Builder()
//            .setSourceLanguage(TranslateLanguage.ENGLISH)
//            //대상 언어 선택
//            .setTargetLanguage(TranslateLanguage.KOREAN)
//            .build()
//        val translator = Translation.getClient(options)
//
//        val conditions = DownloadConditions.Builder()
//            .requireWifi()
//            .build()
//
//        val downloadTask = translator.downloadModelIfNeeded(conditions)
//        downloadTask.addOnSuccessListener {
//            val translateTask = translator.translate(inputText)
//            translateTask.addOnSuccessListener { translatedText ->
//                // 번역된 텍스트를 표시합니다.
//                et2.setText(translatedText.toString())
//            }
//
//        }
//
//    }
}







