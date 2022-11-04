package com.dabee.tp05todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.widget.Toast
import com.dabee.tp05todoapp.EditActivity
import com.dabee.tp05todoapp.databinding.ActivityTodoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.*

class TodoActivity : AppCompatActivity() {

    lateinit var binding: ActivityTodoBinding

    var category: Int= 0 //카테고리 구분번호
    var titles:Array<String> = arrayOf("ALL", "WORK", "STUDY", "HEALTH", "HOBBY", "MEETING", "ETC", "DONE")
    var slogans:Array<String> = arrayOf("사람은 선택할 수 있고 바꿀 수 있는 힘이 있습니다.",
        "오늘일을 내일로 미루지 마세요",
        "하루하루 전문가로 발전하는 딩신을 응원 합니다.",
        "병 만나기는 쉬워도 병 고치기는 어렵습니다.",
        "행복은 멀리있지 않아요. 좋아하는 일을 하세요.",
        "메모하는 습관이 미래를 바꿉니다.",
        "성공은 수고의 대가라는 것을 기억하세요.")

    // 할일 데이터들을 저장하는 리스트
    var todoItems:MutableList<TodoItem> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView(R.layout.activity_todo);
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = intent
        category = intent.getIntExtra("category", 0)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //카테고리 종류에 따라 제목글씨 표시
        supportActionBar!!.title = titles[category]
        //카테고리 종류에 따라 슬로건 글씨를 표시
        binding.tvSlogan.text = slogans[category]

        //오늘의 날짜 표시
        binding.tvToday.text = SimpleDateFormat("yyyy년 MM월 dd일").format(Date())


        //새로운 할일 작성하는 액티비티로 전환
        binding.fabAdd.setOnClickListener { view: View? ->
            val intent1 = Intent(this, EditActivity::class.java)
            intent1.putExtra("category", category)
            startActivity(intent1)
        }

        // 리사이클러뷰에 아답터 생성하여 연결하기
        binding.recyclerView.adapter=TodoRecyclerAdapter(this,todoItems)

        // 아이템의 [완료] 버튼 클릭 이벤트 처리
        binding.btn.setOnClickListener{ clickDone()}

    } //onCreate

    // CRUD INSERT,UPDATE,DELETE execSQL   / SELECT rowSQL

    var position:Int = -1

    private fun clickDone(){

        val num:Int = todoItems[position].num

        val db:SQLiteDatabase = openOrCreateDatabase("Todo", MODE_PRIVATE,null)
        db.execSQL("UPDATE todo SET isDon=1 WHERE num=?", arrayOf(num.toString()))

        todoItems.removeAt(position)
        binding.recyclerView.adapter?.notifyItemRemoved(position)
        BottomSheetBehavior.from(binding.bs).state= BottomSheetBehavior.STATE_COLLAPSED
    }


    //제목줄의 뒤로가기버튼(업버튼)을 클릭했을때 자동 실행되는 콜백메소드
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    // 액티비티가 화면에 보여질 때 자동으로 실행되는 라이프사이클 메소드
    override fun onResume() {
        super.onResume()

        // SQLite DB에 저장된 할일 데이터 읽어오는 기능메소드 호출
        loadDB()

    }

    private fun loadDB(){
        todoItems.clear()
        binding.recyclerView.adapter?.notifyDataSetChanged()

        //SQLite DB에서 할일 데이터 읽어오기
        val db: SQLiteDatabase = openOrCreateDatabase("Todo", MODE_PRIVATE,null)

        //category 선택에 따라 해당 목록만 가져오도록 쿼리문 사용
        var cursor:Cursor = if(category==0) db.rawQuery("SELECT * FROM todo WHERE isDone=0",null) //ALL
                            else if (category==7) db.rawQuery("SELECT * FROM todo WHERE isDone=1",null) //DONE
                            else db.rawQuery("SELECT * FROM todo WHERE isDone=0 and category=?", arrayOf(category.toString())) //카테고리 별

        while (cursor.moveToNext()){
            val num:Int = cursor.getInt(0)
            val title:String = cursor.getString(1)
            val date:String = cursor.getString(2)
            val category:Int = cursor.getInt(3)
            val note:String = cursor.getString(4)
            val isDone:Int = cursor.getInt(5)

            todoItems.add(TodoItem(num,title, date, category, note, isDone))
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()

//        val cusor:Cursor

//        if(category==0){
//            cusor = db.rawQuery("SELECT * FROM todo isDone=0", null)
//        }else if(category==7){
//
//        }else
//        val cusor:Cursor = db.rawQuery("SELECT * FROM todo WHERE category=? and isDone=0", arrayOf(category.toString()))
        

    }

    
    //클릭된 아이템의 정보를 보여주는 BottomSheet를 보여주는 기능 메소드
    fun showBottomSheet(position:Int){

        this.position = position

        // BottomSheet의 상태(열렸을때/닫혔을때)에 따라 코드
        if( BottomSheetBehavior.from(binding.bs).state == BottomSheetBehavior.STATE_COLLAPSED){

            updateBottomSheetData(position)
            BottomSheetBehavior.from(binding.bs).state = BottomSheetBehavior.STATE_EXPANDED
        }else{
            BottomSheetBehavior.from(binding.bs).state = BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.from(binding.bs).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                    updateBottomSheetData(position)
                    BottomSheetBehavior.from(binding.bs).state=BottomSheetBehavior.STATE_EXPANDED
                    BottomSheetBehavior.from(binding.bs).removeBottomSheetCallback(this)


                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {


                }

            })
        }

    }
    fun updateBottomSheetData(position: Int){
        binding.tvBsTitle.text= todoItems.get(position).title
        binding.tvBsDate.text=todoItems[position].date
        binding.tvBsCategory.text = titles[todoItems[position].category]
        binding.etBsNote.setText(todoItems[position].note)

        if(category==7){
            binding.btn.visibility  = View.INVISIBLE
        }
    }

}