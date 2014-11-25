package com.example.zenon.myapplicationtest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zenon on 22/11/14.
 */
public class BackupArrayAdapter extends ArrayAdapter<String>{

    private ArrayList<String> rowData;
    private Activity mainActivity;

    public BackupArrayAdapter(Activity a, int tvId, ArrayList<String> list){
        super(a.getApplicationContext(),tvId,list);
        mainActivity=a;
        rowData=new ArrayList<String>();
        rowData.addAll(list);

    }


    public View getView(final int position, View convertView,ViewGroup parent){

        ViewHolder holder = new ViewHolder();

        LayoutInflater inflator = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflator.inflate(R.layout.custom_text_view, null);

        holder.backupName =(TextView) convertView.findViewById(R.id.custom_list_tv);


        holder.backupName.setText((String)rowData.get(position));
        return convertView;
    }



    static class ViewHolder{
        TextView backupName;
        Button deleteButton;
        Button editButton;
    }
}




