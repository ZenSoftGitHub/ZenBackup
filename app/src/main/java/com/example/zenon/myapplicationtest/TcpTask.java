package com.example.zenon.myapplicationtest;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by zenon on 10/11/14.
 */
public class TcpTask extends AsyncTask<Void,Void,TcpConnection> {
    private TcpConnection connection;
    private String address;
    private Integer port;
    private Handler mHandler;
    public TcpTask(String a, Integer p, Handler h){

        address=a;
        port=p;
        mHandler=h;
    }

    @Override
    public TcpConnection doInBackground(Void ... params){
        try{

            connection = new TcpConnection(address,port,mHandler);
        }
        catch(Exception e){

        }

        connection.run();
        return connection;
    }

    public TcpConnection getTcpConnection(){
        return connection;
    }
}
