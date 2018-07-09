package com.example.mailzwil.cobalistinstall;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ListInstalledApps  extends ListActivity {

    //inisialisasi
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist=null;
    private AppAdapter listAdapter=null;
    private ListView list;
    String keywordpack, sentinfo;
    DatabaseHelper myDb;

    //dijalankan pada saat program ini dipanggil/dibuat
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keywordpack = getString(R.string.keyword_package);
        list=(ListView) findViewById(R.id.list);
        myDb = new DatabaseHelper(this);
        sentinfo = getIntent().getStringExtra("status");
        packageManager=getPackageManager();
        new LoadApplications().execute();
    }

    //memulai aplikasi ketika ditekan
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ApplicationInfo app= applist.get(position);

        try{
            Toast.makeText(this, "Launching " + app.loadLabel(packageManager) + "...", Toast.LENGTH_LONG).show();
            // launch the application
            Intent intent= packageManager.getLaunchIntentForPackage(app.packageName);

            if(intent != null){
                startActivity(intent);
            }
        }catch (NoSuchMethodError e){
            Toast.makeText(this, "not Launching "+app.loadLabel(packageManager)+ "...", Toast.LENGTH_LONG).show();

        }

        catch (ActivityNotFoundException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //mendapatkan list aplikasi terpasang pada ponsel pintar dan memasukkannya ke dalam basis data
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list){
        ArrayList<ApplicationInfo> applist=new ArrayList<ApplicationInfo>();
        for(ApplicationInfo info : list){
            try{
                if(packageManager.getLaunchIntentForPackage(info.packageName) != null){
                    myDb.insertData(info.loadLabel(packageManager).toString(),
                            info.packageName, info.loadLabel(packageManager).toString());
                    applist.add(info);
                }
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if(Objects.equals(sentinfo.toUpperCase(),"reset".toUpperCase())){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }
        return  applist;
    }

    //dilakukan pada saat pemuatan tampilan
    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress=null;

        @Override
        protected Void doInBackground(Void... params) {
            myDb.insertData("Kata Kunci",keywordpack,"call me");
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(packageManager.GET_META_DATA));
            listAdapter=new AppAdapter(ListInstalledApps.this,R.layout.list_item, applist);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listAdapter);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress=ProgressDialog.show(ListInstalledApps.this, null, "Loading app info ...");
            super.onPreExecute();
        }
    }
}