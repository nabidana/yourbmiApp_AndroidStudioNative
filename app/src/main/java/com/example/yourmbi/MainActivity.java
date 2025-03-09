package com.example.yourmbi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private AlertDialog alertDialog;
    private PermissionSupport permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //checkPermission();
        afterJob();
    }
    private void afterJob(){
        TextView progressper = (TextView)findViewById(R.id.progresstext);
        ProgressBar progress = (ProgressBar)findViewById(R.id.main_progress);

        progressper.setText("20%");
        progress.setProgress(20);

        BmiDatas bmiDatas = BmiDatas.getInstance();
        try{
            bmiDatas.setlocalpath(getFilesDir().getAbsolutePath());
            progressper.setText("40%");
            progress.setProgress(40);
            if(!bmiDatas.checkMasterFolder()){
                errorDialog();
                return;
            }
            progressper.setText("60%");
            progress.setProgress(60);
            if(!bmiDatas.getBmiList()){
                errorDialog();
                return;
            }
            progressper.setText("80%");
            progress.setProgress(80);
            run();
        }catch (Exception e){
            Log.e("처음 실행 못함", "ㅇㅇ");
            errorDialog();
        }
    }
    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            permission = new PermissionSupport(this, this);
            if (!permission.checkPermission()) {
                permission.requestPermission();
            }else{
                afterJob();
            }
        }else{
            afterJob();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(!permission.permissionResult(requestCode, permissions, grantResults)){
            permission.requestPermission();
        }
    }

    private void run(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void errorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("에러 발생")
                .setMessage("에러가 발생하여 앱을 종료합니다.").setCancelable(false)
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        destory();
                        //android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        this.alertDialog = builder.create();
        this.alertDialog.show();
    }
    public void destory(){
        this.alertDialog.dismiss();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}