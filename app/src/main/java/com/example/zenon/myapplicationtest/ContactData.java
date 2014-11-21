package com.example.zenon.myapplicationtest;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by zenon on 03/10/14.
 */
public class ContactData {

    private LinkedList<Phone> phoneLinkedList;

    private int rawContactId;
    // Different types available


    public ContactData(int id){

        this.phoneLinkedList = new LinkedList<Phone>();
        this.rawContactId=id;
    }


    public void addPhone(Phone phone){
        this.phoneLinkedList.add(phone);
    }

    public void getPhone(int pos){
        this.phoneLinkedList.get(pos);
    }

    public LinkedList<Phone> getPhoneList(){
        return this.phoneLinkedList;

    }

    public int getRawContactId(){
        return this.rawContactId;
    }


}
