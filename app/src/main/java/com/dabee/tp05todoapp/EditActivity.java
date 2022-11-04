package com.dabee.tp05todoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.CalendarView;

import com.dabee.tp05todoapp.databinding.ActivityEditBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    int category;
    String[] titles = new String[]{"ALL","WORK","STUDY","HEALTH","HOBBY","MEETING","ETC","DONE"};

    String date;
    BottomSheetDialog bottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit);
        binding=ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("할일 추가");

        category=getIntent().getIntExtra("category",0);
        if(category==0) category++;
        binding.tvCategory.setText(titles[category]);

        date=new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date());
        binding.tvDate.setText(date);

        binding.tvDate.setOnClickListener(view -> showBottomSheetDialogCalendar());
        binding.tvCategory.setOnClickListener(view -> showBottomSheetDialogCategory());
        binding.btnComplete.setOnClickListener(view -> clickComplete());



    }//////

    void showBottomSheetDialogCategory(){

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bs_category);
        bottomSheetDialog.show();

        bottomSheetDialog.findViewById(R.id.bsd_category_work).setOnClickListener(view -> clickCategory(1));
        bottomSheetDialog.findViewById(R.id.bsd_category_study).setOnClickListener(view -> clickCategory(2));
        bottomSheetDialog.findViewById(R.id.bsd_category_health).setOnClickListener(view -> clickCategory(3));
        bottomSheetDialog.findViewById(R.id.bsd_category_hobby).setOnClickListener(view -> clickCategory(4));
        bottomSheetDialog.findViewById(R.id.bsd_category_meeting).setOnClickListener(view -> clickCategory(5));
        bottomSheetDialog.findViewById(R.id.bsd_category_etc).setOnClickListener(view -> clickCategory(6));
    }

    void clickCategory(int category){
        this.category=category;
        binding.tvCategory.setText(titles[category]);

        bottomSheetDialog.dismiss();
    }



    void showBottomSheetDialogCalendar(){
        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bs_calendar);
        bottomSheetDialog.show();

        CalendarView calendarView = bottomSheetDialog.findViewById(R.id.bsd_calendar);
        calendarView.setOnDateChangeListener( (view, year, month, day) ->{

//            달력에서 선택한 날짜로 Calendar 객체 생성
            java.util.GregorianCalendar calendar = new GregorianCalendar(year,month,day);

            date= new SimpleDateFormat("yyyy년 MM월 dd일").format(calendar.getTime());
            binding.tvDate.setText(date);
            bottomSheetDialog.dismiss();
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    void clickComplete(){

        SQLiteDatabase db = openOrCreateDatabase("Todo",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS todo(num INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,date TEXT,category INTEGER,note TEXT,isDone INTEGER)");

        //저장할 데이터들 ( title, date(멤버변수), category(멤버변수), not )
        String title= binding.etTitle.getText().toString();
        String note= binding.etNote.getText().toString();

//         "todo" 테이블안에 데이터를 삽입하는 SQL 쿼리문 실행
        db.execSQL("INSERT INTO todo(title,date,category,note,isDone) VALUES(?,?,?,?,?)",new Object[]{title,date,category,note,0});

        // 저장완료했으니.. 현재 액티비티 종료
//        finish();
        onBackPressed();




    }


}