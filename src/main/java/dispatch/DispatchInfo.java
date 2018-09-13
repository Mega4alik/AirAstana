/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dispatch;

/**
 *
 * @author admin
 */
public class DispatchInfo {
    public int id = 0;
    public DispatchMessage default_message;
    public String[] responses;
    public String final_message = "";
    public String getDefaultMessageContent(){
        return default_message.content;
    }
}

class DispatchMessage {
    public String content = "", type="text";
    
}
