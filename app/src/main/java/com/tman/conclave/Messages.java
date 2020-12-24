package com.tman.conclave;

public class Messages {
    private String name;
    private String url;
    private String lastmsg;
    private String lasttime;

    Messages(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getURL() {
        return url;
    }

}
