package com.thilojaeggi.tchat;

import java.io.Serializable;

public class MessageModel implements Serializable {

    private String name;
    private String message;
    private String deviceId;
    private String imageurl;
    public MessageModel(String name, String message, String deviceId, String imageurl) {
        this.name = name;
        this.imageurl = imageurl;
        this.deviceId = deviceId;
        this.message = message;
    }

    public MessageModel() {
    }
    public String getImageurl(){
        return imageurl;
    }
    public void setImageurl (String imageurl){
        this.imageurl = imageurl;
    }
    public String getName() {
        return name;
    }
    public String getDeviceId(){
        return deviceId;
    }
    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setName(String name) {
        this.name = name;
    }
}