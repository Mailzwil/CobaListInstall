package com.example.mailzwil.cobalistinstall;

/**
 * Created by Mailzwil on 12-Mar-18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    //inisialisasi variabel
    public static final String DATABASE_NAME = "InstalledApks.db";
    public static final String TABLE_NAME = "installedApks_table";
    public static final String COL_1 = "PACKAGENAME";
    public static final String COL_2 = "APKNAME";
    public static final String COL_3 = "NICKNAME";

    //inisialisasi program interaksi dengan basis data
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //fungsi yang dijalankan pada saat DatabaseHelper dibuat
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +" (PACKAGENAME TEXT PRIMARY KEY, APKNAME TEXT,NICKNAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    //menghapus isi basis data
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME);
        db.execSQL("VACUUM");
    }

    //mengisi basis data
    public void insertData(String apkname,String packagename,String nickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = checkApkPackage(packagename);
        if(result){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1,packagename);
            contentValues.put(COL_2,apkname);
            contentValues.put(COL_3,nickname);
            db.insert(TABLE_NAME,null,contentValues);
        }
    }

    //memeriksa apakah basis data sudah terisi atau belum
    public boolean checkApkPackage(String apkPackage){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT "+COL_1+" FROM "+TABLE_NAME+" WHERE UPPER("+COL_1+") LIKE UPPER('%"+apkPackage+"%')",null);
        int res = cursor.getCount();
        return res<1;
    }

    //mendapatkan kata sebutan aplikasi dari basis data
    public String getApkNick(String packagename){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT "+COL_3+" FROM "+TABLE_NAME+" WHERE UPPER("+COL_1+") LIKE UPPER('%"+packagename+"%')",null);
        cursor.moveToFirst();
        String res= cursor.getString(cursor.getColumnIndex(COL_3));
        return res;
    }

    //mendapatkan nama aplikasi dari basis data
    public String getApkName(String packagename){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT "+COL_2+" FROM "+TABLE_NAME+" WHERE UPPER("+COL_1+") LIKE UPPER('%"+packagename+"%')",null);
        cursor.moveToFirst();
        String res= cursor.getString(cursor.getColumnIndex(COL_2));
        return res;
    }

    //mendapatkan list aplikasi yang kata sebutannya teridentifikasi
    public ArrayList<String> getApkPackage(String apkNick){
        ArrayList<String> res = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT "+COL_1+" FROM "+TABLE_NAME+" WHERE UPPER("+COL_3+") LIKE UPPER('%"+apkNick+"%')",null);
        cursor.moveToFirst();
        do {
            res.add(cursor.getString(cursor.getColumnIndex(COL_1)));
        } while (cursor.moveToNext());
        return res;
    }

    //mengubah data pada basis data
    public boolean updateData(String packagename,String nickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, packagename);
        contentValues.put(COL_3, nickname);
        long res = db.update(TABLE_NAME, contentValues, COL_1+" = ?", new String[]{packagename});
        return res > 0 ;
    }
}
