package com.example.zenon.myapplicationtest;

/**
 * Created by zenon on 03/10/14.
 */
public class Phone {

    private String phoneNumber;
    private int type;

    final private int TYPE_HOME=11;
    final private int TYPE_MOBILE=2;
    final private int TYPE_OTHER=7;
    final private int TYPE_WORK=3;
    final private int TYPE_WORK_MOBILE=17;


    public Phone(String phone,int type){

        this.phoneNumber=phone;
        this.type=type;
    }


    public int getType(){
        return this.type;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phone){
        this.phoneNumber=phone;
    }

    public void setType(int type){
        this.type=type;
    }
}
