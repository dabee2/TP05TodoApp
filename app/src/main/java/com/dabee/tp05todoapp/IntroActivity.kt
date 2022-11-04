package com.dabee.tp05todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.dabee.tp05todoapp.MainActivity
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import com.dabee.tp05todoapp.R
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultCallback
import android.app.Activity
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.dabee.tp05todoapp.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    //activity_intro.xml의 뷰들 참조변수를 알아서 연결하는 Binding 클래스 참조변수
    var binding: ActivityIntroBinding? = null

    // 프로필이미지경로, 이름 데이터 변수
    var profileImage: String? = ""
    var name: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView(R.layout.activity_intro);
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // [시작하기] 버튼 클릭 이벤트 처리
        binding!!.btn.setOnClickListener { view: View? ->
            saveData()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding!!.civProfile.setOnClickListener { view: View? ->
            //갤러리 or 사진 앱을 실행
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
        loadData() //저장된 프로필정보와 이름을 가져와서 보여주는 기능 호출출
    } //onCreate method

    // SharedPreference 에서 프로필이미지와 이름을 가져와서 화면에 보여주기
    fun loadData() {
        val pref = getSharedPreferences("account", MODE_PRIVATE)
        profileImage = pref.getString("profile", "") //두번째 파라미터 : 없을때 기본 string
        name = pref.getString("name", "")
        Glide.with(this).load(profileImage).error(R.drawable.images).into(binding!!.civProfile)
        binding!!.etName.setText(name)
    }

    // SharedPreference 에 프로필이미지와 이름을 영구히 저장!
    fun saveData() {
        name = binding!!.etName.text.toString()
        val pref = getSharedPreferences("account", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("profile", profileImage)
        editor.putString("name", name)
        editor.commit()
    }

    var resultLauncher =
        registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_CANCELED) {
                val intent = result.data
                val uri = intent!!.data // 선택한 사진의 경로 uri 데이터를 받기
                profileImage = uri.toString()
                Glide.with(this@IntroActivity).load(profileImage).error(R.drawable.images).into(
                    binding!!.civProfile
                )
            }
        }
}