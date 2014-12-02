package com.example.zenon.myapplicationtest;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import android.support.v4.app.Fragment;
import android.os.Handler;
/**
 * Created by zenon on 06/11/14.
 */

public class ConnectionScreen extends Fragment {


    private final List<SpinnerEntry> spinnerContent = new LinkedList<SpinnerEntry>();
    private Spinner spinner;
    private final ContactsSpinnerAdapter adapter = new ContactsSpinnerAdapter(spinnerContent,getActivity());


    private int port;
    private String addrServ;
    private EditText textAddress = null;
    private EditText textPort =null;
    private Button connectBtn = null;
    private Button sendButton = null;
    private TextView status_tv=null;

    private BufferedReader input= null;

    private MovePageListener pageListener;

    private Handler handlerMessage;

    private TcpConnection tcpConnection;
    private TcpTask taskConnection;

    private URI uri=null;
    private HttpPut putReq = null;

    protected ProgressBar mProgressBar;
    @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        pageListener = ((MainActivity)getActivity()).getMovePageListener();
        handlerMessage = ((MainActivity)getActivity()).getHandlerMessage();




        View inflatedView = inflater.inflate(R.layout.activity_my_activity_test, container, false);
        textAddress = (EditText) inflatedView.findViewById(R.id.editText1);
        textPort = (EditText) inflatedView.findViewById(R.id.editText2);
        //status_tv =(TextView) inflatedView.findViewById(R.id.status_tv);
        status_tv =(TextView)inflatedView.findViewById(R.id.textView4);

        mProgressBar = (ProgressBar)inflatedView.findViewById(R.id.progress);
        mProgressBar.setVisibility(View.GONE);
        connectBtn =(Button) inflatedView.findViewById(R.id.button1);
        sendButton=(Button) inflatedView.findViewById(R.id.send_button);

        connectBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if((textAddress.getText().length()!=0 )&& (textPort.getText().length()!=0)) {
                    try {


                        addrServ = textAddress.getText().toString();
                        port = Integer.parseInt(textPort.getText().toString());
                        sendToServer(v);
                    }catch(NumberFormatException ne){
                        Message msg_format= Message.obtain(handlerMessage,MainActivity.MESSAGE_FORMAT_NUMBER,"Please enter a valid address and valid port");
                        handlerMessage.sendMessage(msg_format);

                    }catch(Exception e){

                        Message msg_exception = Message.obtain(handlerMessage,MainActivity.MESSAGE_EXCEPTION,e.getMessage());
                        handlerMessage.sendMessage(msg_exception);
                    }

                    //connect(v);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"Don't be such an idiot and enter" +
                            " a valid Ip address and a port to connect the Backup Droid service!",Toast.LENGTH_LONG).show();
                }
            }

        });

        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                sendToServer(v);
            }
        });


        return inflatedView;

    }

    public void connect(View v){
        taskConnection = new TcpTask(addrServ, port, handlerMessage);
        taskConnection.execute();
    }


    public void sendToServer(View view) {


        //tcpConnection = taskConnection.getTcpConnection();
        //if(tcpConnection!=null) {
        TaskHttpPut httpPutTask= new TaskHttpPut();
        URI url = URI.create("http://192.168.54.11:5000/upload");
        httpPutTask.execute(url,getActivity(),handlerMessage);

        }


    public TextView getStatusTv(){ return status_tv;}

    public Button getSendButton(){
        return sendButton;
    }

    public Button getConnectBtn(){
        return connectBtn;
    }


    public class TaskHttpPut extends AsyncTask<Object,Integer,Void>{

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
                Message err_msg = Message.obtain(mHandler,MainActivity.MESSAGE_CANT_CONNECT,e.getMessage());
                mHandler.sendMessage(err_msg);
            }
            return null;
        }


        @Override
        protected void onPreExecute(){
            status_tv.setText("Working...");
            status_tv.setTextColor(Color.parseColor("#75ad3e"));
            mProgressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onProgressUpdate(Integer...values){
            super.onProgressUpdate(values);

            mProgressBar.setProgress(values[0]);


        }

        @Override
        protected void onPostExecute(Void v){


            mProgressBar.setVisibility(View.GONE);
            status_tv.setText("No pending task");
            status_tv.setTextColor(Color.RED);
        }

    }


}


