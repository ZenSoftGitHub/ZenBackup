package com.example.zenon.myapplicationtest;

/**
 * Created by zenon on 01/10/14.
 */
public class ListViewEntry {

    // Phone number or email address
    private final String dataEntry;

    // Describing the type of the entry (Home,  Pro ...)
    private final int typeResource;

    // Resource which is used as a label of the entry
    private final int entryLabelResource;

    public ListViewEntry(String data, int type, int entryLabel)
    {
        this.dataEntry = data;
        this.typeResource=type;
        this.entryLabelResource =entryLabel;
    }

    public String getDataEntry()
    {
        return dataEntry;
    }

    public int getTypeResource(){
        return typeResource;
    }

    public int getEntryLabelResource()
    {
        return entryLabelResource;
    }
}
