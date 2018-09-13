/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class GenericElement {
    public String title,subtitle;
    public ArrayList<GenericElementButton> buttons = new ArrayList<GenericElementButton>();
    
    public void addWebButton(String url, String title){
        buttons.add(new GenericElementButton("web_url",url,null,title));
    }
    
    public void addPostbackButton(){
        
    }
}

class GenericElementButton{
    String type,url,payload,title;
    GenericElementButton(String type,String url,String payload,String title){
        this.type = type;
        this.url = url;
        this.payload = payload;
        this.title = title;
    }
    
}
