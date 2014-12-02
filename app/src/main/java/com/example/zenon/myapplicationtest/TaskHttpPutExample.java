package com.example.zenon.myapplicationtest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by zenon on 01/12/14.
 */
public class TaskHttpPutExample extends AsyncTask<Object,Integer,Void>{

    private Handler mHandler;
    @Override
    protected Void doInBackground(Object ... params) {
        URI url = (URI)params[0] ;
        Activity main_activity = (Activity)params[1];
        mHandler = (Handler) params[2];

        HttpClient client = new DefaultHttpClient();
        HttpPut putReq= new HttpPut(url);
        XmlSaver saver = new XmlSaver(main_activity);

        try{
            saver.saveContacts();
            String xml = saver.getXmlContacts();
            StringEntity entity = new StringEntity(xml);
            entity.setContentType("text/xml");

            putReq.setEntity(entity);

            HttpResponse response = client.execute(putReq);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                String incommingMsg="";
                String temp="";
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while((temp=rd.readLine())!= null){
                    incommingMsg+=temp;

                }
                Message msg = Message.obtain(mHandler, MainActivity.MESSAGE_SERVER, incommingMsg);
                mHandler.sendMessage(msg);
            }

        }catch(Exception e){
            Log.e("TaskHttpPut","An exception occurs during the execution of HttpPut : " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer...values){
        super.onProgressUpdate(values);



    }

}
