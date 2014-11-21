package com.example.zenon.myapplicationtest;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by zenon on 09/11/14.
 */
public class XmlSaver {

    private String xmlContacts=null;
    private Document docXml;

    public Activity activity;

    public XmlSaver(Activity a){
            activity=a;
    }

    public void saveContacts() throws Exception{

        Log.d("DEBUG", "Début xmlFromContatct");

        WriteXmlContacts xml = new WriteXmlContacts(activity);

        String[] projRawContact = new String[]{
                ContactsContract.RawContacts.CONTACT_ID,
                ContactsContract.RawContacts.DELETED,
                ContactsContract.RawContacts._ID
        };




        // We begin by a request on Table RawContact to get The for each RawCOntact which
        // is the associated Contact
        Cursor cursorRawcontact = activity.getApplicationContext().getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                projRawContact,
                null,null,null
        );




        if(cursorRawcontact.moveToFirst()){
            while(!cursorRawcontact.isAfterLast()){

                // ID contact in RawContact TAble, corresponding to the contact in Contact Table
                int contact_id = cursorRawcontact.getInt(cursorRawcontact.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
                boolean deleted = cursorRawcontact.getInt(cursorRawcontact.getColumnIndex(ContactsContract.RawContacts.DELETED))==1;
                // Id rawContact in RawContact TAble
                int raw_id = cursorRawcontact.getInt(cursorRawcontact.getColumnIndex(ContactsContract.RawContacts._ID));

                if(!deleted) {
                    //xml.addName(node,name);
                    String name = queryAllContactsFromId(contact_id);
                    ContactData contactData = new ContactData(raw_id);
                    Element node = xml.addContactTag(contact_id);
                    queryAllPhoneNumberFromRawContact(raw_id, contactData);
                    xml.addName(node, name);

                    Element raw_node = xml.addRawContactTag(node,raw_id);
                    xml.addDataContact(raw_node,contactData.getPhoneList());


                }
                cursorRawcontact.moveToNext();
            }
            cursorRawcontact.close();

        }




        docXml = xml.getXmlContact();

        if(docXml==null){
            Log.d("DEBUG","docXML=null");
        }


        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT,"yes");

        DOMSource source = new DOMSource(docXml);
        Writer out = new StringWriter();

        t.transform(source,new StreamResult(out));

       // Log.d("DEBUG","FICHIER XML:\n"+ out.toString()+"\n Fin XML");
       // StreamResult fileWriter = new StreamResult(new File("/storage/sdcard0/contacts.xml"));
        //t.transform(source,fileWriter);

        xmlContacts = out.toString();
    }

    public String queryAllContactsFromId(int id){
        String[] projContacts = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID
        };

        Cursor cursContacts = activity.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                projContacts,
                ContactsContract.Contacts._ID+"=?",
                new String[]{String.valueOf(id)},
                null
        );

        if(cursContacts.moveToFirst()){

            String name_contact = cursContacts.getString(cursContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            int id_contact = cursContacts.getInt(cursContacts.getColumnIndex(ContactsContract.Contacts._ID));
            cursContacts.close();
            return name_contact;

        }
        return null;
    }


    public void queryAllPhoneNumberFromRawContact(int raw_id,ContactData contactData){
        String[] projData = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };

        Cursor cursData = activity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projData,
                ContactsContract.Data.RAW_CONTACT_ID +"=?",
                new String[]{String.valueOf(raw_id)},
                null

        );

        if(cursData.moveToFirst()){
            while(!cursData.isAfterLast()) {


                String phoneNumber = cursData.getString(cursData.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int typeNumber = cursData.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                Log.d("DEBUG","phoneNUmber="+phoneNumber+"\n");
                Log.d("DEBUG","typeNumner="+typeNumber+"\n");
                Phone phone = new Phone(phoneNumber, ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(typeNumber));
                contactData.addPhone(phone);
                cursData.moveToNext();
            }

        }
        cursData.close();
    }




    /****************************************************
     *
     * getXmlContacts :
     * return xml file containing the saved contacts
     *
     ****************************************************/
    public String getXmlContacts(){
        return "<begOfFile>\n"+xmlContacts+"<endOfFile>";
    }
    public Document getDocXml(){return docXml;}
}
