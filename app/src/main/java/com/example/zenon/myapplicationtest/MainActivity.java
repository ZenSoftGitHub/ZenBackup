package com.example.zenon.myapplicationtest;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.provider.ContactsContract;
import android.widget.Toast;


import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.sql.Connection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import android.os.Handler;

import com.example.zenon.myapplicationtest.SpinnerEntry;

import java.util.zip.Inflater;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static android.provider.ContactsContract.*;




public class MainActivity extends FragmentActivity{

    private final List<SpinnerEntry> spinnerContent = new LinkedList<SpinnerEntry>();
    private Spinner spinner;
    private final ContactsSpinnerAdapter adapter = new ContactsSpinnerAdapter(spinnerContent,this);
    private ListView contactListView;

    private EditText textAddress = null;
    private EditText textPort =null;
    private Button connectBtn = null;
    private BufferedReader input= null;

    private PagerAdapter mPagerAdapter;

    private CustomViewPager pager;

    public static final int CONNECTED= 1;
    public static final int MESSAGE_SERVER=2;
    public static final int TCP_CONNECTION=3;
    public static final int MESSAGE_SOCKET_CLOSE=4;
    public static final int MESSAGE_CANT_CONNECT=5;

    private Handler mHandler ;
    private TcpConnection tcpConnection;
    private List fragments;


    private MovePageListener pageListener = new MovePageListener() {
        @Override
        public void onMovePage(MovePageEvent event) {
            Toast.makeText(getApplicationContext(),"I caught a MovePageEvent !!!",Toast.LENGTH_LONG).show();
            if (event.typeEvent==1) pager.setCurrentItem(1);
            if(event.typeEvent==0) pager.setCurrentItem(0);
        }
    };







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_view_pager);

        fragments = new Vector();


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message inputMessage) {

                switch (inputMessage.what) {

                    case MESSAGE_SERVER:
                        if(((String)inputMessage.obj).indexOf("OK:")!=-1){
                            tcpConnection.close_connection();
                        }

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast,
                                (ViewGroup) findViewById(R.id.custom_toast));

                        // Create Custom Toast
                        Toast toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
                        TextView toast_tv = (TextView)layout.findViewById(R.id.toast_tv);
                        toast_tv.setText("Server says:"+((String)inputMessage.obj));
                       // toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        //toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();


                        ((ConnectionScreen)fragments.get(0)).getSendButton().setEnabled(true);
                        ((ConnectionScreen)fragments.get(0)).getConnectBtn().setEnabled(false);
                        ((ConnectionScreen)fragments.get(0)).getStatusTv().setText("Connected");
                        ((ConnectionScreen)fragments.get(0)).getStatusTv().setTextColor(Color.GREEN);

                        break;

                    case TCP_CONNECTION:
                        tcpConnection = (TcpConnection)inputMessage.obj;
                        break;

                    case MESSAGE_SOCKET_CLOSE:
                        ((ConnectionScreen)fragments.get(0)).getStatusTv().setText("Disconnected");
                        ((ConnectionScreen)fragments.get(0)).getStatusTv().setTextColor(Color.RED);
                        ((ConnectionScreen)fragments.get(0)).getSendButton().setEnabled(false);
                        ((ConnectionScreen)fragments.get(0)).getConnectBtn().setEnabled(true);
                        break;

                    case MESSAGE_CANT_CONNECT:
                        LayoutInflater inf = getLayoutInflater();
                        View lay = inf.inflate(R.layout.custom_toast,
                                (ViewGroup) findViewById(R.id.custom_toast));

                        // Create Custom Toast
                        Toast t = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
                        TextView t_tv = (TextView)lay.findViewById(R.id.toast_tv);
                        t_tv.setText(((String)inputMessage.obj));
                        // toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        //toast.setDuration(Toast.LENGTH_LONG);
                        t.setView(lay);
                        t.show();

                }
            }
        };





        fragments.add(((ConnectionScreen)(Fragment.instantiate(this,ConnectionScreen.class.getName()))));
        fragments.add(Fragment.instantiate(this,ControlScreen.class.getName()));



        // Create  the adapter in charge of fragment list
         this.mPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);

        pager = (CustomViewPager) super.findViewById(R.id.viewpager);
        pager.setAdapter(this.mPagerAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_activity_test,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.local_backup:
                Log.d("MainActivity", "Create Local backup action");
                askForFileName();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void askForFileName(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter a name for the  backup");
        alert.setMessage("File Name:");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                String filename = input.getText().toString();
                try{
                    ((ControlScreen)fragments.get(1)).saveContacts(filename);
                    ((ControlScreen)fragments.get(1)).listBackup();
                    // Do something with value!
                }catch (Exception e){
                    Log.e("AskForFileName","Exception="+e.getMessage());
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }});

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public MovePageListener getMovePageListener(){
        return pageListener;
    }

    public Handler getHandlerMessage(){
        return mHandler;
    }

    public TcpConnection getTcpConnection(){
        return tcpConnection;
    }

}
