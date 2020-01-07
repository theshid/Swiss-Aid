package com.shid.swissaid.Model;

public class Chat {
    private String sender;
    private String receiver;
    private  String message;
    private boolean isseen;
    private String time;
    private String type;
    private String docName;
    private String sentImagePath;
    private double latitude;
    private double longitude;

    public Chat(String sender, String receiver, String message,boolean isseen,String time, String type,String docName
    ,double latitude, double longitude, String sentImagePath) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.time = time;
        this.type = type;
        this.docName = docName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sentImagePath = sentImagePath;
    }

    public Chat() {
    }

    public String getSentImagePath() {
        return sentImagePath;
    }

    public void setSentImagePath(String sentImagePath) {
        this.sentImagePath = sentImagePath;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
