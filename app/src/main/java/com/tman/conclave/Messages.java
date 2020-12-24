package com.tman.conclave;

import java.net.MalformedURLException;
import java.net.URL;

public class Messages {
    private String name;
    private URL url;
    private String lastmsg;
    private String lasttime;

    Messages(String name) throws MalformedURLException {
        //default image url field must be there

        this.url = new URL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSLlQ9DL2jP_heI_mtZXdw8cxNdGunsejk7FQ&usqp=CAU");
        this.name = name;
    }

    Messages(String name,String url){

    }//read image url along with this

    public String getName(){
        return name;
    }

    public URL getURL() {
        return url;
    }

}
