package com.example.mailzwil.cobalistinstall;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;


public class AppAdapter extends ArrayAdapter<ApplicationInfo> {

    //inisialisasi variabel
    private List<ApplicationInfo> applist = null;
    private Context context;
    private PackageManager packageManager;
    private DatabaseHelper myDb;

    //inisialisasi program pembantu listview
    public AppAdapter(Context context,int resource, List<ApplicationInfo> objects) {
        super(context, resource,  objects);

        this.context=context;
        this.applist=objects;
        packageManager=context.getPackageManager();
        myDb = new DatabaseHelper(getContext());
    }

//    @Override
//    public int getCount() {
//        return ((null != applist) ? applist.size() : 0);
//    }
//
//    @Override
//    public ApplicationInfo getItem(int position) {
//        return ((null != applist) ? applist.get(position) : null);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    //fungsi untuk memasukkan benda-benda untuk setiap variabel pada list
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewHolder mainViewHolder = null;

        if(convertView == null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.list_item,null);
            viewHolder viewHolder = new viewHolder();
            viewHolder.iconView = (ImageView) convertView.findViewById(R.id.appIcon);
            viewHolder.appName = (TextView) convertView.findViewById(R.id.appName);
            viewHolder.appNick = (TextView) convertView.findViewById(R.id.appNick);
            viewHolder.btn = (Button) convertView.findViewById(R.id.btnEdit);
            convertView.setTag(viewHolder);
        }
        mainViewHolder = (viewHolder) convertView.getTag();
        final ApplicationInfo data= applist.get(position);
        if(data != null){
            mainViewHolder.appName.setText(data.loadLabel(packageManager));
            mainViewHolder.appNick.setText(myDb.getApkNick(data.packageName));
            mainViewHolder.iconView.setImageDrawable(data.loadIcon(packageManager));
        }

        mainViewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apkName = data.loadLabel(packageManager).toString();
                String apkPackage = data.packageName;
                changeWord(apkPackage,apkName);
            }
        });
        return convertView;
    }

    //inisialisasi benda-benda pada list_item.xml
    public class viewHolder{
        ImageView iconView;
        TextView appName, appNick;
        Button btn;
    }

    //fungsi untuk memanggil activity pengubah kata
    public void changeWord(String apkPackage, String apkName){
        try{
            Toast.makeText(getContext(), "Editing " + apkName + "...", Toast.LENGTH_LONG).show();
            Intent edit= new Intent(context, ChangeWord.class);
            if(edit != null){
                edit.putExtra("apkPackage", apkPackage);
                context.startActivity(edit);
            }
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}