package com.example.niket.chatapplication.pojoClass;

/**
 * Created by Niket on 1/23/2018.
 */

public class MyPojo {
    private String ID;
    private String Name;
    private String DOB;
    private String Mobile;
    private String Email;
    private String password;
    private String Image_URL;
    private String online;
    private String timeStamp;
    private String registerTimeStamp;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    public String getRegisterTimeStamp() {
        return registerTimeStamp;
    }

    public void setRegisterTimeStamp(String registerTimeStamp) {
        this.registerTimeStamp = registerTimeStamp;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage_URL() {
        return Image_URL;
    }

    public void setImage_URL(String image_URL) {
        Image_URL = image_URL;
    }
}
