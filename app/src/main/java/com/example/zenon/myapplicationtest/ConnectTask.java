package com.example.zenon.myapplicationtest;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by zenon on 04/10/14.
 */
public class ConnectTask extends AsyncTask<String,Integer,Void>{


    private String address;
    private String port;
    private String answer;

    private ConnectionListener listener;
    private Socket socketGPDS=null;
    private TcpConnection tcpConnection;

    public ConnectTask(ConnectionListener l){
        listener = l;
    }

    @Override
    protected void onPostExecute(Void ruse){
       //     listener.OnConnectionCompleted(answer);

    }

    @Override
    protected Void doInBackground(String...params){
        this.address = params[0];
        this.port = params[1];
        BufferedReader input;
       // tcpConnection = new TcpConnection(address,Integer.parseInt(port),listener);

        try {
            //Socket socketGPDS;
    /*        socketGPDS = new Socket(address, Integer.parseInt(port));
            input = new BufferedReader(new InputStreamReader(socketGPDS.getInputStream()));
            answer = input.readLine();
*/
            tcpConnection.run();

             //Log.d("DEBUG Connection", read);

        }catch(Exception io){

            io.printStackTrace();
        }
        return null;
    }


}
