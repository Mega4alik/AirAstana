/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;


import classes.DNode;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONObject;

public class Track{
    Connection db;
    Statement stmt;
    PreparedStatement pstmt;
    Gson gson = new Gson();
    
    public Track() throws Exception{
        this.db = Conn.ConnectMain();
        this.stmt = db.createStatement();
        pstmt = db.prepareStatement("INSERT INTO history(user_id, event, props, operator_id) VALUES(?,?,?,0)");
    }
    
    public void finish(){
        try{
            if (stmt!=null) stmt.close();
            if (pstmt!=null) pstmt.close();
            if (db!=null) db.close();            
            stmt = null;
            pstmt = null;
            db = null;
        } catch(Exception e){e.printStackTrace();}
    }    
    
    public void MovedToOperator(int user_id) throws Exception {                                
        stmt.execute("INSERT INTO history(user_id, event) VALUES("+user_id+", 'MovedToOperator')");                        
    }
    
    public void UserText(String text, int user_id, DNode node){        
        try{
            JSONObject res = new JSONObject();
            res.put("r", node.r);
            res.put("question", node.question);
            res.put("answer", node.r == 5 ? "CurrencyToday" : node.answer);
            res.put("quickReply",gson.toJson(node.quickReply));
            String ResultJson = gson.toJson(res);
            JSONObject props = new JSONObject();            
            props.put("text",text);            
            props.put("result", res);                   
            pstmt.setInt(1, user_id);
            pstmt.setString(2, "BotMessage");
            pstmt.setString(3, props.toString());        
            //pstmt.execute();
        } catch(Exception e){}
    }
    
    public void UserLocation(double lat, double lng, int user_id, DNode res){        
        try{
            String ResultJson = gson.toJson(res);
            JSONObject props = new JSONObject();            
            props.put("lat",lat);
            props.put("lng",lng);
            props.put("result", ResultJson);        
            pstmt.setInt(1, user_id);
            pstmt.setString(2, "UserLocation");
            pstmt.setString(3, props.toString());        
            //pstmt.execute();
        } catch(Exception e){}
    }
    
    public void QuickReplied(int user_id, String type, String reply, String sid, DNode node){        
        try{                                               
            JSONObject props = new JSONObject();            
            props.put("type",type);
            props.put("reply",reply);
            props.put("qid", sid);        
            JSONObject res = new JSONObject();
            res.put("quickReply", gson.toJson(node.quickReply));
            props.put("result",res);                
            pstmt.setInt(1, user_id);
            pstmt.setString(2, "QuickReplied");
            pstmt.setString(3, props.toString());
            //pstmt.execute();
        } catch(Exception e){}
    }
        
    public void dispatchResponsed(int user_id, int dispatch_id, int response_idx){        
        try {                                               
            JSONObject props = new JSONObject();            
            props.put("dispatch_id",dispatch_id);
            props.put("response_idx", response_idx);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, "DispatchResponse");
            pstmt.setString(3, props.toString());
            pstmt.execute();
        } catch(Exception e){}
    }    
    
    public void UserRate(int user_id, int rate, String feedback){
        try{
            JSONObject props = new JSONObject();
            props.put("rate",rate);
            props.put("feedback",feedback);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, "UserRate");
            pstmt.setString(3, props.toString());
            pstmt.execute();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

/*

import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.json.JSONObject;
public class Track {
    MessageBuilder messageBuilder = new MessageBuilder("f1bc56f5448edce78e88b2ec72f33352");//6845e65a43fbc9ec1a6830c94f01fd1b
    MixpanelAPI mixpanel = new MixpanelAPI();
    public void MovedToOperator(int user_id) throws Exception{        
        JSONObject props = new JSONObject();
        //props.put("Gender", "Female");
        JSONObject planEvent = messageBuilder.event(user_id+"", "MovedToOperator", props);
        ClientDelivery delivery = new ClientDelivery();    
        delivery.addMessage(planEvent);            
        mixpanel.deliver(delivery);                 
    }
    
    public void UserText(String text, int user_id, String ResultJson) throws Exception{        
        JSONObject props = new JSONObject();
        props.put("type","text");
        props.put("text",text);
        props.put("result", ResultJson);
        JSONObject event = messageBuilder.event(user_id+"", "UserMessage", props);
        ClientDelivery delivery = new ClientDelivery();    
        delivery.addMessage(event);            
        mixpanel.deliver(delivery);          
    }
    
    public void UserRate(int user_id, int rate, String feedback) throws Exception{
        JSONObject props = new JSONObject();
        props.put("rate",rate);
        props.put("feedback",feedback);
        JSONObject event = messageBuilder.event(user_id+"", "UserRate", props);
        ClientDelivery delivery = new ClientDelivery();    
        delivery.addMessage(event);            
        mixpanel.deliver(delivery); 
    }
}
*/