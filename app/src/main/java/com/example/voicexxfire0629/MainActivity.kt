package com.example.voicexxfire0629

//<a href="https://www.flaticon.com/kr/free-icons/" title="마이크로폰 아이콘">마이크로폰 아이콘  제작자: Freepik - Flaticon</a>

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.translation.Translator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.voicexxfire0629.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation

import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException

//글로벌 변수 선언
object GlobalData {
    val list1 = mutableListOf<String>()
}


class MainActivity : AppCompatActivity() {

    //광고변수전역선언
    lateinit var mAdView : AdView


    //api키 전역선언
    private lateinit var key: String
    // Rest of your code
//    바인딩 추가 초기화
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    //전역번역 관련 변수
    private lateinit var translatorentokorean: com.google.mlkit.nl.translate.Translator
    private lateinit var translatorkoreantoen: com.google.mlkit.nl.translate.Translator
    private var booleanentokorean = false
    private var booleankoreantoen = false

    //음성 코드
    private val PERMISSIONS_RECORD_AUDIO = 1
    private val RC_SPEECH_RECOGNIZER = 2

    private lateinit var tv1: TextView
    private lateinit var tv4: TextView
    private lateinit var tv5: TextView
    //한영 버튼 변수 1이면 한영  2이면 영한
    private  var kor = 1
    //편집 텍스트
    private var et1: EditText? = null
    private var et2: EditText? = null
    private var et3: EditText? = null
    //speak 변수
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        //광고 초기화
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        // 저장된 key 값 가져오기
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        key = sharedPreferences.getString("key", "") ?: ""

        //변수선언
        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        et3 = findViewById(R.id.et3)
        tv1 = findViewById(R.id.tv1)
        tv4 = findViewById(R.id.tv4)
        tv5 = findViewById(R.id.tv5)

/////////////변수선언
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

        ///////////////////////////텍스트번역///////////////////////////////////////
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
///////////////////////speak 변수
        // TextToSpeech 초기화
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
            }
        }
        // TTS 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }


//////////////////////////////////버튼 설정
        //////////////////////////////////버튼 설정
        //메뉴버튼
        binding.btnmenu.setOnClickListener{
            startActivity(intent)
        }
        //  영어 체이지 버튼 클릭시
        btn_current_location.setOnClickListener{
            if(kor == 1){
                tv4.text = "영어"
                tv5.text = "한글"
                kor = 2
            }else{
                tv4.text = "한글"
                tv5.text = "영어"
                kor = 1
            }
        }
        //지우기 버튼 (et1,2,3 전부 지울것인가  나머지 초기화할 내용은?
        btn_clear.setOnClickListener{
            et1?.setText("")
            tv4.text = "한글"
            tv5.text = "영어"
            tv1.text = "---"
            kor = 1
        }
        //번역버튼
        btn_trans.setOnClickListener {
            if (kor == 1){
                transkortoen()
            }
            else{
                transentokor()
            }
        }

        //버튼 듣기1
        btn_speak1.setOnClickListener{
            speak1()

        }
        //버튼 듣기2
        btn_speak2.setOnClickListener{
            speak2()
        }
        //버튼 ai
        btn_ai.setOnClickListener{
            ai()
        }
        //버튼 보여주기
        btn_show.setOnClickListener{
            val dialogFragment = CustomDialogFragment()
            dialogFragment.show(supportFragmentManager, "CustomDialogFragment")
        }
        ////
        btn_voice.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
            } else {

                startSpeechToText()
            }
        }
    }


    // 화면 클릭하여 키보드 숨기기 및 포커스 제거
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tv1.text = "준비"
                startSpeechToText()
            }else {
                tv1.text = "권한 거부됨"
            }
        }
    }


    private fun startSpeechToText() {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        if (kor == 1) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN)
        } else if (kor == 2) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음성을 입력하세요.")
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // 음성 입력 준비가 완료되었을 때 호출됩니다.
                tv1.text = "녹음중"
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
                tv1.text = "enkodown"
            }
            override fun onError(error: Int) {
                // 음성 입력 중 오류가 발생했을 때 호출됩니다.
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val inputText = matches[0]
                    if (kor == 1){
                        et1?.setText(inputText.toString())
                        transkortoen1(inputText)

                    }
                    else{
                        et1?.setText(inputText.toString())
                        transentokor1(inputText)
                    }
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
                tv1.text = "endown"
            }
            .addOnFailureListener {
                println("1111111111111111")
                booleankoreantoen = false
            }
        translatorentokorean.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                booleanentokorean = true
                tv1.text = "enkodown"
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
                    // 현재 날짜 구하기
                    val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    // 텍스트 파일 저장 경로
                    val filePath = "${externalCacheDir?.absolutePath}/$currentDate.txt"
                    // 변수 x의 값을 텍스트 파일에 저장
                    File(filePath).writeText(translatedText.toString())
                    //글로벌변수에 추가
                    GlobalData.list1.add("$translatedText")

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
                    // 현재 날짜 구하기
                    val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    // 텍스트 파일 저장 경로
                    val filePath = "${externalCacheDir?.absolutePath}/$currentDate.txt"
                    // 변수 x의 값을 텍스트 파일에 저장
                    File(filePath).writeText(translatedText.toString())
                    //글로벌변수에 추가
                    GlobalData.list1.add("$translatedText")

                }
                .addOnFailureListener { exception ->
                    et2?.setText(exception.toString())
                }
        }

    }
    private  fun transkortoen1(inputtext:String){

        if (booleankoreantoen) {
            translatorkoreantoen.translate(inputtext.toString())
                .addOnSuccessListener{ translatedText ->
                    et2?.setText(translatedText.toString())
                    // 현재 날짜 구하기
                    val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    // 텍스트 파일 저장 경로
                    val filePath = "${externalCacheDir?.absolutePath}/$currentDate.txt"
                    // 변수 x의 값을 텍스트 파일에 저장
                    File(filePath).writeText(translatedText.toString())
                    //글로벌변수에 추가
                    GlobalData.list1.add("$translatedText")

                }
                .addOnFailureListener { exception ->

                    et2?.setText(exception.toString())
                }
        }

    }
    private  fun transentokor1(inputtext:String){
        if (booleanentokorean) {
            translatorentokorean.translate(inputtext.toString())
                .addOnSuccessListener { translatedText ->
                    et2?.setText(translatedText.toString())
                    // 현재 날짜 구하기
                    val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    // 텍스트 파일 저장 경로
                    val filePath = "${externalCacheDir?.absolutePath}/$currentDate.txt"
                    // 변수 x의 값을 텍스트 파일에 저장
                    File(filePath).writeText(translatedText.toString())
                    //글로벌변수에 추가
                    GlobalData.list1.add("$translatedText")

                }
                .addOnFailureListener { exception ->

                    et2?.setText(exception.toString())
                }
        }

    }



///엑티비티 종료전 스피크 오디오 초기화
    override fun onDestroy() {
        super.onDestroy()
        // TextToSpeech 해제
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
//////스피크 함수 실행
    private fun speak1() {
        val text = et2?.text.toString()

        when (kor) {
            2 -> {
                // 한글로 음성 출력
                textToSpeech.language = Locale.KOREAN
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                textToSpeech.setPitch(0.7f)
                textToSpeech.setSpeechRate(0.8f)
            }
            1 -> {
                // 영어로 음성 출력
                textToSpeech.language = Locale.US
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                textToSpeech.setPitch(0.7f)
                textToSpeech.setSpeechRate(0.8f)
            }
        }
    }
    private fun speak2() {
        val text = et3?.text.toString()

        when (kor) {
            2 -> {
                // 한글로 음성 출력
                textToSpeech.language = Locale.KOREAN
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
            1 -> {
                // 영어로 음성 출력
                textToSpeech.language = Locale.US
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    // ai 함수
    fun ai() {
        val inputT = et2?.text.toString()
        val inputText = "최대한 짧게 답해"+ "$inputT"
        et3?.setText("기다리시오")



        GlobalScope.launch(Dispatchers.IO) {
            try {
                // ChatGPT API에 요청을 보내고 응답을 반환
                val responseText = sendRequestToChatGPT(inputText)

                // 챗 GPT API의 응답을 처리하여 결과를 얻음
                val answer = processChatGPTResponse(responseText)


                // UI 업데이트는 메인 스레드에서 수행
                withContext(Dispatchers.Main) {
                    et3?.setText(answer)


                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // ChatGPT API에 요청을 보내고 응답을 반환하는 함수
    fun sendRequestToChatGPT(inputText: String): String {
        val client = OkHttpClient()
        val url = "https://api.openai.com/v1/chat/completions" // ChatGPT API의 엔드포인트 URL로 대체해야 함
        val mediaType = "application/json".toMediaType()

        val messages = JSONArray().apply {
            // 사용자 메시지 추가
            put(JSONObject().apply {
                put("role", "user")
                put("content", inputText)
            })
            // 시스템 메시지 추가 (선택사항)
            // put(JSONObject().apply {
            //     put("role", "system")
            //     put("content", "System message goes here")
            // })
        }

        val json = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", messages)
        }

        val requestBody = json.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $key")
            .post(requestBody)
            .build()

        val response: Response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    // ChatGPT API의 응답을 처리하고 결과를 반환하는 함수
    fun processChatGPTResponse(responseText: String): String {
        try {
            val jsonResponse = JSONObject(responseText)
            val choicesArray = jsonResponse.getJSONArray("choices")
            if (choicesArray.length() > 0) {
                val firstChoice = choicesArray.getJSONObject(0)
                val message = firstChoice.getJSONObject("message")
                val content = message.getString("content")
                return content
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return ""
    }


}

class CustomDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Dialog의 레이아웃을 설정합니다.
        return inflater.inflate(R.layout.activity_sssss, container, false)
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 크기를 현재 창의 1/2로 설정합니다.
        val width = (resources.displayMetrics.widthPixels * 1).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        dialog?.window?.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 텍스트 파일을 읽어와서 TextView에 표시합니다.
        val textView = view.findViewById<TextView>(R.id.tv_s1)
        val stringBuilder = StringBuilder()
        for (item in GlobalData.list1) {
            stringBuilder.append(item).append("\n")
        }
        val fontSize = 20 // 원하는 폰트 크기로 설정
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())
        textView.text = stringBuilder
    }
}





