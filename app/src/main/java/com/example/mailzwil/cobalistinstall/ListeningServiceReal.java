package com.example.mailzwil.cobalistinstall;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Mailzwil on 22-Jun-18.
 */

public class ListeningServiceReal extends AppCompatActivity implements VoiceInterface {


    //inisialisasi
    final int REQ_CODE_SPEECH_INPUT = 100;
    private VoiceRecognition vRL;
    Intent recognizerIntent;
    SpeechRecognizer speech = null;
    DatabaseHelper myDb;
    String LOG_TAG = "VoiceRecognitionActivity";
    PackageManager packageManager = null;
    Context context;
    String keywordpack;


    //dijalankan pada saat dibuat
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        keywordpack = getString(R.string.keyword_package);
        myDb = new DatabaseHelper(context);
        packageManager=getPackageManager();
        vRL = VoiceRecognition.getInstance();
    }

    //memanggil activity mendengar kata sebutan
    public void listenApk() {
        initSpeech();
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Sebutkan Sebutan untuk Aplikasi");
        try {
            startActivityForResult(recognizerIntent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Maaf Device Anda Tidak Mendukung Fitur ini",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //menerima hasil mendengar kata sebutan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:{
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    try{
                        String apkNick = result.get(0).replaceAll("\'", "");
                        launchApk(apkNick);
                    }catch(Exception e){
                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }else restartListening();
                break;
            }
            default:
                Toast.makeText(getApplicationContext(),"Terjadi Kesalahan pada panjang kata yang diinginkan", Toast.LENGTH_SHORT).show();
        }
    }

    //memulai aplikasi yang disebutkan
    public void launchApk(String apkNick){
        try{
            String apkPackage="";
            ArrayList<String> apkPacks = myDb.getApkPackage(apkNick);
            for (String apkPack: apkPacks){
                String apkNickname = myDb.getApkNick(apkPack);
                if (Objects.equals(apkNickname.toUpperCase(),apkNick.toUpperCase())){
                    apkPackage= apkPack;
                }
            }
            String apkName = myDb.getApkName(apkPackage);
            Intent i = packageManager.getLaunchIntentForPackage(apkPackage);
            if(i !=null){
                startActivity(i);
                Toast.makeText(getApplicationContext(),"Launching "+apkName,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),apkNick+" intent kosong",Toast.LENGTH_SHORT).show();
                restartListening();
            }
        }catch(Exception e){
            if (Objects.equals(e.getMessage().toUpperCase(),"index 0 requested, with a size of 0".toUpperCase()))
                Toast.makeText(getApplicationContext(),apkNick+" Aplikasi yang disebut tidak terdaftar",Toast.LENGTH_LONG).show();
            else Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            restartListening();
        }
    }

    //memulai mendengar kata kunci
    public void listenKeyword() {
        try {
            stopListening();
            initSpeech();
            speech.startListening(recognizerIntent);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Maaf Device Anda Tidak Mendukung Fitur ini",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //berhenti mendengar
    public void stopListening(){
        if (speech != null) {
            speech.destroy();
        }
        speech = null;
    }

    //menginisialisasi fungsi mendengar
    public void initSpeech(){
        recognizerIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(vRL);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(context));
    }


    //memroses hasil mendengar kata kunci
    public void processVoiceCommands(String voiceCommands){
        final String keyword = myDb.getApkNick(keywordpack);
        String match = voiceCommands;
        if (match!=null && Objects.equals(match.toUpperCase(),keyword.toUpperCase())){
            try{
                stopListening();
                listenApk();
            }catch(Exception e){
                Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else restartListening();
    }

    //mengakhiri dan memulai mendengar
    @Override
    public void restartListening(){
        stopListening();
        listenKeyword();
    }
}