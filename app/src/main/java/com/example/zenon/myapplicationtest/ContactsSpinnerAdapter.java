package com.example.zenon.myapplicationtest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import static android.R.id.text1;

/**
 * Created by zenon on 01/10/14.
 */
public class ContactsSpinnerAdapter extends BaseAdapter implements SpinnerAdapter{

    private final List<SpinnerEntry> content;
    private final Activity activity;

    public ContactsSpinnerAdapter(List<SpinnerEntry> content,
                                  Activity activity){
        super();
        this.content = content;
        this.activity = activity;


    }

    public int getCount()
    {
        return content.size();
    }

    public SpinnerEntry getItem(int position)
    {
        return content.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position,View convertView,ViewGroup parent)
    {
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View spinnerEntry = inflater.inflate(android.R.layout.simple_spinner_item,null);

        final TextView contactName = (TextView) spinnerEntry.findViewById(text1);

        final SpinnerEntry currentEntry = content.get(position);
        if(currentEntry!=null) {
            contactName.setText(currentEntry.getContactName());
        }
        else contactName.setText("UNDEFINED");

            return spinnerEntry;

    }
}
