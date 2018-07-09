package com.example.mailzwil.cobalistinstall;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by Mailzwil on 22-Jun-18.
 */

public class VoiceRecognition implements RecognitionListener {

    //inisialisasi
    private String LOG_TAG = "VoiceRecognition";
    private static VoiceRecognition instance = null;
    VoiceInterface voiceInterface;

    //inisialisasi program ini
    public VoiceRecognition() {}

    //mendapatkan permintaan
    public static VoiceRecognition getInstance() {
        if (instance == null) {
            instance = new VoiceRecognition();
        }
        return instance;
    }

    //menyiapkan pendengar pada interface
    public void setListener(VoiceInterface listener) {
        this.voiceInterface = listener;
    }

    //mengembalikan hasil mendengar
    public void processVoiceCommands(String voiceCommands){
        voiceInterface.processVoiceCommands(voiceCommands);
    }


    @Override
    public void onReadyForSpeech(Bundle bundle) {Log.i(LOG_TAG, "onReadyForSpeech");}

    @Override
    public void onBeginningOfSpeech() {Log.i(LOG_TAG, "onBeginningOfSpeech");}

    @Override
    public void onRmsChanged(float rmsdB) {Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);}

    @Override
    public void onBufferReceived(byte[] buffer) {Log.i(LOG_TAG, "onBufferReceived: " + buffer);}

    @Override
    public void onEndOfSpeech() {Log.i(LOG_TAG, "onEndOfSpeech");}

    @Override
    public void onPartialResults(Bundle bundle) {Log.i(LOG_TAG, "onPartialResults");}

    @Override
    public void onEvent(int i, Bundle bundle) {Log.i(LOG_TAG, "onEvent");}

    //pada saat error terjadi
    @Override
    public void onError(int i) {
        Log.d(LOG_TAG, "ERROR, restarts listening");
        voiceInterface.restartListening();
    }

    //hasil yang didapatkan
    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        if(results!=null) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String result = matches.get(0);
            processVoiceCommands(result);
        }else voiceInterface.restartListening();
    }
}
