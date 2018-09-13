/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;

import classes.Location;
import classes.QRFamily;
import classes.QRNode;
import com.google.gson.annotations.SerializedName;
import graphs.DGraphRespOption;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class DGraphNode implements Cloneable {
    @SerializedName("@id")  
    public String id;
     
    public String 
            next, 
            message, //default message to user
            fb_answer, tg_answer, //json format when required
            response, //used like user response and system response
            event;
    public int required = 0;//1-text, 2-location, 3-one of respOption
    public boolean waiting=false;
    public Location loc;
    public DGraphRespOption[] respOptions;//options for either user or system response
    public QRFamily quickReply;
    public JSONObject obj;
    
    
    public DGraphNode clone() throws CloneNotSupportedException {
        return (DGraphNode)super.clone();
    }
                
    
    public void addSpecialButtons(int mode){
        if (quickReply == null){
                quickReply = new QRFamily();
                quickReply.title = "";
        }        
        
        if (mode == 1){//back and finish            
            quickReply.children.add(new QRNode("⬅️ Назад","command_back",2));            
            quickReply.children.add(new QRNode("❎ Завершить","command_finish",2));            
        } else if (mode == 2){ //skip            
            quickReply.children.add(new QRNode("⏭️ Пропустить","command_skip",2));            
            quickReply.children.add(new QRNode("⬅️ Назад","command_back",2));            
            quickReply.children.add(new QRNode("❎ Завершить","command_finish",2));            
        } else if (mode == 3){//finish only
            quickReply.children.add(new QRNode("❎ Завершить","command_finish",2));            
        } else if (mode == 4){//back only
            quickReply.children.add(new QRNode("⬅️ Назад","command_back",2));            
        }
    }
    
    public void onIncorrect(){
        //if (quickReply!=null && quickReply.oninc_title!=null) quickReply.title = quickReply.oninc_title;
    }        
    
}

