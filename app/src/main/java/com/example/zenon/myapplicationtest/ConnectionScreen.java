package com.example.zenon.myapplicationtest;

import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
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
    private ListView contactListView;

    private int port;
    private String addrServ;
    private EditText textAddress = null;
    private EditText textPort =null;
    private Button connectBtn = null;
    private Button sendButton = null;
    private TextView status_tv=null;

    private BufferedReader input= null;

    private MovePageListener pageListener;
    private ConnectionListener listenerSockRes;
    private Handler handlerMessage;

    private TcpConnection tcpConnection;
    private TcpTask taskConnection;
    @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      //  super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_activity_test);
       // getActivity().setContentView(R.layout.activity_view_pager);

        listenerSockRes=((MainActivity)getActivity()).getConnectionListener();
        pageListener = ((MainActivity)getActivity()).getMovePageListener();
        handlerMessage = ((MainActivity)getActivity()).getHandlerMessage();




        View inflatedView = inflater.inflate(R.layout.activity_my_activity_test, container, false);
        textAddress = (EditText) inflatedView.findViewById(R.id.editText1);
        textPort = (EditText) inflatedView.findViewById(R.id.editText2);
        status_tv =(TextView) inflatedView.findViewById(R.id.status_tv);
        connectBtn =(Button) inflatedView.findViewById(R.id.button1);
        sendButton=(Button) inflatedView.findViewById(R.id.send_button);

        connectBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // Toast.makeText(getApplicationContext(),"Try to connect",Toast.LENGTH_SHORT).show();
                if((textAddress.getText().length()!=0 )&& (textPort.getText().length()!=0)) {
                    addrServ=textAddress.getText().toString();
                    port=Integer.parseInt(textPort.getText().toString());


                    connect(v);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"Don't be such an idiot and enter" +
                            " true values to connect GNU Perl Droid service!",Toast.LENGTH_LONG).show();
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


        tcpConnection = taskConnection.getTcpConnection();
        if(tcpConnection!=null) {
            XmlSaver saver = new XmlSaver(getActivity());
            try {

                saver.saveContacts();
                String xml = saver.getXmlContacts();
                tcpConnection.sendData(xml);
            } catch (Exception e) {
                Log.e("SendToServer", "Exception caught:" + e.getMessage());
            }
        }
        else{

        }
    }

    public TextView getStatusTv(){ return status_tv;}

    public Button getSendButton(){
        return sendButton;
    }

    public Button getConnectBtn(){
        return connectBtn;
    }



}


