package com.example.zenon.myapplicationtest;

import java.util.EventObject;

/**
 * Created by zenon on 07/11/14.
 */
public class MovePageEvent extends EventObject {

    public int typeEvent;

    public MovePageEvent(Object source,int type){
        super(source);
        typeEvent = type;
    }


}
