package com.example.zenon.myapplicationtest;

import android.app.Activity;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by zenon on 01/10/14.
 */
public class WriteXmlContacts {

    private Activity activity;
    private Document xmlContacts;
    private Element rootElement;

    private String contact="contact";
    private String raw_contact="raw_contact";
    private String data="data";

    private final String nameTag="name";
    private final String phoneTag="phone";
    private final String typeTag="type";


    public WriteXmlContacts(Activity activity) throws Exception{

        this.activity = activity;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        xmlContacts = docBuilder.newDocument();
        rootElement = xmlContacts.createElement("Contacts-backup");
        xmlContacts.appendChild(rootElement);
    }

    /*******************************************************
     * Add node contact to the Document,
     * receives contact_id as parameter to set the id of the
     * node
     *
     * @param contact_id
     * @return
     */
    public Element addContactTag(int contact_id){
            Element contact_node= xmlContacts.createElement(contact);
            contact_node.setAttribute("id", String.valueOf(contact_id));
            rootElement.appendChild(contact_node);
            return contact_node;
    }

    /*******************************************************
     * Add a raw_contact node to the Element in args ...
     *
     * @param contact_node
     * @return
     */
    public Element addRawContactTag(Element contact_node,int id)    {
        Element rw_node= xmlContacts.createElement(raw_contact);
        contact_node.appendChild(rw_node);
        rw_node.setAttribute("id",String.valueOf(id));
        return rw_node;
    }

    public void addName(Element node,String name)
    {
        Element name_node = xmlContacts.createElement(nameTag);
        Text textNameNode = xmlContacts.createTextNode(name);
        Log.d("DEBUG", "ADD  " + name + "\n");
        name_node.appendChild(textNameNode);
        node.appendChild(name_node);
    }

    /*******************************************************
     * Add a data_node to Element in args, which should be a
     * raw_node ...
     *
     * @param raw_node
     * @return
     */
    public void addDataContact(Element raw_node,LinkedList<Phone> list){


       for(Phone phone : list){
               Element data_node = xmlContacts.createElement(data);
               Element phoneNode = xmlContacts.createElement(phoneTag);
               Element typeNode  = xmlContacts.createElement(typeTag);

               Text phoneTextNode= xmlContacts.createTextNode(phone.getPhoneNumber());
               Text typeTextNode=xmlContacts.createTextNode(activity.getString( phone.getType()));

               phoneNode.appendChild(phoneTextNode);
               typeNode.appendChild(typeTextNode);
               data_node.appendChild(phoneNode);
               data_node.appendChild(typeNode);
               raw_node.appendChild(data_node);

           }

    }

    public Document getXmlContact(){

        return xmlContacts;

    }
}
