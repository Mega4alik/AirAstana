/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package global;

import classes.User;
import classes.UserRate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dispatch.DispatchInfo;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.json.JSONObject;
/**
 *
 * @author admin
 */

public class DB {
    Global g = new Global();
    Connection db;
    Statement stmt;
    Gson gson = new Gson();
    
    public static void main(String args[]) throws Exception{
        DB dbQ = new DB();        
        //System.out.println(dbQ.gson.toJson(dbQ.getUser(1)));
        dbQ.getUserIdByFBId("832122653555005");
    }
    
    public DB(){
        try {
            db = Conn.ConnectMain();
            stmt = db.createStatement();
        } catch(Exception e){}
    }
    
    public void finish(){
        try{            
            if (stmt!=null) stmt.close();
            if (db!=null) db.close();
            stmt = null;
            db = null;
        } catch(Exception e){e.printStackTrace();}
    }    
    
    
    public User getUser(int id) throws SQLException{
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE user_id = '"+id+"' LIMIT 1");
        if (rs.next()){
            User u = new User();            
            u.id = rs.getInt("user_id");
            u.fb_id = rs.getString("fb_id");
            try{
                if (rs.getString("fb_user_info")!=null){
                    JSONObject o = new JSONObject(rs.getString("fb_user_info"));
                    u.name = o.getString("first_name");
                } else if (rs.getString("telegram_user_info")!=null){
                    JSONObject o = new JSONObject(rs.getString("telegram_user_info"));                    
                    u.name = (o.has("firstName") ? o.getString("firstName"):"")
                            +(o.has("lastName") ? " "+o.getString("lastName"):"");
                }                
            } catch(Exception e){}
            try{                
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");                
                if (rs.getString("operator_moved_at")!=null) u.lastMovedToOperator = format.parse(rs.getString("operator_moved_at"));
            } catch(Exception e){e.printStackTrace();}
            try{
                Type tt = new TypeToken<ArrayList<UserRate>>(){}.getType();
                if (rs.getString("user_rates")!=null) u.rates = gson.fromJson(rs.getString("user_rates"), tt);
            } catch(Exception e){}
            return u;
        }   
        return null;
    }
    
    public List<User> getAllUsers() throws Exception{
        List<User> ans = new ArrayList<User>();
        ResultSet rs = stmt.executeQuery("SELECT user_id FROM users");
        if (rs.next()){
            ans.add(getUser(rs.getInt("user_id")));
        }
        return ans;
    }
    
    public void addUserRate(User u, int rate, String feedback) throws SQLException {
        if (u.rates == null) u.rates = new ArrayList<UserRate>();
        u.rates.add(new UserRate(rate,feedback,g.getCurrDateInStandard()));        
        stmt.execute(String.format("UPDATE users SET user_rates='%s' WHERE user_id=%s", gson.toJson(u.rates), u.id));        
    }
    
    public void OperatorMovedSet(int user_id) throws SQLException{
        String dateSt = g.getCurrDateIn("yyyy-MM-dd HH:mm:ss");        
        stmt.execute(String.format("UPDATE users SET operator_moved_at='%s' WHERE user_id=%s", dateSt, user_id));        
    }
    
    public String[] getActiveOperatorsFBIds() throws Exception{
        ArrayList<String> fbIds = new ArrayList<String>();
        ResultSet rs = stmt.executeQuery("SELECT fb_id FROM users WHERE user_status='admin-active'");        
        while(rs.next()){
            fbIds.add(rs.getString("fb_id"));
        }        
        return fbIds.toArray(new String[fbIds.size()]);
    }
        
    public int getUserIdByFBId(String sender_id) throws Exception{
        ResultSet rs = stmt.executeQuery("SELECT user_id FROM users WHERE fb_id = '"+sender_id+"' LIMIT 1");
        if (rs.next()){
            return rs.getInt("user_id");
        } else {          
          String fbUserInfo = g.getURLContent("https://graph.facebook.com/v2.6/"+sender_id+"?fields=first_name,last_name,profile_pic,locale,timezone,gender&access_token="+g.fbToken);
          try{
            JSONObject o = new JSONObject(fbUserInfo);
            String first_name = g.UnicodeToUTF8(o.getString("first_name"));
            String last_name = g.UnicodeToUTF8(o.getString("last_name"));                  
            fbUserInfo = o.toString();
          } catch(Exception e){}          
          System.out.println(fbUserInfo);          
          stmt.executeUpdate("INSERT INTO users(fb_id,fb_user_info) VALUES('"+sender_id+"', '"+fbUserInfo+"')");
          rs = stmt.executeQuery("SELECT user_id FROM users WHERE fb_id = '"+sender_id+"' LIMIT 1");
          if (rs.next()) {
              int user_id = rs.getInt("user_id");              
              return user_id;
          }                   
        }
        return 0;
    }
    
    public int getUserIdByTelegramId(String chatId, String userId, String userName, String firstName, String lastName) throws Exception{        
        ResultSet rs = stmt.executeQuery("SELECT user_id FROM users WHERE telegram_id = '"+chatId+"' LIMIT 1");
        if (rs.next()){
            return rs.getInt("user_id");
        } else { 
            JSONObject o = new JSONObject();
            o.put("userId",userId);
            o.put("userName", userName);
            o.put("firstName", firstName);//g.ISOToUTF8(firstName)
            o.put("lastName", lastName); //g.ISOToUTF8(lastName)
            stmt.executeUpdate("INSERT INTO users(telegram_id,telegram_user_info) VALUES('"+chatId+"','"+o.toString()+"')");
            rs = stmt.executeQuery("SELECT user_id FROM users WHERE telegram_id = '"+chatId+"' LIMIT 1");
            if (rs.next()) {
                int user_id = rs.getInt("user_id");              
                return user_id;
            }              
        }
        return 0;
    }  
    
    public int getUserIdByWPId(String wp_id) throws Exception{        
        ResultSet rs = stmt.executeQuery("SELECT user_id FROM users WHERE wp_id = '"+wp_id+"' LIMIT 1");
        if (rs.next()){
            return rs.getInt("user_id");
        } else {  
            stmt.executeUpdate("INSERT INTO users(wp_id) VALUES('"+wp_id+"')");
            rs = stmt.executeQuery("SELECT user_id FROM users WHERE wp_id = '"+wp_id+"' LIMIT 1");
            if (rs.next()) {
                int user_id = rs.getInt("user_id");              
                return user_id;
            }              
        }
        return 0;
    }     
    
    public int getUserIdBySkypeId(String skypeId, String name) throws Exception{        
        ResultSet rs = stmt.executeQuery("SELECT user_id FROM users WHERE skype_id = '"+skypeId+"' LIMIT 1");
        if (rs.next()){
            return rs.getInt("user_id");
        } else { 
            JSONObject o = new JSONObject();
            //o.put("userId",userId);
            o.put("name", name);            
            stmt.executeUpdate("INSERT INTO users(skype_id, skype_user_info) VALUES('"+skypeId+"','"+o.toString()+"')");
            rs = stmt.executeQuery("SELECT user_id FROM users WHERE skype_id = '"+skypeId+"' LIMIT 1");
            if (rs.next()) {
                int user_id = rs.getInt("user_id");              
                return user_id;
            }              
        }
        return 0;
    }      

    
    public void addIntentRecommendation(String intent,double prob) throws Exception{
        try{
            System.out.println("addIntentRecommendation - "+intent);
            PreparedStatement pstmt = db.prepareStatement("INSERT INTO intentRecommendations(intent,prob) VALUES(?,?)");
            pstmt.setString(1, intent);
            pstmt.setDouble(2,prob);
            pstmt.execute();
            pstmt.close();
        } catch(Exception e){}
    }
        
    
    public List<DispatchInfo> getALLDispatchInfos() throws Exception{
        List<DispatchInfo> ans = new ArrayList<DispatchInfo>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM dispatches");        
        while(rs.next()){
            DispatchInfo q = gson.fromJson(rs.getString("info"), DispatchInfo.class);
            q.id = rs.getInt("id");            
            ans.add(q);
        }
        rs.close();
        return ans;
    }    
    
        
    public String getImageID(String path) throws SQLException{
        String ans = null;
        ResultSet rs = stmt.executeQuery("SELECT id FROM images WHERE path='"+path+"'");        
        if (rs.next()){
            ans = Integer.toString(rs.getInt("id"));
        }
        rs.close();
        return ans;
    }
    
    public String InsertNewImage(String path) throws SQLException{
        String ans = null;
        stmt.executeUpdate("INSERT INTO images(path) VALUES('"+path+"')");
        ResultSet rs = stmt.executeQuery("SELECT id FROM images WHERE path='"+path+"'");
        if (rs.next()) {
            ans = Integer.toString(rs.getInt("id"));                  
        }
        rs.close();
        return ans;
    }
    
    
}


