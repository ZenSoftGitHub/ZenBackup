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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

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
    //private String filename;
    private ListView listViewBackup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView= inflater.inflate(R.layout.controls_screen,container,false);
        //tv= (TextView)inflatedView.findViewById(R.id.textViewXml);
        //tv.setMovementMethod(new ScrollingMovementMethod());
        listViewBackup =(ListView) inflatedView.findViewById(R.id.list_backup);
        listViewBackup.setClickable(true);

        listViewBackup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String)parent.getItemAtPosition(position);
                Toast.makeText(getActivity().getApplicationContext(),"Deleting " + s,Toast.LENGTH_LONG).show();
                String[] splited= s.split(":");
                delete_backup(splited[0]);
                listBackup();
            }
        });
        listBackup();


        return inflatedView;
    }



    public void saveContacts(String filename) throws Exception{





        saver = new XmlSaver(getActivity());
        saver.saveContacts();
        Log.d("XmlFIle ",saver.getXmlContacts());

        writeToFile(saver.getDocXml(),filename);


    }

    public void writeToFile(Document xml,String filename){
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
        ArrayList<String> fileName = fileName = new ArrayList<String>();

        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            Log.d("Files", "FileName:" + file[i].getName());
            Date lastMod = new Date(file[i].lastModified());

            //files=files+file[i].getName()+" last modified:"+ lastMod+"\n";
            fileName.add(file[i].getName()+": "+lastMod);
        }
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_text_view,R.id.custom_list_tv,fileName);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_text_view,R.id.custom_list_tv,fileName);
        //BackupArrayAdapter adapter = new BackupArrayAdapter(getActivity(),R.id.custom_list_tv,fileName);
        listViewBackup.setAdapter(adapter);


        //tv.setText(files);
    }

    public void delete_backup(String name){
        String path = Environment.getExternalStorageDirectory().toString()+"/zenbackup/";
        File dir = new File(path);
        File files[] = dir.listFiles();
        boolean find=false;
        int i=0;
        File temp=null;
        while(!find && i<files.length){
            temp = files[i];
            find=name.contentEquals(temp.getName());

            i++;
        }
        if(find) {
            try {

                temp.delete();
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }



}