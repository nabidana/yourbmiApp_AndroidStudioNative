package com.example.yourmbi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity implements CustomAdapter.ListBtnClickListener{
    public BmiDatas bmiDatas;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        this.bmiDatas = BmiDatas.getInstance();
        Button gohome = (Button) findViewById(R.id.gohome);
        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                bmiDatas.addHisotry("");
                startActivity(intent);
                finish();
            }
        });
        Button goinput = (Button)findViewById(R.id.goinput);
        goinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                bmiDatas.addHisotry("calender");
                bmiDatas.setHistoryDay("");
                startActivity(intent);
                finish();
            }
        });
        Button goback = (Button)findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String historyPath = bmiDatas.getHistory();
                if(historyPath.equals("input")){
                    Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                    startActivity(intent);
                    finish();
                }else if(!historyPath.equals("calender")){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month++;
                String inputday = String.valueOf(year)+String.format("%02d", month)+String.format("%02d", day);
                //Log.d("select", "날짜 "+year+" 월 "+month+" 일 "+day+" / input "+inputday);
                setList(inputday);
            }
        });

        this.listView = (ListView) findViewById(R.id.calendarlist);

        Intent intent = getIntent();
        String selectDay = "";
        try {
            selectDay = intent.getStringExtra("selectday");
        } catch (Exception e) { }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        if(selectDay == null){
            Date now = new Date();
            String today = format.format(now);
            setList(today);
        }else if(selectDay.isEmpty()){
            Date now = new Date();
            String today = format.format(now);
            setList(today);
        }else{
            Long selectDayLong = 0L;
            try {
                Date selects = format.parse(selectDay);
                selectDayLong = selects.getTime();
            } catch (Exception e) {
                Date now = new Date();
                selectDayLong = now.getTime();
            }
            calendarView.setDate(selectDayLong);
            setList(selectDay);
        }
    }

    public void setList(String days){
        //Log.d("day_path2", bmiDatas.getLocalBmiPath());
        List<String> itemList = bmiDatas.getAdapterList(days);
        CustomAdapter customAdapter = new CustomAdapter(itemList, this, this);
        //ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.activity_calender, itemList);
        this.listView.setAdapter(customAdapter);
//        if(itemList.size() != 0){
//        }else{
//
//        }
    }

    @Override
    public void onListBtnClick(int position, String s) {
        Log.d("calender click", s+"//"+position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String[] s_split = s.split(",");
        String nowday = "";
        try {
            Date searchDay = format.parse(s_split[0]);
            Log.d("format1", String.valueOf(searchDay));
            nowday = format2.format(searchDay);
            Log.d("format2",  nowday);
        } catch (Exception e) {
            nowday = s_split[0].split(" ")[0];
            Log.d("format2",  nowday);
        }
        Intent intent = new Intent(getApplicationContext(), InputActivity.class);
        intent.putExtra("type", "update");
        intent.putExtra("day", nowday);
        intent.putExtra("index", position);
        bmiDatas.addHisotry("calender");
        bmiDatas.setHistoryDay(nowday);
        startActivity(intent);
        finish();
    }
}
