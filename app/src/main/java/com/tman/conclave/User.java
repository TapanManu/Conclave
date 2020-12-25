package com.tman.conclave;

import java.net.MalformedURLException;
import java.net.URL;

public class User {
    private String id;
    private String username;
    private String email;
    private String imageURL;
    private String status;
    private String lastmsg;
    private String lasttime;

    User(){

    }

    User(String id,String username,String email)  {
        //default image url field must be there
        this.id = id;
        this.username = username;
        this.lastmsg = "No Chats";      //by default display
        this.email = email;
    }

   /* User(String name,String email,String url){

    }//read image url along with this
*/
    public String getId(){ return id; }

    public String getName(){
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() { return status; }

    void setStatus(String value){
        status = value;
    }

}
