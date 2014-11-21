package com.example.zenon.myapplicationtest;

/**
 * Created by zenon on 01/10/14.
 */
public class SpinnerEntry {

    private final int contactId;

    private final String contactName;

    public SpinnerEntry(int contactID, String contactName){

        this.contactId = contactID;
        this.contactName = contactName;
    }

    public int  getContactId()
    {
        return contactId;
    }

    public String getContactName()
    {
        if(contactName!=null)

            return contactName;
        return "UNDEFINED";

    }
}
