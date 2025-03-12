package com.example.yourmbi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InputActivity extends AppCompatActivity {

    public BmiDatas bmiDatas;
    private TextView titleText;
    private TextView dateText;
    private TextView hulapText;
    private TextView huldangText;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        this.bmiDatas = BmiDatas.getInstance();
        Button gohome = (Button) findViewById(R.id.gohome);
        gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome();
            }
        });
        Button gocalender = (Button) findViewById(R.id.gocalender);
        gocalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCalender();
            }
        });
        Button goback = (Button) findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBack();
            }
        });
        Button selectDay = (Button)findViewById(R.id.selectdaybtn);
        selectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDateDialog();
            }
        });

        this.titleText = (TextView) findViewById(R.id.inputText);
        this.dateText = (TextView) findViewById(R.id.todaytext);
        this.hulapText = (TextView) findViewById(R.id.hulapText);
        this.huldangText = (TextView) findViewById(R.id.huldangText);

        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String day = dateText.getText().toString();
                String hulap = hulapText.getText().toString();
                String huldang = huldangText.getText().toString();
                if(hulap.equals("")){
                    makeDialog("혈압을 입력해주세요.", "alert", null, -1);
                }else if(huldang.equals("")){
                    makeDialog("혈당을 입력해주세요.", "alert", null, -1);
                }else{
                    try {
                        Double check_hulap = Double.valueOf(hulap);
                    }catch (Exception e){
                        makeDialog("올바른 혈압을 입력해주세요.", "alert", null, -1);
                        return;
                    }
                    try {
                        Double check_huldang = Double.valueOf(huldang);
                    }catch (Exception e){
                        makeDialog("올바른 혈당을 입력해주세요.", "alert", null, -1);
                        return;
                    }
                    BmiVo bmiVo = new BmiVo(day, hulap, huldang);
                    makeDialog("저장하시겠습니까?", "confirm", bmiVo, -1);
                }
            }
        });

        Intent intent = getIntent();
        String type = "";
        try{
            type = intent.getStringExtra("type");
        } catch (Exception e) { }
        if(type == null){
            this.titleText.setText("혈압 혈당 입력하기");
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            String now = format.format(today);
            this.dateText.setText(now);
        }else if(type.equals("update")){
            this.titleText.setText("혈압 혈당 수정하기");
            String day = intent.getStringExtra("day");
            int index = intent.getIntExtra("index", 0);
            Log.d("search", day + " / " + String.valueOf(index));
            BmiVo vo = bmiDatas.bmiVoDetail(day, index);
            selectDay.setVisibility(View.GONE);
            this.dateText.setText(vo.getDay());
            this.hulapText.setText(vo.getHulap());
            this.huldangText.setText(vo.getHuldang());
            save.setText("수정 하기");
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String day = dateText.getText().toString();
                    String hulap = hulapText.getText().toString();
                    String huldang = huldangText.getText().toString();
                    if(hulap.equals("")){
                        makeDialog("혈압을 입력해주세요.", "alert", null, -1);
                    }else if(huldang.equals("")){
                        makeDialog("혈당을 입력해주세요.", "alert", null, -1);
                    }else{
                        try {
                            Double check_hulap = Double.valueOf(hulap);
                        }catch (Exception e){
                            makeDialog("올바른 혈압을 입력해주세요.", "alert", null, -1);
                            return;
                        }
                        try {
                            Double check_huldang = Double.valueOf(huldang);
                        }catch (Exception e){
                            makeDialog("올바른 혈당을 입력해주세요.", "alert", null, -1);
                            return;
                        }
                        BmiVo bmiVo = new BmiVo(day, hulap, huldang);
                        makeDialog("수정하시겠습니까?", "update", bmiVo, index);
                    }
                }
            });
        }else{
            this.titleText.setText("혈압 혈당 입력하기");
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            String now = format.format(today);
            this.dateText.setText(now);
        }

    }

    private void goToHome(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        bmiDatas.addHisotry("");
        startActivity(intent);
        finish();
    }
    private void goToCalender(){
        Intent intent = new Intent(getApplicationContext(), CalenderActivity.class);
        bmiDatas.addHisotry("input");
        startActivity(intent);
        finish();
    }
    private void goToBack(){
        String historyPath = bmiDatas.getHistory();
        if(historyPath.equals("calender")){
            Intent intent = new Intent(getApplicationContext(), CalenderActivity.class);
            if(bmiDatas.getHistoryDay() != null){
                intent.putExtra("selectday", bmiDatas.getHistoryDay());
            }
            startActivity(intent);
            finish();
        }else if(!historyPath.equals("input")){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void makeDialog(String message, String type, BmiVo bmiVo, int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(type.equals("confirm")){
            builder.setTitle("확인 요청")
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            destory();
                        }
                    })
                    .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveData(bmiVo);
                        }
                    });
        }else if(type.equals("update")){
            builder.setTitle("수정 요청")
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            destory();
                        }
                    })
                    .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateData(bmiVo, index);
                        }
                    });
        }else{
            builder.setTitle("에러 발생")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            destory();
                        }
                    });
        }
        builder.setMessage(message).setCancelable(false);
        this.alertDialog = builder.create();
        this.alertDialog.show();
    }
    private void destory(){
        this.alertDialog.dismiss();
    }
    private void saveData(BmiVo bmiVo){
        destory();
        makeLoadingDialog();
        boolean results = this.bmiDatas.saveBmi(bmiVo);
        saveAfterDialog(results);
    }
    private void updateData(BmiVo bmiVo, int index){
        destory();
        makeLoadingDialog();
        boolean resluts = this.bmiDatas.updateBmi(bmiVo, index);
        saveAfterDialog(resluts);
    }

    private void makeLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("저장중....")
                .setCancelable(false)
                .setView(R.layout.dialog_progress);
        this.alertDialog = builder.create();
        this.alertDialog.show();
    }
    private void saveAfterDialog(boolean type){
        destory();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(type){
            builder.setMessage("저장에 성공하였습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            destory();
                            goToHome();
                        }
                    });
        }else{
            builder.setMessage("저장에 실패하였습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            destory();
                        }
                    });
        }
        builder.setTitle("알림")
                .setCancelable(false);
        this.alertDialog = builder.create();
        this.alertDialog.show();
    }
    private void selectDateDialog(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                String selectDay = String.valueOf(year)+"-"+String.format("%02d", month)+"-"+String.format("%02d", day)+" 00:00:00";
                dateText.setText(selectDay);
                //Log.d("select", "날짜 "+year+" 월 "+month+" 일 "+day+" / input "+selectDay);
            }
        }, year, month, day);
        datePickerDialog.show();
    }
}
