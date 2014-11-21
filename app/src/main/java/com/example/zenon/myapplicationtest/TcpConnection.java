package com.example.zenon.myapplicationtest;

import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.os.Handler;
/**
 * Created by zenon on 09/11/14.
 */
public class TcpConnection{

    private Socket socketGPDS=null;
    private BufferedReader buffer=null;
    private PrintWriter printer=null;

    private ConnectionListener listener=null;

    private Handler mHandler;
    private String addr,incommingMsg;
    private Integer port;

    Boolean run=true;

    public TcpConnection(String a, Integer p,Handler h){

        addr=a;
        port=p;
        mHandler = h;

    }

    public void run(){

        try{
            socketGPDS = new Socket();
            SocketAddress address = new InetSocketAddress(InetAddress.getByName(addr),port);

             socketGPDS.connect(address,5000);
            //Message connectionOk= Message.obtain(mHandler,MyActivityTest.CONNECTED);
            Message msg_connection = Message.obtain(mHandler, MainActivity.TCP_CONNECTION, this);
            mHandler.sendMessage(msg_connection);

            try{

                buffer = new BufferedReader(new InputStreamReader(socketGPDS.getInputStream()));

                printer = new PrintWriter(new OutputStreamWriter(socketGPDS.getOutputStream()));

                while(run){

                    incommingMsg = buffer.readLine();


                    if(incommingMsg.length()>0) {
                        // listener.OnConnectionCompleted(incommingMsg);
                        Log.d("TcpConnection", "Server says: " + incommingMsg);
                        Message msg = Message.obtain(mHandler, MainActivity.MESSAGE_SERVER, incommingMsg);
                        mHandler.sendMessage(msg);

                    }

                }

            }catch(Exception e){
                Log.e("TcpConnectionClient", "Error detected in run() " + e.getMessage());

            }
            finally {
                printer.flush();
                printer.close();
                buffer.close();
                socketGPDS.close();
                Message msg_socket_close = Message.obtain(mHandler,MainActivity.MESSAGE_SOCKET_CLOSE);
                mHandler.sendMessage(msg_socket_close);

            }



        }catch(Exception e){
            Log.e("TcpConnectionClient", "Cannot create Socket "+e.getMessage());
            Message msg_cant_connected = Message.obtain(mHandler,MainActivity.MESSAGE_CANT_CONNECT,e.getMessage());
            mHandler.sendMessage(msg_cant_connected);
        }



    }


    public void sendData(String msg)  {

        Log.d("TcpConnection","sendData function\n");
        if(printer!=null && !printer.checkError()){

            printer.println(msg);
            printer.flush();

        }

    }

    public void close_connection(){
        try {
            socketGPDS.close();
        }
        catch(Exception e){Log.e("TcpConnection",e.getMessage());}
    }



}
