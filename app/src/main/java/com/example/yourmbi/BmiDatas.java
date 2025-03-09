package com.example.yourmbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BmiDatas extends Application {
    private static BmiDatas instance;
    private Map<String, List<BmiVo>> bmiList;
    private String hisotry;
    private String historyDay;
    //private final String localBmiPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "bmifolder";
    //private String localBmiPath = android.os.Environment.getDataDirectory().getAbsolutePath() + File.separator + "bmifolder";//this.getFilesDir().getAbsolutePath() + File.separator + "bmifolder";
    private String localBmiPath;
            //android.os.Environment.getDataDirectory().getAbsolutePath() + File.separator + "bmifolder";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static BmiDatas getInstance(){
        if(instance == null){
            instance = new BmiDatas();
            instance.hisotry = "";
        }
        return instance;
    }
    public void setlocalpath(String path){
        this.localBmiPath = path + File.separator + "bmifolder";
    }
    public String getLocalBmiPath(){
        return  this.localBmiPath;
    }
    public boolean checkMasterFolder(){
        //Log.d("path", localBmiPath);
        File checkFolder = new File(localBmiPath);
        try {
            if(!checkFolder.exists()){
                return checkFolder.mkdir();
            }else{
                return  true;
            }
        }catch (Exception e){
            //Log.d("check mst Excpeion", e.toString());
            return  false;
        }
    }
    public boolean saveBmi(BmiVo bmiVo){
        try{
            String folderName = bmiVo.folerName();
            String folerPath = localBmiPath + File.separator + folderName;
            File folder = new File(folerPath);
            if(!folder.exists()){
                folder.mkdir();
            }
            //파일 생성
            String filename = bmiVo.filename();
            String filePath = folerPath + File.separator + filename;
            File file = new File(filePath);
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(bmiVo.toString());
            bw.newLine();
            bw.flush();
            bw.close();
            List<BmiVo> voList = new ArrayList<>();
            voList.add(bmiVo);
            bmiList.put(filename, voList);
            return true;
        } catch (Exception e) {
            //Log.e("Exception", e.toString());
            return false;
        }
    }
    public boolean updateBmi(BmiVo bmiVo, int index){
        try {
            String folderName = bmiVo.folerName();
            String folerPath = localBmiPath + File.separator + folderName;
            File folder = new File(folerPath);
            if(!folder.exists()){
                return false;
            }
            String filename = bmiVo.filename();
            String filePath = folerPath + File.separator + filename;
            File file = new File(filePath);
            if(!file.exists()){
                return false;
            }
            List<BmiVo> voList = bmiList.get(filename);
            if(voList == null){
                return false;
            }else if(voList.isEmpty()){
                return  false;
            }
            voList.set(index, bmiVo);
            for(BmiVo vo : voList){
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
                bw.write(vo.toString());
                bw.newLine();
                bw.flush();
                bw.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean getBmiList(){
        Map<String, List<BmiVo>> addlist = new HashMap<>();
        try{
            File localBmiFile = new File(localBmiPath);
            String[] monthList = localBmiFile.list();
            if(monthList.length > 0){
                for(String month : monthList){
                    String monthPath = localBmiPath + File.separator + month;
                    File monthFolder = new File(monthPath);
                    if( !monthFolder.isDirectory() ){
                        monthFolder.delete();
                        monthFolder.mkdir();
                        continue;
                    }
                    String[] dayList = monthFolder.list();
                    if(dayList.length > 0){
                        for(String day : dayList){
                            String dayPath = monthPath + File.separator + day;
                            File dayFile = new File(dayPath);
                            BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(dayFile)));
                            StringBuffer sb = new StringBuffer();
                            String line;
                            while ( (line = br.readLine()) != null){
                                sb.append(line);
                            }
                            br.close();
                            String bmi_str = sb.toString();
                            String[] bmi_str_split = bmi_str.split("\n");
                            List<BmiVo> bmis = new ArrayList<>();
                            for(String sen : bmi_str_split){
                                if(sen == null){
                                    continue;
                                }else if(sen.equals("")){
                                    continue;
                                }else{
                                    BmiVo bmiVo = new BmiVo(sen);
                                    bmis.add(bmiVo);
                                }
                            }
                            addlist.put(day, bmis);
                        }
                    }
                }
            }
            this.bmiList = addlist;
            return true;
        }catch (Exception e){
            //Log.e("Exception", e.toString());
            return false;
        }
    }

    public List<String> getAdapterList(String search){
        List<String> list = new ArrayList<>();
        List<BmiVo> bmiVoLists = this.bmiList.get(search);
        if(bmiVoLists != null){
            //Log.d("volist", bmiVoLists.toString());
            for(BmiVo vo : bmiVoLists){
                String s = vo.toString();
                list.add(s);
            }
        }
        return  list;
    }
    public BmiVo bmiVoDetail(String day, int index){
//        Log.d("check", String.valueOf(this.bmiList.isEmpty()));
//        Log.d("day", day);
        List<BmiVo> finds = this.bmiList.get(day);
//        for(String k : this.bmiList.keySet()){
//            Log.d("key : ", k);
//        }
        return finds.get(index);
    }

    public void addHisotry(String path){
        this.hisotry = path;
    }
    public String getHistory(){
        return this.hisotry;
    }
    public void setHistoryDay(String day){
        this.historyDay = day;
    }
    public String getHistoryDay(){
        return this.historyDay;
    }

    public String homeChartHulap(String searchday){
        String resultMsg = "0.0";
        List<BmiVo> bmiVoLists = this.bmiList.get(searchday);
        if(bmiVoLists != null){
            Double total = 0.0;
            if(bmiVoLists.size() > 0){
                for(BmiVo vo : bmiVoLists){
                    total += Double.valueOf(vo.getHulap());
                }
                total = total / bmiVoLists.size();
                total = Math.round(total * 10.0) / 10.0;
            }
            resultMsg = String.valueOf(total);
        }
        return resultMsg;
    }
    public String homeChartHuldang(String searchday){
        String resultMsg = "0.0";
        List<BmiVo> bmiVoLists = this.bmiList.get(searchday);
        if(bmiVoLists != null){
            Double total = 0.0;
            if(bmiVoLists.size() > 0){
                for(BmiVo vo : bmiVoLists){
                    total += Double.valueOf(vo.getHuldang());
                }
                total = total / bmiVoLists.size();
                total = Math.round(total * 10.0) / 10.0;
            }
            resultMsg = String.valueOf(total);
        }
        return resultMsg;
    }

    public String downloadBmiData(String type, String select){
        StringBuilder sb = new StringBuilder();
        sb.append("혈압 혈당 전체 다운로드");
        sb.append("\n====================================================");
        if(type.equals("all")){
            for(String key : bmiList.keySet()){
                List<BmiVo> getlist = bmiList.get(key);
                if(getlist != null){
                    for(BmiVo vo : getlist){
                        sb.append("\n").append(vo.downFormats());
                    }
                }
            }
        }else if(type.equals("month")){
            for(String key : bmiList.keySet()){
                if(!key.contains(select)){
                    continue;
                }
                List<BmiVo> getlist = bmiList.get(key);
                if(getlist != null){
                    for(BmiVo vo : getlist){
                        sb.append("\n").append(vo.downFormats());
                    }
                }
            }
        }
        return sb.toString();
    }
}
