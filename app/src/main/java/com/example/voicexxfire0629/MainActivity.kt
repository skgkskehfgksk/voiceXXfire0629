package com.example.voicexxfire0629

//<a href="https://www.flaticon.com/kr/free-icons/" title="마이크로폰 아이콘">마이크로폰 아이콘  제작자: Freepik - Flaticon</a>

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException

//글로벌 변수 선언
object GlobalData {
    val list1 = mutableListOf<String>()
}

// Define the callback interface outside of any class
interface SpeechRecognitionCallback {
    fun onSpeechRecognitionCompleted(inputText: String)
}

class MainActivity : AppCompatActivity() {
    //알림창 시작시 api키입력
    private val PREF_NAME = "MyPreferences"
    private val PREF_DIALOG_SHOWN = "DialogShown"
    private val PREF_DIALOG_TIME = "DialogTime"

    //et선택 번호 지정
    private var selectedEditTextId: Int = 0

    // Add a global variable to track whether speech is in progress
    private var isSpeechInProgress = false

    //광고변수전역선언
    lateinit var mAdView: AdView


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
    private var kor = 1

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

        // Check if the dialog has been shown before충돌주의
        val sharedPreferences1 = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isDialogShown = sharedPreferences1.getBoolean(PREF_DIALOG_SHOWN, false)
        if (!isDialogShown) {
            showCustomDialog()
        }

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
        val btnmenu: Button = findViewById(R.id.btnmenu)
        val btn_voice: Button = findViewById(R.id.btn_voice)
        val btn_current_location: ImageButton = findViewById(R.id.btn_current_location)
        val btn_trans: Button = findViewById(R.id.btn_trans)
        val btn_clear: Button = findViewById(R.id.btn_clear)
        val btn_speak1: Button = findViewById(R.id.btn_speak1)
        val btn_speak2: Button = findViewById(R.id.btn_speak2)
        val btn_show: Button = findViewById(R.id.btn_show)
        val btn_ai: Button = findViewById(R.id.btn_ai)
        //화살표 선언
        var rotationCount = 0


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


        // 단어를 클릭시 번역  벡터 이미지를 클릭하면 해당 단어 발음 실행
        et1?.setOnLongClickListener {
            // 긴 클릭 이벤트 처리
            val et1Text = et1?.text.toString() // Change to et2
            val offset = et1?.getOffsetForPosition(it.x, it.y) // Change to et2
            var start = offset?.let {
                et1Text?.lastIndexOf(' ', it)?.plus(1) ?: 0 // Change to et2Text
            } ?: 0 // Handle the case when offset is null

            var end = offset?.let {
                et1Text?.indexOf(' ', it)
            } ?: et1Text?.length ?: 0 // Change to et2Text

            // Make sure start and end values are not negative
            if (start < 0) start = 0
            if (end < 0) end = et1Text?.length ?: 0

            val selectedWord = et1Text?.substring(start, end)

            // wordtrans(selectedWord)를 호출하여 단어를 번역하거나 원하는 작업을 수행합니다.
            wordtrans(selectedWord)
            true // true를 반환하여 클릭 이벤트를 수신하고 EditText의 편집 기능을 막습니다.
        }

        et2?.setOnLongClickListener {
            // 긴 클릭 이벤트 처리
            val et2Text = et2?.text.toString() // Change to et2
            val offset = et2?.getOffsetForPosition(it.x, it.y) // Change to et2
            var start = offset?.let {
                et2Text?.lastIndexOf(' ', it)?.plus(1) ?: 0 // Change to et2Text
            } ?: 0 // Handle the case when offset is null

            var end = offset?.let {
                et2Text?.indexOf(' ', it)
            } ?: et2Text?.length ?: 0 // Change to et2Text

            // Make sure start and end values are not negative
            if (start < 0) start = 0
            if (end < 0) end = et2Text?.length ?: 0

            val selectedWord = et2Text?.substring(start, end)

            // wordtrans(selectedWord)를 호출하여 단어를 번역하거나 원하는 작업을 수행합니다.
            wordtrans(selectedWord)
            true // true를 반환하여 클릭 이벤트를 수신하고 EditText의 편집 기능을 막습니다.
        }
        et3?.setOnLongClickListener {
            // 긴 클릭 이벤트 처리
            val et3Text = et3?.text.toString() // Change to et2
            val offset = et3?.getOffsetForPosition(it.x, it.y) // Change to et2
            var start = offset?.let {
                et3Text?.lastIndexOf(' ', it)?.plus(1) ?: 0 // Change to et2Text
            } ?: 0 // Handle the case when offset is null

            var end = offset?.let {
                et3Text?.indexOf(' ', it)
            } ?: et3Text?.length ?: 0 // Change to et2Text

            // Make sure start and end values are not negative
            if (start < 0) start = 0
            if (end < 0) end = et3Text?.length ?: 0

            val selectedWord = et3Text?.substring(start, end)

            // wordtrans(selectedWord)를 호출하여 단어를 번역하거나 원하는 작업을 수행합니다.
            wordtrans(selectedWord)
            true // true를 반환하여 클릭 이벤트를 수신하고 EditText의 편집 기능을 막습니다.
        }
        //메뉴버튼
        binding.btnmenu.setOnClickListener {
            startActivity(intent)
        }
        //  영어 체이지 버튼 클릭시
        btn_current_location.setOnClickListener {
            rotationCount += 1
            if (rotationCount > 2) rotationCount = 1

            // Calculate the degrees for rotation
            val degrees = 90 * rotationCount

            // Create a RotateAnimation and set it to the button
            val rotateAnimation = RotateAnimation(
                0f,
                degrees.toFloat(),
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotateAnimation.duration =
                300 // Set the duration of the animation (0.5 seconds)
            btn_current_location.startAnimation(rotateAnimation)

            if (kor == 1) {
                tv4.text = "영어"
                tv5.text = "한글"
                kor = 2

            } else {
                tv4.text = "한글"
                tv5.text = "영어"
                kor = 1
            }
        }


        //지우기 버튼 (et1,2,3 전부 지울것인가  나머지 초기화할 내용은?
        btn_clear.setOnClickListener {
            et1?.setText("")
            tv4.text = "한글"
            tv5.text = "영어"
            tv1.text = ""
            kor = 1
        }
        //번역버튼
        btn_trans.setOnClickListener {
            if (kor == 1) {
                transkortoen()
            } else {
                transentokor()
            }
        }

        //버튼 듣기1
        btn_speak1.setOnClickListener {
            speak1()

        }
        //버튼 듣기2
        btn_speak2.setOnClickListener {
            speak2()
        }
        //버튼 ai
        btn_ai.setOnClickListener {
            ai()
        }
        //버튼 보여주기
        btn_show.setOnClickListener {
            val dialogFragment = CustomDialogFragment()
            dialogFragment.show(supportFragmentManager, "CustomDialogFragment")
        }
        ////음성입력 버튼
        btn_voice.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSIONS_RECORD_AUDIO
                )
            } else {
                // Start speech recognition and pass a callback
                startSpeechToText(object : SpeechRecognitionCallback {
                    override fun onSpeechRecognitionCompleted(inputText: String) {

                        Handler().postDelayed({
                            speak1()
                        }, 1000)


                        ai()
                        // Execute code after another 1-second delay (3 seconds from the beginning)
                        Handler().postDelayed({
                            speak2()
                        }, 3000)


                    }
                })
            }
        }

    }

    private fun wordtrans(inputtext: String?) {
        if (booleankoreantoen) {
            translatorentokorean.translate(inputtext.toString())
                .addOnSuccessListener { translatedText ->
                    // 커스텀 레이아웃으로 토스트 메시지 생성
                    val toastLayout = layoutInflater.inflate(
                        R.layout.toast_custom_layout,
                        findViewById(R.id.toast_layout_root) as ViewGroup?
                    )
                    val toastMessage = toastLayout.findViewById<TextView>(R.id.textViewMessage)
                    val imageViewIcon = toastLayout.findViewById<ImageView>(R.id.imageViewIcon001)

                    // 토스트 메시지에 표시될 내용 설정
                    toastMessage.text = translatedText

                    // ImageView 설정 (이미지 리소스를 추가해야 합니다.)
                    imageViewIcon.setImageResource(R.drawable.baseline_volume_mute_24)

                    val toast = Toast(this)
                    toast.duration = Toast.LENGTH_SHORT
                    toast.view = toastLayout

                    // 중앙에 토스트 메시지 표시
                    toast.setGravity(Gravity.CENTER, 0, 0)

                    toast.show()

                    imageViewIcon.setOnClickListener {
                        val selectedWord: String = when (selectedEditTextId) {
                            R.id.et1 -> et1?.text.toString()
                            R.id.et2 -> et2?.text.toString()
                            R.id.et3 -> et3?.text.toString()
                            // 여기에 다른 EditText들의 ID와 해당하는 값을 추가하면 됩니다.
                            else -> "" // 선택된 EditText가 없을 때에 대한 기본값 처리
                        }
                        wordspeak(selectedWord)
                    }
                }
                .addOnFailureListener { exception ->
                    // 번역 실패 시 처리할 내용 추가 가능
                }
        }
    }


    //알림창 일주일에 한번
    private fun showCustomDialog() {
        // Create the dialog with the custom layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.imageView1)
        val imageView2 = dialogView.findViewById<ImageView>(R.id.imageView2)
        val imageView3 = dialogView.findViewById<ImageView>(R.id.imageView3)
        val textView = dialogView.findViewById<TextView>(R.id.textView001)

        // Set the image and text from resources
        imageView.setImageResource(R.drawable.key1) // Replace with your image resource

        // Build the AlertDialog
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setView(dialogView)
            setTitle("알림")
            setPositiveButton("Don't show for a week") { _, _ ->
                // Save that the dialog has been shown, so it won't show again for a week (7 days)
                val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean(PREF_DIALOG_SHOWN, true)
                editor.putLong(PREF_DIALOG_TIME, System.currentTimeMillis())
                editor.apply()
            }
            setNegativeButton("Close") { _, _ ->
                // Save that the dialog has been shown, so it won't show again
                val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean(PREF_DIALOG_SHOWN, true)
                editor.apply()
            }
        }
        // Show the dialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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
                tv1.text = ""
//                startSpeechToText()
            } else {
                tv1.text = ""
            }
        }
    }


    private fun startSpeechToText(callback: SpeechRecognitionCallback) {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        if (kor == 1) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN)
        } else if (kor == 2) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음성을 입력하세요.")
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // 음성 입력 준비가 완료되었을 때 호출됩니다.
                tv1.text = ""
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
                tv1.text = ""
            }

            override fun onError(error: Int) {
                // 음성 입력 중 오류가 발생했을 때 호출됩니다.
            }

            // Inside onResults() method, invoke the callback with the recognized text
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val inputText = matches[0]
                    if (kor == 1) {
                        et1?.setText(inputText.toString())
                        transkortoen1(inputText)
                        callback.onSpeechRecognitionCompleted(inputText)


                    } else {
                        et1?.setText(inputText.toString())
                        transentokor1(inputText)
                        //0725수정
                        callback.onSpeechRecognitionCompleted(inputText)

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
                tv1.text = ""
            }
            .addOnFailureListener {
                println("1111111111111111")
                booleankoreantoen = false
            }
        translatorentokorean.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                booleanentokorean = true
                tv1.text = ""
            }
            .addOnFailureListener {
                println("1111111111111111")
                booleanentokorean = false
            }
    }

    private fun transkortoen() {

        if (booleankoreantoen) {
            translatorkoreantoen.translate(et1?.text.toString())
                .addOnSuccessListener { translatedText ->
                    et2?.setText(translatedText.toString())
                    // 현재 날짜 구하기
                    val currentDate =
                        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    // 텍스트 파일 저장 경로
                    val filePath = "${externalCacheDir?.absolutePath}/$currentDate.txt"

                    //글로벌변수에 추가
                    GlobalData.list1.add("$translatedText")
                    // 변수 x의 값을 텍스트 파일에 저장
                    File(filePath).writeText(GlobalData.list1.toString())

                }
                .addOnFailureListener { exception ->
                    et2?.setText(exception.toString())
                }
        }

    }

    private fun transentokor() {
        if (booleanentokorean) {
            translatorentokorean.translate(et1?.text.toString())
                .addOnSuccessListener { translatedText ->
                    et2?.setText(translatedText.toString())
                    // 현재 날짜 구하기
                    val currentDate =
                        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    // 텍스트 파일 저장 경로
                    val filePath = "${externalCacheDir?.absolutePath}/$currentDate.txt"

                    //글로벌변수에 추가
                    GlobalData.list1.add("$translatedText")
                    // 변수 x의 값을 텍스트 파일에 저장
                    File(filePath).writeText(GlobalData.list1.toString())

                }
                .addOnFailureListener { exception ->
                    et2?.setText(exception.toString())
                }
        }

    }

    private fun transkortoen1(inputtext: String) {

        if (booleankoreantoen) {
            translatorkoreantoen.translate(inputtext.toString())
                .addOnSuccessListener { translatedText ->
                    et2?.setText(translatedText.toString())
                    // 현재 날짜 구하기
                    val currentDate =
                        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    // 텍스트 파일 저장 경로
                    val filePath = "${externalCacheDir?.absolutePath}/$currentDate.txt"

                    //글로벌변수에 추가
                    GlobalData.list1.add("$translatedText")
                    // 변수 x의 값을 텍스트 파일에 저장
                    File(filePath).writeText(GlobalData.list1.toString())

                }
                .addOnFailureListener { exception ->

                    et2?.setText(exception.toString())
                }
        }

    }

    private fun transentokor1(inputtext: String) {
        if (booleanentokorean) {
            translatorentokorean.translate(inputtext.toString())
                .addOnSuccessListener { translatedText ->
                    et2?.setText(translatedText.toString())
                    // 현재 날짜 구하기
                    val currentDate =
                        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    // 텍스트 파일 저장 경로
                    val filePath = "${externalCacheDir?.absolutePath}/$currentDate.txt"

                    //글로벌변수에 추가
                    GlobalData.list1.add("$translatedText")
                    // 변수 x의 값을 텍스트 파일에 저장
                    File(filePath).writeText(GlobalData.list1.toString())

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

    private fun wordspeak(selectedWord:String) {
        val text = selectedWord
        textToSpeech.language = Locale.US
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,null,null)
    }


    private fun speak1() {
        if (!isSpeechInProgress) {
            isSpeechInProgress = true
            val text = et2?.text.toString()

            val params = HashMap<String, String>()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "SPEAK_1"

            when (kor) {
                2 -> {
                    // 한글로 음성 출력
                    textToSpeech.language = Locale.KOREAN
                    textToSpeech.setPitch(0.7f)
                    textToSpeech.setSpeechRate(0.8f)
                }

                1 -> {
                    // 영어로 음성 출력
                    textToSpeech.language = Locale.US
                    textToSpeech.setPitch(0.7f)
                    textToSpeech.setSpeechRate(0.8f)
                }
            }

            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                }

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == "SPEAK_1") {
                        isSpeechInProgress = false
                        // Call the second speech function after the first speech is done
                        //speak2() 실행 삭제
                    }
                }

                override fun onError(utteranceId: String?) {
                    isSpeechInProgress = false
                }
            })

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SPEAK_1")
        }
    }

    private fun speak2() {
        if (!isSpeechInProgress) {
            isSpeechInProgress = true
            val text = et3?.text.toString()
            val params = HashMap<String, String>()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "SPEAK_2"
            //ai는 영어로만 말하기
            // 영어로 음성 출력
            textToSpeech.language = Locale.US



            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                }

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == "SPEAK_2") {
                        isSpeechInProgress = false
                    }
                }

                override fun onError(utteranceId: String?) {
                    isSpeechInProgress = false
                }
            })

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SPEAK_2")
        }
    }


        // ai 함수
        fun ai() {
            val inputT = et2?.text.toString()
            val inputText = "Please answer the following sentence briefly in English." + "$inputT"
            et3?.setText("-----")



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






