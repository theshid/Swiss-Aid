package com.shid.swissaid.Model;

public class Upload {

    private String name;
    private String url;
    private String id;
    private String mission;
    private String name_employee;
    private String numero_ta;
    private String time;
    private User user;



    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Upload(User user,String name, String url, String id, String mission, String name_employee, String numero_ta, String time) {
        this.name = name;
        this.url = url;
        this.id = id;
        this.mission = mission;
        this.name_employee = name_employee;
        this.numero_ta = numero_ta;
        this.time = time;

    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public String getMission() {
        return mission;
    }

    public String getName_employee() {
        return name_employee;
    }

    public String getNumero_ta() {
        return numero_ta;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public void setName_employee(String name_employee) {
        this.name_employee = name_employee;
    }

    public void setNumero_ta(String numero_ta) {
        this.numero_ta = numero_ta;
    }
}
