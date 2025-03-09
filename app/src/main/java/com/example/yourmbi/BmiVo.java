package com.example.yourmbi;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BmiVo {
    private Date day;
    private String hulap;
    private String huldang;
    public String getDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        return format.format(day);
    }
    public String folerName(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM", Locale.KOREA);
        return format.format(day);
    }
    public String filename(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        return format.format(day);
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        return (format.format(day)) + "," + hulap + "," + huldang;
    }
    public String downFormats(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        return "날짜 : " + (format.format(day)) + " 혈압 : " + hulap + " 혈당 : " + huldang;
    }

    public void setDay(String day) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        this.day = format.parse(day);
    }
    public void setDay(Date day){
        this.day = day;
    }

    public String getHulap() {
        return hulap;
    }

    public void setHulap(String hulap) {
        this.hulap = hulap;
    }

    public String getHuldang() {
        return huldang;
    }

    public void setHuldang(String huldang) {
        this.huldang = huldang;
    }

    public BmiVo(){}
    public BmiVo(String sentense) throws  Exception{
        String[] sentenSplit = sentense.split(",");
        if(sentenSplit.length == 3){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            this.day = format.parse(sentenSplit[0]);
            this.hulap = sentenSplit[1];
            this.huldang = sentenSplit[2];
        }
    }
    public BmiVo(String hulap, String huldang){
        this.day = new Date();
        this.hulap = hulap;
        this.huldang = huldang;
    }
    public BmiVo(String day, String hulap, String huldang){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            this.day = format.parse(day);
        }catch (Exception e){
            this.day = new Date();
        }
        this.hulap = hulap;
        this.huldang = huldang;
    }
}
