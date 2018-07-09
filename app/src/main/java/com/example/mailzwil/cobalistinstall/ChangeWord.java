package com.example.mailzwil.cobalistinstall;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Mailzwil on 10-Mar-18.
 */

public class ChangeWord extends AppCompatActivity implements View.OnClickListener {

    //Menginisialisasikan variabel-variabel yang dibutuhkan
    Button btnUbah, btnRekam;
    ImageView icon;
    TextView appName;
    EditText appNick;
    DatabaseHelper myDb;
    Drawable apkicon;
    PackageManager packageManager;
    String apksPackage, apksNick, apksName;
    String keywordpack;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //Program yang dijalankan pada saat activity dibuat
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_word);

        //deklarasi package manager dan program interaksi basis data
        packageManager = getPackageManager();
        myDb = new DatabaseHelper(this);

        //deklarasi primary key kata kunci
        keywordpack = getString(R.string.keyword_package);

        //deklarasi benda-benda pada tampilan
        appName = findViewById(R.id.appName);
        appNick = findViewById(R.id.appNick);
        btnUbah = findViewById(R.id.btnUbah);
        btnRekam = findViewById(R.id.btnRekam);
        icon = findViewById(R.id.appIcon);

        //menerima string yang dikirim dari activity sebelumnya
        apksPackage = getIntent().getStringExtra("apkPackage");

        //penentu apakah akan menampilkan tampilan pengubah kata kunci atau sebutan
        if(!Objects.equals(apksPackage.toUpperCase(),keywordpack.toUpperCase())){
            apksName = getApkName(apksPackage);
            apkicon = getApkIcon(apksPackage);
        }else {
            apksName = "Kata Kunci";
            apkicon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_star, null);
        }
        apksNick = myDb.getApkNick(apksPackage);
        appNick.setText(apksNick);
        appName.setText(apksName);
        icon.setImageDrawable(apkicon);
        btnUbah.setOnClickListener(this);
        btnRekam.setOnClickListener(this);
    }

    //mengambil nama aplikasi menggunakan packageManager
    public String getApkName(String packagename) {
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(packagename, PackageManager.GET_META_DATA);
            String appName = (String) packageManager.getApplicationLabel(info);
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //mengambil icon aplikasi menggunakan packagemanager
    public Drawable getApkIcon(String packagename){
        try {
            Drawable icon = packageManager.getApplicationIcon(packagename);
            return icon;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //fungsi untuk menentukan tombol yang ditekan
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUbah:
                changeWord();
                break;
            case R.id.btnRekam:
                recordWord();
                break;
            default:
                Toast.makeText(this, "Error no button pressed " + "...", Toast.LENGTH_LONG).show();
                break;
        }
    }

    //fungsi untuk mengubah kata pada basis data dan EditText
    public void changeWord(){
        String newNick = appNick.getText().toString();
        boolean updateres= myDb.updateData(apksPackage,newNick);
        if (updateres) {
            try{
                appNick.setText(myDb.getApkNick(apksPackage));
                Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_LONG).show();
            }catch(Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else Toast.makeText(getApplicationContext(),"Data not Updated",Toast.LENGTH_LONG).show();
    }

    //fungsi untuk memulai activity mendengar kata
    public void recordWord(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Sebutkan Sebutan untuk Aplikasi");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Maaf Device Anda Tidak Mendukung Fitur ini",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //fungsi untuk menerima hasil dari activity mendengar kata
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    appNick.setText(result.get(0));
                    changeWord();
                }
                break;
            }
            default:
                Toast.makeText(getApplicationContext(),
                        "Terjadi Kesalahan pada panjang kata yang diinginkan",
                        Toast.LENGTH_SHORT).show();
        }
    }
}