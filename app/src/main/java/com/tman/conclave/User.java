package com.tman.conclave;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class User {
    private String id;
    private String username;
    private String email;
    private Uri imageURL;
    private String status;
    private String lastmsg;
    private String lasttime;

    User(){

    }

    User(String id,String username,String email,Uri imageURL)  {
        //default image url field must be there
        this.id = id;
        this.username = username;
        this.lastmsg = "No Chats";      //by default display
        this.email = email;
        this.status = "offline";        //default status
        this.imageURL = imageURL;
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

    public Uri getImageURL() { return imageURL; }

    public String getStatus() { return status; }

    public void setStatus(String value){
        status = value;
    }
    public void setImageURL(Uri value){ imageURL = value; }

    public void setLastmsg(String msg){
        lastmsg = msg;
    }

    public void setLasttime(String time){
        lasttime = time;
    }

    public String getLastmsg(){ return lastmsg; }
    public String getLasttime(){ return lasttime; }



}
