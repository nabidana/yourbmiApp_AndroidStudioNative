package com.example.yourmbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    public BmiDatas bmiDatas;
    private LineChart chart;
    private final int chartdaycount = 7;
    private AlertDialog alertDialog;
    private String selectType = "";
    private String downloadType = "";
    private String downloadmonth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.bmiDatas = BmiDatas.getInstance();
        Button goinput = (Button)findViewById(R.id.goinput);
        goinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                bmiDatas.addHisotry("home");
                startActivity(intent);
                finish();
            }
        });
        Button gocalender = (Button) findViewById(R.id.gocalender);
        gocalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CalenderActivity.class);
                bmiDatas.addHisotry("home");
                startActivity(intent);
                finish();
            }
        });
        Button alldownload = (Button) findViewById(R.id.alldownload);
        alldownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadData("all", null);
            }
        });
        Button monthdownload = (Button) findViewById(R.id.thismonthdownload);
        monthdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthSelectDialog();
            }
        });

        chart = findViewById(R.id.mychart);
        configureLineChart();
    }

    private void configureLineChart(){
        List<String> dayList = new ArrayList<>();
        for(int i = 0 ; i < chartdaycount; i++){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, (i * -1));
            //int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //String.format("%02d", month)+"월\n"+
            String addit = String.format("%02d", day)+"일";
            dayList.add(addit);
        }
        chart.setExtraBottomOffset(15f);
        chart.getDescription().setEnabled(true);

        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(10);
        legend.setTextSize(13);
        legend.setTextColor(Color.parseColor("#A3A3A3"));
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setYEntrySpace(5);
        legend.setWordWrapEnabled(true);
        legend.setXOffset(80f);
        legend.setYOffset(20f);
        legend.getCalculatedLineSizes();

        // XAxis (아래쪽) - 선 유무, 사이즈, 색상, 축 위치 설정
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 데이터 표시 위치
        xAxis.setGranularity(1f);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.rgb(118, 118, 118));
        xAxis.setSpaceMin(0.1f); // Chart 맨 왼쪽 간격 띄우기
        xAxis.setSpaceMax(0.1f); // Chart 맨 오른쪽 간격 띄우기

        // YAxis(Right) (왼쪽) - 선 유무, 데이터 최솟값/최댓값, 색상
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextSize(14f);
        yAxisLeft.setTextColor(Color.rgb(163, 163, 163));
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setAxisLineWidth(2);
        yAxisLeft.setAxisMinimum(-20); // 최솟값
        yAxisLeft.setAxisMaximum((float) 120); // 최댓값
        yAxisLeft.setGranularity((float) 1);

        // YAxis(Left) (오른쪽) - 선 유무, 데이터 최솟값/최댓값, 색상
        YAxis yAxis = chart.getAxisRight();
        yAxis.setDrawLabels(true); // label 삭제
        yAxis.setTextColor(Color.rgb(163, 163, 163));
        yAxis.setDrawAxisLine(false);
        yAxis.setAxisLineWidth(2);
        yAxis.setAxisMinimum(-20); // 최솟값
        yAxis.setAxisMaximum((float) 120); // 최댓값
        yAxis.setGranularity((float) 1);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                //Log.d("val", String.valueOf(value));
                return dayList.get( dayList.size() - (int) value - 1);
            }
        });

        MyMarkerView mv1 = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv1.setChartView(chart);
        chart.setMarker(mv1);
        chart.setData(createChartData());
        chart.invalidate();
    }

    private LineData createChartData(){
        ArrayList<Entry> entry1 = new ArrayList<>();
        ArrayList<Entry> entry2 = new ArrayList<>();

        LineData chartData = new LineData();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        for(int i = 0 ; i < chartdaycount; i++){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, ( ( chartdaycount-i-1) * -1));
            String regDate = format.format(calendar.getTime());
            String hulap = bmiDatas.homeChartHulap(regDate);
            float val1 = Float.parseFloat(hulap);
            entry1.add(new Entry(i, val1));
            String huldang = bmiDatas.homeChartHuldang(regDate);
            float val2 = Float.parseFloat(huldang);
            entry2.add(new Entry(i, val2));
        }

        LineDataSet lineDataSet1 = new LineDataSet(entry1, "혈압");
        chartData.addDataSet(lineDataSet1);
        lineDataSet1.setLineWidth(3);
        lineDataSet1.setCircleRadius(6);
        lineDataSet1.setDrawValues(true);
        lineDataSet1.setDrawCircleHole(true);
        lineDataSet1.setDrawCircles(true);
        lineDataSet1.setDrawHorizontalHighlightIndicator(false);
        lineDataSet1.setDrawHighlightIndicators(false);
        lineDataSet1.setColor(Color.rgb(255, 155, 155));
        lineDataSet1.setCircleColor(Color.rgb(255, 155, 155));

        LineDataSet lineDataSet2 = new LineDataSet(entry2, "혈당");
        chartData.addDataSet(lineDataSet2);
        lineDataSet2.setLineWidth(3);
        lineDataSet2.setCircleRadius(6);
        lineDataSet2.setDrawValues(true);
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setDrawCircles(true);
        lineDataSet2.setDrawHorizontalHighlightIndicator(false);
        lineDataSet2.setDrawHighlightIndicators(false);
        lineDataSet2.setColor(Color.rgb(178, 223, 138));
        lineDataSet2.setCircleColor(Color.rgb(178, 223, 138));

        chartData.setValueTextSize(15);
        return chartData;
    }

    private void makeLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("저장중....")
                .setCancelable(false)
                .setView(R.layout.dialog_progress);
        this.alertDialog = builder.create();
        this.alertDialog.show();
    }
    public void monthSelectDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_select, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH)+1);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(year);
        yearPicker.setMaxValue(2099);
        yearPicker.setValue(year);

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selectType = String.valueOf(yearPicker.getValue())+String.format("%02d", monthPicker.getValue());
                        Log.d("selects", selectType);
                        downloadData("month", selectType);
                        destory();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        destory();
                    }
                });
        this.alertDialog = builder.create();
        this.alertDialog.show();
    }

    public void destory(){
        this.alertDialog.dismiss();
        this.alertDialog = null;
    }
    private void downloadData(String type, String select){
//        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
//        Log.d("debug-path", downloadPath);
        if(type.equals("all")){
            downloadType = "all";
            String filename = "내혈압혈당전체데이터.txt";
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, filename);
            someActivityResultLauncher.launch(intent);
        }else if(type.equals("month")){
            downloadType = "month";
            downloadmonth = select;
            String filename = select+"혈압혈당데이터.txt";
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, filename);
            someActivityResultLauncher.launch(intent);
        }else{
            destory();
        }
    }
    private void addText(Uri uri){
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
            fos.write(bmiDatas.downloadBmiData(downloadType, downloadmonth).getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
            pfd.close();
            downloadType = "";
            downloadmonth = "";
        } catch (Exception e) {
            Log.d("파일 다운로드 에러", e.toString());
        }
    }
    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        addText(uri);
                    }
                }
            });
}
