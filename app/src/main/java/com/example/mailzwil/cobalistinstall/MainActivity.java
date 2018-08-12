package com.example.mailzwil.cobalistinstall;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Objects;


public class MainActivity extends ListeningServiceReal implements View.OnClickListener {

    //inisialisasi
    Button btn1,btn2,btnTutup,btnJalan,btnResetDB,btnStop;
    DatabaseHelper myDb;
    Intent i;
    String keywordpack;

    //dijalankan pada saat perangkat lunak dimulai
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        keywordpack= getString(R.string.keyword_package);
        VoiceRecognition.getInstance().setListener(this);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btnTutup = findViewById(R.id.btnTutup);
        btnJalan = findViewById(R.id.btnJalan);
        btnResetDB = findViewById(R.id.btnResetDB);
        btnStop = findViewById(R.id.btnStop);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btnJalan.setOnClickListener(this);
        btnTutup.setOnClickListener(this);
        btnResetDB.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    //fungsi untuk tombol-tombol
    @Override
    public void onClick (View v){
        switch (v.getId()) {
            case R.id.btn1:
                changeKeyWord();
                break;
            case R.id.btn2:
                i = new Intent(this, ListInstalledApps.class);
                i.putExtra("status","run");
                startActivity(i);
                break;
            case R.id.btnTutup:
                onBackPressed();
                break;
            case R.id.btnJalan:
                try {
                    boolean keyWord= myDb.checkApkPackage(keywordpack);
                    if(keyWord) listenKeyword();
                    else{
                        Toast.makeText(this, "Kata Kunci Belum Terisi, " +
                                "Silahkan Tekan Set Default Data terlebih dahulu", Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                    Toast.makeText(this,"Kata Kunci Belum Terisi, " +
                            "Silahkan Tekan Set Default Data terlebih dahulu", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnStop:
                stopListening();
                break;
            case R.id.btnResetDB:
                resetTable();
                break;
            default:
                Toast.makeText(this, "Error no button pressed ...", Toast.LENGTH_LONG).show();
                break;
        }
    }

    //fungsi untuk menampilkan dialog konfirmasi menutup aplikasi
    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        adb.setTitle("Keluar dari Aplikasi?");
        adb.setMessage("Klik ya untuk menutup aplikasi");
        adb.setCancelable(false);
        adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        adb.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        adb.show();
    }

    //memanggil activity pengubah kata kunci
    public void changeKeyWord(){
        try{
            String keyWord= myDb.getApkNick(keywordpack);
            if(!Objects.equals(keyWord,null)) {
                Toast.makeText(this, "Editing Kata Kunci", Toast.LENGTH_LONG).show();
                i = new Intent(this, ChangeWord.class);
                if (i != null) {
                    i.putExtra("apkPackage", keywordpack);
                    startActivity(i);
                }
            }
        }catch (Exception e){
            Toast.makeText(this, "Kata Kunci Belum Terisi, Silahkan Tekan Set Default Data Terlebih dahulu", Toast.LENGTH_LONG).show();
        }
    }

    //fungsi untuk mengosongkan tabel pada basis data dengan menggunakan DatabaseHelper
    public void resetTable(){
        try {
            myDb.deleteTable();
            i = new Intent(this, ListInstalledApps.class);
            i.putExtra("status","reset");
            Toast.makeText(this, "Resetting table ", Toast.LENGTH_LONG).show();
            startActivity(i);
        }catch (SQLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
