package com.example.zenon.myapplicationtest;

/**
 * Created by zenon on 06/11/14.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ControlScreen extends Fragment {

    private Button contactsButton;
    private Button listFileButton;
    private TcpConnection tcp;
    private XmlSaver saver;
    private TextView tv;
    private String filename;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView= inflater.inflate(R.layout.controls_screen,container,false);
        tv= (TextView)inflatedView.findViewById(R.id.textViewXml);
        tv.setMovementMethod(new ScrollingMovementMethod());
        // Get the contacts save button
        contactsButton = (Button) inflatedView.findViewById(R.id.button_contacts);
        contactsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    askForFileName();
                    //saveContacts();

                    //sendContacts();
                }catch(Exception io){
                    Log.e("ControlsScreen","An error occurred during saving\n ");
                }
            }
        });


        listFileButton=(Button)inflatedView.findViewById(R.id.listFileButton);
        listFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listBackup();
            }
        });
 
        return inflatedView;
    }

    public void askForFileName(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Enter a name for the  backup");
        alert.setMessage("File Name:");

// Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                filename = input.getText().toString();
                try{
                    saveContacts();
                // Do something with value!
            }catch (Exception e){Log.e("AskForFileName","Exception="+e.getMessage());}
        }});

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    public void saveContacts() throws Exception{





        saver = new XmlSaver(getActivity());
        saver.saveContacts();
        Log.d("XmlFIle ",saver.getXmlContacts());
        tv.setText(saver.getXmlContacts());

        writeToFile(saver.getDocXml());


    }

    public void writeToFile(Document xml){
       try {
           DOMSource source = new DOMSource(xml);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer t = tf.newTransformer();
           String sdcard = Environment.getExternalStorageDirectory().toString();
           StreamResult fileWriter = new StreamResult(new File(sdcard+"/zenbackup/"+filename));
           t.transform(source, fileWriter);
       }catch(Exception e){Log.e("writeToFile","Exception :"+e.getMessage());}
    }


    public void sendContacts(){
        Log.d("ControlScreen","sendContacts function\n");
        tcp = ((MainActivity)getActivity()).getTcpConnection();
        if(tcp!=null){
            Log.d("ControlScreen","Sending XmlSave to Server\n");
            tcp.sendData(saver.getXmlContacts());
        }
        else{
            Log.d("ControlScreen","TcpConnection is null");
        }

    }

    public void listBackup(){
        String path = Environment.getExternalStorageDirectory().toString()+"/zenbackup/";

        AssetManager mgr = getActivity().getAssets();
        String files="";
        File f = new File(path);
        File file[] = f.listFiles();
        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            Log.d("Files", "FileName:" + file[i].getName());
            files=files+file[i].getName()+"\n";
        }

        tv.setText(files);
    }



}