package com.example.zenon.myapplicationtest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import static android.R.layout.list_content;

/**
 * Created by zenon on 01/10/14.
 */
public class ContactListViewAdapter extends BaseAdapter implements ListAdapter {

    private final List<ListViewEntry> content;
    private final Activity activity;

    public ContactListViewAdapter(List<ListViewEntry> content,
                                  Activity activity){
        this.content = content;
        this.activity = activity;
    }

    public int getCount(){
        return content.size();
    }

    public ListViewEntry getItem(int position){
        return content.get(position);
    }

    public long getItemId(int position){

        return position;
    }

    public View getView(int position, View convertView,ViewGroup parent){

        final LayoutInflater inflater = activity.getLayoutInflater();
        final View listEntry = inflater.inflate(R.layout.listview_two_line_entry,null);

        final TextView number = (TextView) listEntry.findViewById(R.id.numberPhoneNumber);
        final TextView type = (TextView) listEntry.findViewById(R.id.numberType);

        final ListViewEntry current = content.get(position);
        if(current!=null) {
            number.setText(current.getDataEntry());
            type.setText(activity.getString(current.getTypeResource()));
        } else   number.setText("La Ruse pour le test");
        return listEntry;
    }
}
