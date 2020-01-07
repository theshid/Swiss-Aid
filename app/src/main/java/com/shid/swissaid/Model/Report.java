package com.shid.swissaid.Model;

public class Report {

    private String name;
    private String url;
    private String language;

    public Report() {
    }

    public Report(String name, String url, String language) {
        this.name = name;
        this.url = url;
        this.language = language;
    }



    public Report(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
